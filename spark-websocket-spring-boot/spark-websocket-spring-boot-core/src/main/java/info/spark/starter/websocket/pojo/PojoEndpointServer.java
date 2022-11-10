package info.spark.starter.websocket.pojo;

import info.spark.starter.websocket.standard.ServerEndpointConfig;
import info.spark.starter.websocket.support.AntPathMatcherWrapper;
import info.spark.starter.websocket.support.DefaultPathMatcher;
import info.spark.starter.websocket.support.MethodArgumentResolver;
import info.spark.starter.websocket.support.PathVariableMapMethodArgumentResolver;
import info.spark.starter.websocket.support.PathVariableMethodArgumentResolver;
import info.spark.starter.websocket.support.WsPathMatcher;

import org.springframework.beans.TypeMismatchException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:08
 * @since 2022.1.1
 */
@SuppressWarnings("all")
public class PojoEndpointServer {

    /** SESSION_KEY */
    public static final AttributeKey<Session> SESSION_KEY = AttributeKey.valueOf("WEBSOCKET_SESSION");
    /** URI_TEMPLATE */
    public static final AttributeKey<Map<String, String>> URI_TEMPLATE = AttributeKey.valueOf("WEBSOCKET_URI_TEMPLATE");
    /** REQUEST_PARAM */
    public static final AttributeKey<Map<String, List<String>>> REQUEST_PARAM = AttributeKey.valueOf("WEBSOCKET_REQUEST_PARAM");
    /** POJO_KEY */
    private static final AttributeKey<Object> POJO_KEY = AttributeKey.valueOf("WEBSOCKET_IMPLEMENT");
    /** PATH_KEY */
    private static final AttributeKey<String> PATH_KEY = AttributeKey.valueOf("WEBSOCKET_PATH");
    /** logger */
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PojoEndpointServer.class);
    /** Path method mapping map */
    private final Map<String, PojoMethodMapping> pathMethodMappingMap = new HashMap<>();
    /** Config */
    private final ServerEndpointConfig config;
    /** Path matchers */
    private final Set<WsPathMatcher> pathMatchers = new HashSet<>();

    /**
     * Pojo endpoint server
     *
     * @param methodMapping method mapping
     * @param config        config
     * @param path          path
     * @since 2022.1.1
     */
    public PojoEndpointServer(PojoMethodMapping methodMapping, ServerEndpointConfig config, String path) {
        addPathPojoMethodMapping(path, methodMapping);
        this.config = config;
    }

    /**
     * Add path pojo method mapping
     *
     * @param path              path
     * @param pojoMethodMapping pojo method mapping
     * @since 2022.1.1
     */
    public void addPathPojoMethodMapping(String path, PojoMethodMapping pojoMethodMapping) {
        pathMethodMappingMap.put(path, pojoMethodMapping);
        for (MethodArgumentResolver onOpenArgResolver : pojoMethodMapping.getOnOpenArgResolvers()) {
            if (onOpenArgResolver instanceof PathVariableMethodArgumentResolver
                || onOpenArgResolver instanceof PathVariableMapMethodArgumentResolver) {
                pathMatchers.add(new AntPathMatcherWrapper(path));
                return;
            }
        }
        pathMatchers.add(new DefaultPathMatcher(path));
    }

    /**
     * Has before handshake
     *
     * @param channel channel
     * @param path    path
     * @return the boolean
     * @since 2022.1.1
     */
    public boolean hasBeforeHandshake(Channel channel, String path) {
        PojoMethodMapping methodMapping = getPojoMethodMapping(path, channel);
        return methodMapping.getBeforeHandshake() != null;
    }

    /**
     * Gets pojo method mapping *
     *
     * @param path    path
     * @param channel channel
     * @return the pojo method mapping
     * @since 2022.1.1
     */
    private PojoMethodMapping getPojoMethodMapping(String path, Channel channel) {
        PojoMethodMapping methodMapping;
        if (pathMethodMappingMap.size() == 1) {
            methodMapping = pathMethodMappingMap.values().iterator().next();
        } else {
            Attribute<String> attrPath = channel.attr(PATH_KEY);
            attrPath.set(path);
            methodMapping = pathMethodMappingMap.get(path);
            if (methodMapping == null) {
                throw new RuntimeException("path " + path + " is not in pathMethodMappingMap ");
            }
        }
        return methodMapping;
    }

    /**
     * Do before handshake
     *
     * @param channel channel
     * @param req     req
     * @param path    path
     * @since 2022.1.1
     */
    public void doBeforeHandshake(Channel channel, FullHttpRequest req, String path) {
        PojoMethodMapping methodMapping = getPojoMethodMapping(path, channel);

        Object implement;
        try {
            implement = methodMapping.getEndpointInstance();
        } catch (Exception e) {
            logger.error(e);
            return;
        }
        channel.attr(POJO_KEY).set(implement);
        Session session = new Session(channel);
        channel.attr(SESSION_KEY).set(session);
        Method beforeHandshake = methodMapping.getBeforeHandshake();
        if (beforeHandshake != null) {
            try {
                beforeHandshake.invoke(implement, methodMapping.getBeforeHandshakeArgs(channel, req));
            } catch (TypeMismatchException e) {
                throw e;
            } catch (Throwable t) {
                logger.error(t);
            }
        }
    }

    /**
     * Do on open
     *
     * @param channel channel
     * @param req     req
     * @param path    path
     * @since 2022.1.1
     */
    public void doOnOpen(Channel channel, FullHttpRequest req, String path) {
        PojoMethodMapping methodMapping = getPojoMethodMapping(path, channel);

        Object implement = channel.attr(POJO_KEY).get();
        if (implement == null) {
            try {
                implement = methodMapping.getEndpointInstance();
                channel.attr(POJO_KEY).set(implement);
            } catch (Exception e) {
                logger.error(e);
                return;
            }
            Session session = new Session(channel);
            channel.attr(SESSION_KEY).set(session);
        }

        Method onOpenMethod = methodMapping.getOnOpen();
        if (onOpenMethod != null) {
            try {
                onOpenMethod.invoke(implement, methodMapping.getOnOpenArgs(channel, req));
            } catch (TypeMismatchException e) {
                throw e;
            } catch (Throwable t) {
                logger.error(t);
            }
        }
    }

    /**
     * Do on close
     *
     * @param channel channel
     * @since 2022.1.1
     */
    public void doOnClose(Channel channel) {
        Attribute<String> attrPath = channel.attr(PATH_KEY);
        PojoMethodMapping methodMapping = null;
        if (pathMethodMappingMap.size() == 1) {
            methodMapping = pathMethodMappingMap.values().iterator().next();
        } else {
            String path = attrPath.get();
            methodMapping = pathMethodMappingMap.get(path);
            if (methodMapping == null) {
                return;
            }
        }
        if (methodMapping.getOnClose() != null) {
            if (!channel.hasAttr(SESSION_KEY)) {
                return;
            }
            Object implement = channel.attr(POJO_KEY).get();
            try {
                methodMapping.getOnClose().invoke(implement,
                                                  methodMapping.getOnCloseArgs(channel));
            } catch (Throwable t) {
                logger.error(t);
            }
        }
    }

    /**
     * Do on error
     *
     * @param channel   channel
     * @param throwable throwable
     * @since 2022.1.1
     */
    public void doOnError(Channel channel, Throwable throwable) {
        Attribute<String> attrPath = channel.attr(PATH_KEY);
        PojoMethodMapping methodMapping;
        if (pathMethodMappingMap.size() == 1) {
            methodMapping = pathMethodMappingMap.values().iterator().next();
        } else {
            String path = attrPath.get();
            methodMapping = pathMethodMappingMap.get(path);
        }
        if (methodMapping.getOnError() != null) {
            if (!channel.hasAttr(SESSION_KEY)) {
                return;
            }
            Object implement = channel.attr(POJO_KEY).get();
            try {
                Method method = methodMapping.getOnError();
                Object[] args = methodMapping.getOnErrorArgs(channel, throwable);
                method.invoke(implement, args);
            } catch (Throwable t) {
                logger.error(t);
            }
        }
    }

    /**
     * Do on message
     *
     * @param channel channel
     * @param frame   frame
     * @since 2022.1.1
     */
    public void doOnMessage(Channel channel, WebSocketFrame frame) {
        Attribute<String> attrPath = channel.attr(PATH_KEY);
        PojoMethodMapping methodMapping;
        if (pathMethodMappingMap.size() == 1) {
            methodMapping = pathMethodMappingMap.values().iterator().next();
        } else {
            String path = attrPath.get();
            methodMapping = pathMethodMappingMap.get(path);
        }
        if (methodMapping.getOnMessage() != null) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            Object implement = channel.attr(POJO_KEY).get();
            try {
                methodMapping.getOnMessage().invoke(implement, methodMapping.getOnMessageArgs(channel, textFrame));
            } catch (Throwable t) {
                logger.error(t);
            }
        }
    }

    /**
     * Do on binary
     *
     * @param channel channel
     * @param frame   frame
     * @since 2022.1.1
     */
    public void doOnBinary(Channel channel, WebSocketFrame frame) {
        Attribute<String> attrPath = channel.attr(PATH_KEY);
        PojoMethodMapping methodMapping;
        if (pathMethodMappingMap.size() == 1) {
            methodMapping = pathMethodMappingMap.values().iterator().next();
        } else {
            String path = attrPath.get();
            methodMapping = pathMethodMappingMap.get(path);
        }
        if (methodMapping.getOnBinary() != null) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
            Object implement = channel.attr(POJO_KEY).get();
            try {
                methodMapping.getOnBinary().invoke(implement, methodMapping.getOnBinaryArgs(channel, binaryWebSocketFrame));
            } catch (Throwable t) {
                logger.error(t);
            }
        }
    }

    /**
     * Do on event
     *
     * @param channel channel
     * @param evt     evt
     * @since 2022.1.1
     */
    public void doOnEvent(Channel channel, Object evt) {
        Attribute<String> attrPath = channel.attr(PATH_KEY);
        PojoMethodMapping methodMapping;
        if (pathMethodMappingMap.size() == 1) {
            methodMapping = pathMethodMappingMap.values().iterator().next();
        } else {
            String path = attrPath.get();
            methodMapping = pathMethodMappingMap.get(path);
        }
        if (methodMapping.getOnEvent() != null) {
            if (!channel.hasAttr(SESSION_KEY)) {
                return;
            }
            Object implement = channel.attr(POJO_KEY).get();
            try {
                methodMapping.getOnEvent().invoke(implement, methodMapping.getOnEventArgs(channel, evt));
            } catch (Throwable t) {
                logger.error(t);
            }
        }
    }

    /**
     * Gets host *
     *
     * @return the host
     * @since 2022.1.1
     */
    public String getHost() {
        return config.getHost();
    }

    /**
     * Gets port *
     *
     * @return the port
     * @since 2022.1.1
     */
    public int getPort() {
        return config.getPort();
    }

    /**
     * Gets path matcher set *
     *
     * @return the path matcher set
     * @since 2022.1.1
     */
    public Set<WsPathMatcher> getPathMatcherSet() {
        return pathMatchers;
    }
}
