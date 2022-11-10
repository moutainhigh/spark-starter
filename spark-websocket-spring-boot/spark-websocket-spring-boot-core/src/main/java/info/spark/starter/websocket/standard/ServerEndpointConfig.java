package info.spark.starter.websocket.standard;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:24
 * @since 2022.1.1
 */
@Slf4j
@SuppressWarnings("all")
public class ServerEndpointConfig {

    /** randomPort */
    private static Integer randomPort;
    /** Host */
    private final String HOST;
    /** Port */
    private final int PORT;
    /** Boss loop group threads */
    private final int BOSS_LOOP_GROUP_THREADS;
    /** Worker loop group threads */
    private final int WORKER_LOOP_GROUP_THREADS;
    /** Use compression handler */
    private final boolean USE_COMPRESSION_HANDLER;
    /** Connect timeout millis */
    private final int CONNECT_TIMEOUT_MILLIS;
    /** So backlog */
    private final int SO_BACKLOG;
    /** Write spin count */
    private final int WRITE_SPIN_COUNT;
    /** Write buffer high water mark */
    private final int WRITE_BUFFER_HIGH_WATER_MARK;
    /** Write buffer low water mark */
    private final int WRITE_BUFFER_LOW_WATER_MARK;
    /** So rcvbuf */
    private final int SO_RCVBUF;
    /** So sndbuf */
    private final int SO_SNDBUF;
    /** Tcp nodelay */
    private final boolean TCP_NODELAY;
    /** So keepalive */
    private final boolean SO_KEEPALIVE;
    /** So linger */
    private final int SO_LINGER;
    /** Allow half closure */
    private final boolean ALLOW_HALF_CLOSURE;
    /** Reader idle time seconds */
    private final int READER_IDLE_TIME_SECONDS;
    /** Writer idle time seconds */
    private final int WRITER_IDLE_TIME_SECONDS;
    /** All idle time seconds */
    private final int ALL_IDLE_TIME_SECONDS;
    /** Max frame payload length */
    private final int MAX_FRAME_PAYLOAD_LENGTH;
    /** Use event executor group */
    private final boolean USE_EVENT_EXECUTOR_GROUP;
    /** Event executor group threads */
    private final int EVENT_EXECUTOR_GROUP_THREADS;
    /** Key password */
    private final String KEY_PASSWORD;
    /** Key store */
    private final String KEY_STORE;
    /** Key store password */
    private final String KEY_STORE_PASSWORD;
    /** Key store type */
    private final String KEY_STORE_TYPE;
    /** Trust store */
    private final String TRUST_STORE;
    /** Trust store password */
    private final String TRUST_STORE_PASSWORD;
    /** Trust store type */
    private final String TRUST_STORE_TYPE;
    /** Cors origins */
    private final String[] CORS_ORIGINS;
    /** Cors allow credentials */
    private final Boolean CORS_ALLOW_CREDENTIALS;

    /**
     * Server endpoint config
     *
     * @param host                      host
     * @param port                      port
     * @param bossLoopGroupThreads      boss loop group threads
     * @param workerLoopGroupThreads    worker loop group threads
     * @param useCompressionHandler     use compression handler
     * @param connectTimeoutMillis      connect timeout millis
     * @param soBacklog                 so backlog
     * @param writeSpinCount            write spin count
     * @param writeBufferHighWaterMark  write buffer high water mark
     * @param writeBufferLowWaterMark   write buffer low water mark
     * @param soRcvbuf                  so rcvbuf
     * @param soSndbuf                  so sndbuf
     * @param tcpNodelay                tcp nodelay
     * @param soKeepalive               so keepalive
     * @param soLinger                  so linger
     * @param allowHalfClosure          allow half closure
     * @param readerIdleTimeSeconds     reader idle time seconds
     * @param writerIdleTimeSeconds     writer idle time seconds
     * @param allIdleTimeSeconds        all idle time seconds
     * @param maxFramePayloadLength     max frame payload length
     * @param useEventExecutorGroup     use event executor group
     * @param eventExecutorGroupThreads event executor group threads
     * @param keyPassword               key password
     * @param keyStore                  key store
     * @param keyStorePassword          key store password
     * @param keyStoreType              key store type
     * @param trustStore                trust store
     * @param trustStorePassword        trust store password
     * @param trustStoreType            trust store type
     * @param corsOrigins               cors origins
     * @param corsAllowCredentials      cors allow credentials
     * @since 2022.1.1
     */
    public ServerEndpointConfig(String host,
                                int port,
                                int bossLoopGroupThreads,
                                int workerLoopGroupThreads,
                                boolean useCompressionHandler,
                                int connectTimeoutMillis,
                                int soBacklog,
                                int writeSpinCount,
                                int writeBufferHighWaterMark,
                                int writeBufferLowWaterMark,
                                int soRcvbuf,
                                int soSndbuf,
                                boolean tcpNodelay,
                                boolean soKeepalive,
                                int soLinger,
                                boolean allowHalfClosure,
                                int readerIdleTimeSeconds,
                                int writerIdleTimeSeconds,
                                int allIdleTimeSeconds,
                                int maxFramePayloadLength,
                                boolean useEventExecutorGroup,
                                int eventExecutorGroupThreads,
                                String keyPassword,
                                String keyStore,
                                String keyStorePassword,
                                String keyStoreType,
                                String trustStore,
                                String trustStorePassword,
                                String trustStoreType,
                                String[] corsOrigins,
                                Boolean corsAllowCredentials) {
        if (StringUtils.isEmpty(host) || "0.0.0.0".equals(host) || "0.0.0.0/0.0.0.0".equals(host)) {
            this.HOST = "0.0.0.0";
        } else {
            this.HOST = host;
        }

        this.PORT = getAvailablePort(port);
        this.BOSS_LOOP_GROUP_THREADS = bossLoopGroupThreads;
        this.WORKER_LOOP_GROUP_THREADS = workerLoopGroupThreads;
        this.USE_COMPRESSION_HANDLER = useCompressionHandler;
        this.CONNECT_TIMEOUT_MILLIS = connectTimeoutMillis;
        this.SO_BACKLOG = soBacklog;
        this.WRITE_SPIN_COUNT = writeSpinCount;
        this.WRITE_BUFFER_HIGH_WATER_MARK = writeBufferHighWaterMark;
        this.WRITE_BUFFER_LOW_WATER_MARK = writeBufferLowWaterMark;
        this.SO_RCVBUF = soRcvbuf;
        this.SO_SNDBUF = soSndbuf;
        this.TCP_NODELAY = tcpNodelay;
        this.SO_KEEPALIVE = soKeepalive;
        this.SO_LINGER = soLinger;
        this.ALLOW_HALF_CLOSURE = allowHalfClosure;
        this.READER_IDLE_TIME_SECONDS = readerIdleTimeSeconds;
        this.WRITER_IDLE_TIME_SECONDS = writerIdleTimeSeconds;
        this.ALL_IDLE_TIME_SECONDS = allIdleTimeSeconds;
        this.MAX_FRAME_PAYLOAD_LENGTH = maxFramePayloadLength;
        this.USE_EVENT_EXECUTOR_GROUP = useEventExecutorGroup;
        this.EVENT_EXECUTOR_GROUP_THREADS = eventExecutorGroupThreads;

        this.KEY_PASSWORD = keyPassword;
        this.KEY_STORE = keyStore;
        this.KEY_STORE_PASSWORD = keyStorePassword;
        this.KEY_STORE_TYPE = keyStoreType;
        this.TRUST_STORE = trustStore;
        this.TRUST_STORE_PASSWORD = trustStorePassword;
        this.TRUST_STORE_TYPE = trustStoreType;

        this.CORS_ORIGINS = corsOrigins;
        this.CORS_ALLOW_CREDENTIALS = corsAllowCredentials;
    }

    /**
     * Gets available port *
     *
     * @param port port
     * @return the available port
     * @since 2022.1.1
     */
    private int getAvailablePort(int port) {
        if (port != 0) {
            return port;
        }
        if (randomPort != null && randomPort != 0) {
            return randomPort;
        }
        InetSocketAddress inetSocketAddress = new InetSocketAddress(0);
        Socket socket = new Socket();
        try {
            socket.bind(inetSocketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int localPort = socket.getLocalPort();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        randomPort = localPort;
        return localPort;
    }

    /**
     * Gets random port *
     *
     * @return the random port
     * @since 2022.1.1
     */
    public static Integer getRandomPort() {
        return randomPort;
    }

    /**
     * Gets host *
     *
     * @return the host
     * @since 2022.1.1
     */
    public String getHost() {
        return HOST;
    }

    /*public Set<String> getPathSet() {

        return PATH_SET;
    }*/

    /**
     * Gets port *
     *
     * @return the port
     * @since 2022.1.1
     */
    public int getPort() {
        return PORT;
    }

    /**
     * Gets boss loop group threads *
     *
     * @return the boss loop group threads
     * @since 2022.1.1
     */
    public int getBossLoopGroupThreads() {
        return BOSS_LOOP_GROUP_THREADS;
    }

    /**
     * Gets worker loop group threads *
     *
     * @return the worker loop group threads
     * @since 2022.1.1
     */
    public int getWorkerLoopGroupThreads() {
        return WORKER_LOOP_GROUP_THREADS;
    }

    /**
     * Is use compression handler
     *
     * @return the boolean
     * @since 2022.1.1
     */
    public boolean isUseCompressionHandler() {
        return USE_COMPRESSION_HANDLER;
    }

    /**
     * Gets connect timeout millis *
     *
     * @return the connect timeout millis
     * @since 2022.1.1
     */
    public int getConnectTimeoutMillis() {
        return CONNECT_TIMEOUT_MILLIS;
    }

    /**
     * Gets so backlog *
     *
     * @return the so backlog
     * @since 2022.1.1
     */
    public int getSoBacklog() {
        return SO_BACKLOG;
    }

    /**
     * Gets write spin count *
     *
     * @return the write spin count
     * @since 2022.1.1
     */
    public int getWriteSpinCount() {
        return WRITE_SPIN_COUNT;
    }

    /**
     * Gets write buffer high water mark *
     *
     * @return the write buffer high water mark
     * @since 2022.1.1
     */
    public int getWriteBufferHighWaterMark() {
        return WRITE_BUFFER_HIGH_WATER_MARK;
    }

    /**
     * Gets write buffer low water mark *
     *
     * @return the write buffer low water mark
     * @since 2022.1.1
     */
    public int getWriteBufferLowWaterMark() {
        return WRITE_BUFFER_LOW_WATER_MARK;
    }

    /**
     * Gets so rcvbuf *
     *
     * @return the so rcvbuf
     * @since 2022.1.1
     */
    public int getSoRcvbuf() {
        return SO_RCVBUF;
    }

    /**
     * Gets so sndbuf *
     *
     * @return the so sndbuf
     * @since 2022.1.1
     */
    public int getSoSndbuf() {
        return SO_SNDBUF;
    }

    /**
     * Is tcp nodelay
     *
     * @return the boolean
     * @since 2022.1.1
     */
    public boolean isTcpNodelay() {
        return TCP_NODELAY;
    }

    /**
     * Is so keepalive
     *
     * @return the boolean
     * @since 2022.1.1
     */
    public boolean isSoKeepalive() {
        return SO_KEEPALIVE;
    }

    /**
     * Gets so linger *
     *
     * @return the so linger
     * @since 2022.1.1
     */
    public int getSoLinger() {
        return SO_LINGER;
    }

    /**
     * Is allow half closure
     *
     * @return the boolean
     * @since 2022.1.1
     */
    public boolean isAllowHalfClosure() {
        return ALLOW_HALF_CLOSURE;
    }

    /**
     * Gets reader idle time seconds *
     *
     * @return the reader idle time seconds
     * @since 2022.1.1
     */
    public int getReaderIdleTimeSeconds() {
        return READER_IDLE_TIME_SECONDS;
    }

    /**
     * Gets writer idle time seconds *
     *
     * @return the writer idle time seconds
     * @since 2022.1.1
     */
    public int getWriterIdleTimeSeconds() {
        return WRITER_IDLE_TIME_SECONDS;
    }

    /**
     * Gets all idle time seconds *
     *
     * @return the all idle time seconds
     * @since 2022.1.1
     */
    public int getAllIdleTimeSeconds() {
        return ALL_IDLE_TIME_SECONDS;
    }

    /**
     * Gets frame payload length *
     *
     * @return the frame payload length
     * @since 2022.1.1
     */
    public int getmaxFramePayloadLength() {
        return MAX_FRAME_PAYLOAD_LENGTH;
    }

    /**
     * Is use event executor group
     *
     * @return the boolean
     * @since 2022.1.1
     */
    public boolean isUseEventExecutorGroup() {
        return USE_EVENT_EXECUTOR_GROUP;
    }

    /**
     * Gets event executor group threads *
     *
     * @return the event executor group threads
     * @since 2022.1.1
     */
    public int getEventExecutorGroupThreads() {
        return EVENT_EXECUTOR_GROUP_THREADS;
    }

    /**
     * Gets key password *
     *
     * @return the key password
     * @since 2022.1.1
     */
    public String getKeyPassword() {
        return KEY_PASSWORD;
    }

    /**
     * Gets key store *
     *
     * @return the key store
     * @since 2022.1.1
     */
    public String getKeyStore() {
        return KEY_STORE;
    }

    /**
     * Gets key store password *
     *
     * @return the key store password
     * @since 2022.1.1
     */
    public String getKeyStorePassword() {
        return KEY_STORE_PASSWORD;
    }

    /**
     * Gets key store type *
     *
     * @return the key store type
     * @since 2022.1.1
     */
    public String getKeyStoreType() {
        return KEY_STORE_TYPE;
    }

    /**
     * Gets trust store *
     *
     * @return the trust store
     * @since 2022.1.1
     */
    public String getTrustStore() {
        return TRUST_STORE;
    }

    /**
     * Gets trust store password *
     *
     * @return the trust store password
     * @since 2022.1.1
     */
    public String getTrustStorePassword() {
        return TRUST_STORE_PASSWORD;
    }

    /**
     * Gets trust store type *
     *
     * @return the trust store type
     * @since 2022.1.1
     */
    public String getTrustStoreType() {
        return TRUST_STORE_TYPE;
    }

    /**
     * Get cors origins
     *
     * @return the string [ ]
     * @since 2022.1.1
     */
    public String[] getCorsOrigins() {
        return CORS_ORIGINS;
    }

    /**
     * Gets cors allow credentials *
     *
     * @return the cors allow credentials
     * @since 2022.1.1
     */
    public Boolean getCorsAllowCredentials() {
        return CORS_ALLOW_CREDENTIALS;
    }
}
