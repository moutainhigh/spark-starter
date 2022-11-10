package info.spark.starter.websocket.standard;

import info.spark.starter.websocket.pojo.PojoEndpointServer;
import info.spark.starter.websocket.util.SslUtils;

import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:06
 * @since 2022.1.1
 */
@Slf4j
@SuppressWarnings("all")
public class WebsocketServer {

    /** Pojo endpoint server */
    private final PojoEndpointServer pojoEndpointServer;
    /** Config */
    private final ServerEndpointConfig config;

    /**
     * Websocket server
     *
     * @param webSocketServerHandler web socket server handler
     * @param serverEndpointConfig   server endpoint config
     * @since 2022.1.1
     */
    public WebsocketServer(PojoEndpointServer webSocketServerHandler, ServerEndpointConfig serverEndpointConfig) {
        this.pojoEndpointServer = webSocketServerHandler;
        this.config = serverEndpointConfig;

    }

    /**
     * Init
     *
     * @throws InterruptedException interrupted exception
     * @throws SSLException         ssl exception
     * @since 2022.1.1
     */
    public void init() throws InterruptedException, SSLException {
        EventExecutorGroup eventExecutorGroup = null;
        final SslContext sslCtx;
        if (!StringUtils.isEmpty(config.getKeyStore())) {
            sslCtx = SslUtils.createSslContext(config.getKeyPassword(), config.getKeyStore(), config.getKeyStoreType(),
                                               config.getKeyStorePassword(), config.getTrustStore(), config.getTrustStoreType(),
                                               config.getTrustStorePassword());
        } else {
            sslCtx = null;
        }
        String[] corsOrigins = config.getCorsOrigins();
        Boolean corsAllowCredentials = config.getCorsAllowCredentials();
        final CorsConfig corsConfig = createCorsConfig(corsOrigins, corsAllowCredentials);

        if (config.isUseEventExecutorGroup()) {
            eventExecutorGroup = new DefaultEventExecutorGroup(config.getEventExecutorGroupThreads() == 0 ? 16 :
                                                               config.getEventExecutorGroupThreads());
        }
        EventLoopGroup boss = new NioEventLoopGroup(config.getBossLoopGroupThreads());
        EventLoopGroup worker = new NioEventLoopGroup(config.getWorkerLoopGroupThreads());
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventExecutorGroup finalEventExecutorGroup = eventExecutorGroup;
        bootstrap.group(boss, worker)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMillis())
            .option(ChannelOption.SO_BACKLOG, config.getSoBacklog())
            .childOption(ChannelOption.WRITE_SPIN_COUNT, config.getWriteSpinCount())
            .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(config.getWriteBufferLowWaterMark(),
                                                                                         config.getWriteBufferHighWaterMark()))
            .childOption(ChannelOption.TCP_NODELAY, config.isTcpNodelay())
            .childOption(ChannelOption.SO_KEEPALIVE, config.isSoKeepalive())
            .childOption(ChannelOption.SO_LINGER, config.getSoLinger())
            .childOption(ChannelOption.ALLOW_HALF_CLOSURE, config.isAllowHalfClosure())
            .handler(new LoggingHandler(LogLevel.DEBUG))
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    if (sslCtx != null) {
                        pipeline.addFirst(sslCtx.newHandler(ch.alloc()));
                    }
                    pipeline.addLast(new HttpServerCodec());
                    pipeline.addLast(new HttpObjectAggregator(65536));
                    if (corsConfig != null) {
                        pipeline.addLast(new CorsHandler(corsConfig));
                    }
                    pipeline.addLast(new HttpServerHandler(pojoEndpointServer, config, finalEventExecutorGroup, corsConfig != null));
                }
            });

        if (config.getSoRcvbuf() != -1) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, config.getSoRcvbuf());
        }

        if (config.getSoSndbuf() != -1) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, config.getSoSndbuf());
        }

        ChannelFuture channelFuture;
        if ("0.0.0.0".equals(config.getHost())) {
            channelFuture = bootstrap.bind(config.getPort());
        } else {
            try {
                channelFuture = bootstrap.bind(new InetSocketAddress(InetAddress.getByName(config.getHost()), config.getPort()));
            } catch (UnknownHostException e) {
                channelFuture = bootstrap.bind(config.getHost(), config.getPort());
                log.error(e.getMessage(), e);
            }
        }

        channelFuture.addListener(future -> {
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            boss.shutdownGracefully().syncUninterruptibly();
            worker.shutdownGracefully().syncUninterruptibly();
        }));
    }

    /**
     * Create cors config
     *
     * @param corsOrigins          cors origins
     * @param corsAllowCredentials cors allow credentials
     * @return the cors config
     * @since 2022.1.1
     */
    private CorsConfig createCorsConfig(String[] corsOrigins, Boolean corsAllowCredentials) {
        if (corsOrigins.length == 0) {
            return null;
        }
        CorsConfigBuilder corsConfigBuilder = null;
        for (String corsOrigin : corsOrigins) {
            if ("*".equals(corsOrigin)) {
                corsConfigBuilder = CorsConfigBuilder.forAnyOrigin();
                break;
            }
        }
        if (corsConfigBuilder == null) {
            corsConfigBuilder = CorsConfigBuilder.forOrigins(corsOrigins);
        }
        if (corsAllowCredentials != null && corsAllowCredentials) {
            corsConfigBuilder.allowCredentials();
        }
        corsConfigBuilder.allowNullOrigin();
        return corsConfigBuilder.build();
    }

    /**
     * Gets pojo endpoint server *
     *
     * @return the pojo endpoint server
     * @since 2022.1.1
     */
    public PojoEndpointServer getPojoEndpointServer() {
        return pojoEndpointServer;
    }
}
