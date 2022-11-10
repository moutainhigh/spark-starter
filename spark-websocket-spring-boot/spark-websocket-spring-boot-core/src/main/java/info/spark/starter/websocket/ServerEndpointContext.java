package info.spark.starter.websocket;

import info.spark.starter.websocket.annotation.ServerEndpoint;
import info.spark.starter.websocket.exception.DeploymentException;
import info.spark.starter.websocket.pojo.PojoEndpointServer;
import info.spark.starter.websocket.pojo.PojoMethodMapping;
import info.spark.starter.websocket.standard.ServerEndpointConfig;
import info.spark.starter.websocket.standard.WebsocketServer;

import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import javax.net.ssl.SSLException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.11 12:31
 * @since 2022.1.1
 */
@Slf4j
@SuppressWarnings("all")
public class ServerEndpointContext extends ApplicationObjectSupport implements SmartInitializingSingleton, BeanFactoryAware {
    /** Address websocket server map */
    private final Map<InetSocketAddress, WebsocketServer> addressWebsocketServerMap = new HashMap<>();
    /** Bean factory */
    private AbstractBeanFactory beanFactory;

    /**
     * After singletons instantiated
     *
     * @since 2022.1.1
     */
    @Override
    public void afterSingletonsInstantiated() {
        handleEndpoints();
    }

    /**
     * Handle endpoints
     *
     * @since 2022.1.1
     */
    private void handleEndpoints() {
        ApplicationContext context = getApplicationContext();

        String[] endpointBeanNames = Objects.requireNonNull(context).getBeanNamesForAnnotation(ServerEndpoint.class);
        Set<Class<?>> endpointClasses = new LinkedHashSet<>();
        for (String beanName : endpointBeanNames) {
            endpointClasses.add(context.getType(beanName));
        }

        for (Class<?> endpointClass : endpointClasses) {
            if (AopUtils.isCglibProxy(endpointClass)) {
                handleEndpoint(endpointClass.getSuperclass());
            } else {
                handleEndpoint(endpointClass);
            }
        }

        init();
    }

    /**
     * Handle endpoint
     *
     * @param endpointClass endpoint class
     * @since 2022.1.1
     */
    private void handleEndpoint(Class<?> endpointClass) {
        ServerEndpoint annotation = AnnotatedElementUtils.findMergedAnnotation(endpointClass, ServerEndpoint.class);
        if (annotation == null) {
            throw new IllegalStateException("missingAnnotation ServerEndpoint");
        }

        ServerEndpointConfig serverEndpointConfig = buildConfig(annotation);

        ApplicationContext context = getApplicationContext();

        PojoMethodMapping pojoMethodMapping;
        try {
            pojoMethodMapping = new PojoMethodMapping(endpointClass, context, beanFactory);
        } catch (DeploymentException e) {
            throw new IllegalStateException("Failed to register ServerEndpointConfig: " + serverEndpointConfig, e);
        }

        InetSocketAddress inetSocketAddress = new InetSocketAddress(serverEndpointConfig.getHost(), serverEndpointConfig.getPort());
        String path = resolveAnnotationValue(annotation.value(), String.class, "path");

        WebsocketServer websocketServer = addressWebsocketServerMap.get(inetSocketAddress);
        if (websocketServer == null) {
            PojoEndpointServer pojoEndpointServer = new PojoEndpointServer(pojoMethodMapping, serverEndpointConfig, path);
            websocketServer = new WebsocketServer(pojoEndpointServer, serverEndpointConfig);
            addressWebsocketServerMap.put(inetSocketAddress, websocketServer);
        } else {
            websocketServer.getPojoEndpointServer().addPathPojoMethodMapping(path, pojoMethodMapping);
        }
    }

    /**
     * Init
     *
     * @since 2022.1.1
     */
    private void init() {
        for (Map.Entry<InetSocketAddress, WebsocketServer> entry : addressWebsocketServerMap.entrySet()) {
            WebsocketServer websocketServer = entry.getValue();
            try {
                websocketServer.init();
                PojoEndpointServer pojoEndpointServer = websocketServer.getPojoEndpointServer();
                StringJoiner stringJoiner = new StringJoiner(",");
                pojoEndpointServer.getPathMatcherSet().forEach(pathMatcher -> stringJoiner.add("'" + pathMatcher.getPattern() + "'"));
                log.info("Netty WebSocket started on port: [{}] with context path(s): [{}]",
                         pojoEndpointServer.getPort(),
                         stringJoiner);
            } catch (InterruptedException e) {
                log.error("websocket [{}] init fail", entry.getKey(), e);
            } catch (SSLException e) {
                log.error("websocket [{}] ssl create fail", entry.getKey(), e);

            }
        }
    }

    /**
     * Build config
     *
     * @param annotation annotation
     * @return the server endpoint config
     * @since 2022.1.1
     */
    private ServerEndpointConfig buildConfig(ServerEndpoint annotation) {
        String host = resolveAnnotationValue(annotation.host(), String.class, "host");
        int port = resolveAnnotationValue(annotation.port(), Integer.class, "port");
        String path = resolveAnnotationValue(annotation.value(), String.class, "value");
        int bossLoopGroupThreads = resolveAnnotationValue(annotation.bossLoopGroupThreads(), Integer.class, "bossLoopGroupThreads");
        int workerLoopGroupThreads = resolveAnnotationValue(annotation.workerLoopGroupThreads(), Integer.class, "workerLoopGroupThreads");
        boolean useCompressionHandler = resolveAnnotationValue(annotation.useCompressionHandler(), Boolean.class, "useCompressionHandler");

        int optionConnectTimeoutMillis = resolveAnnotationValue(annotation.optionConnectTimeoutMillis(), Integer.class,
                                                                "optionConnectTimeoutMillis");
        int optionSoBacklog = resolveAnnotationValue(annotation.optionSoBacklog(), Integer.class, "optionSoBacklog");

        int childOptionWriteSpinCount = resolveAnnotationValue(annotation.childOptionWriteSpinCount(), Integer.class,
                                                               "childOptionWriteSpinCount");
        int childOptionWriteBufferHighWaterMark = resolveAnnotationValue(annotation.childOptionWriteBufferHighWaterMark(), Integer.class,
                                                                         "childOptionWriteBufferHighWaterMark");
        int childOptionWriteBufferLowWaterMark = resolveAnnotationValue(annotation.childOptionWriteBufferLowWaterMark(), Integer.class,
                                                                        "childOptionWriteBufferLowWaterMark");
        int childOptionSoRcvbuf = resolveAnnotationValue(annotation.childOptionSoRcvbuf(), Integer.class, "childOptionSoRcvbuf");
        int childOptionSoSndbuf = resolveAnnotationValue(annotation.childOptionSoSndbuf(), Integer.class, "childOptionSoSndbuf");
        boolean childOptionTcpNodelay = resolveAnnotationValue(annotation.childOptionTcpNodelay(), Boolean.class, "childOptionTcpNodelay");
        boolean childOptionSoKeepalive = resolveAnnotationValue(annotation.childOptionSoKeepalive(), Boolean.class,
                                                                "childOptionSoKeepalive");
        int childOptionSoLinger = resolveAnnotationValue(annotation.childOptionSoLinger(), Integer.class, "childOptionSoLinger");
        boolean childOptionAllowHalfClosure = resolveAnnotationValue(annotation.childOptionAllowHalfClosure(), Boolean.class,
                                                                     "childOptionAllowHalfClosure");

        int readerIdleTimeSeconds = resolveAnnotationValue(annotation.readerIdleTimeSeconds(), Integer.class, "readerIdleTimeSeconds");
        int writerIdleTimeSeconds = resolveAnnotationValue(annotation.writerIdleTimeSeconds(), Integer.class, "writerIdleTimeSeconds");
        int allIdleTimeSeconds = resolveAnnotationValue(annotation.allIdleTimeSeconds(), Integer.class, "allIdleTimeSeconds");

        int maxFramePayloadLength = resolveAnnotationValue(annotation.maxFramePayloadLength(), Integer.class, "maxFramePayloadLength");

        boolean useEventExecutorGroup = resolveAnnotationValue(annotation.useEventExecutorGroup(), Boolean.class, "useEventExecutorGroup");
        int eventExecutorGroupThreads = resolveAnnotationValue(annotation.eventExecutorGroupThreads(), Integer.class,
                                                               "eventExecutorGroupThreads");

        String sslKeyPassword = resolveAnnotationValue(annotation.sslKeyPassword(), String.class, "sslKeyPassword");
        String sslKeyStore = resolveAnnotationValue(annotation.sslKeyStore(), String.class, "sslKeyStore");
        String sslKeyStorePassword = resolveAnnotationValue(annotation.sslKeyStorePassword(), String.class, "sslKeyStorePassword");
        String sslKeyStoreType = resolveAnnotationValue(annotation.sslKeyStoreType(), String.class, "sslKeyStoreType");
        String sslTrustStore = resolveAnnotationValue(annotation.sslTrustStore(), String.class, "sslTrustStore");
        String sslTrustStorePassword = resolveAnnotationValue(annotation.sslTrustStorePassword(), String.class, "sslTrustStorePassword");
        String sslTrustStoreType = resolveAnnotationValue(annotation.sslTrustStoreType(), String.class, "sslTrustStoreType");

        String[] corsOrigins = annotation.corsOrigins();
        if (corsOrigins.length != 0) {
            for (int i = 0; i < corsOrigins.length; i++) {
                corsOrigins[i] = resolveAnnotationValue(corsOrigins[i], String.class, "corsOrigins");
            }
        }
        Boolean corsAllowCredentials = resolveAnnotationValue(annotation.corsAllowCredentials(), Boolean.class, "corsAllowCredentials");

        return new ServerEndpointConfig(host,
                                        port,
                                        bossLoopGroupThreads,
                                        workerLoopGroupThreads,
                                        useCompressionHandler,
                                        optionConnectTimeoutMillis,
                                        optionSoBacklog,
                                        childOptionWriteSpinCount,
                                        childOptionWriteBufferHighWaterMark,
                                        childOptionWriteBufferLowWaterMark,
                                        childOptionSoRcvbuf,
                                        childOptionSoSndbuf,
                                        childOptionTcpNodelay,
                                        childOptionSoKeepalive,
                                        childOptionSoLinger,
                                        childOptionAllowHalfClosure,
                                        readerIdleTimeSeconds,
                                        writerIdleTimeSeconds,
                                        allIdleTimeSeconds,
                                        maxFramePayloadLength,
                                        useEventExecutorGroup,
                                        eventExecutorGroupThreads,
                                        sslKeyPassword,
                                        sslKeyStore,
                                        sslKeyStorePassword,
                                        sslKeyStoreType,
                                        sslTrustStore,
                                        sslTrustStorePassword,
                                        sslTrustStoreType,
                                        corsOrigins,
                                        corsAllowCredentials);
    }

    /**
     * Resolve annotation value
     *
     * @param <T>          parameter
     * @param value        value
     * @param requiredType required type
     * @param paramName    param name
     * @return the t
     * @since 2022.1.1
     */
    private <T> T resolveAnnotationValue(Object value, Class<T> requiredType, String paramName) {
        if (value == null) {
            return null;
        }
        TypeConverter typeConverter = beanFactory.getTypeConverter();

        if (value instanceof String) {
            String strVal = beanFactory.resolveEmbeddedValue((String) value);
            BeanExpressionResolver beanExpressionResolver = beanFactory.getBeanExpressionResolver();
            if (beanExpressionResolver != null) {
                value = beanExpressionResolver.evaluate(strVal, new BeanExpressionContext(beanFactory, null));
            } else {
                value = strVal;
            }
        }
        try {
            return typeConverter.convertIfNecessary(value, requiredType);
        } catch (TypeMismatchException e) {
            throw new IllegalArgumentException("Failed to convert value of parameter '" +
                                               paramName + "' to required type '" +
                                               requiredType.getName() + "'");
        }
    }

    /**
     * Sets bean factory *
     *
     * @param beanFactory bean factory
     * @since 2022.1.1
     */
    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) {
        if (!(beanFactory instanceof AbstractBeanFactory)) {
            throw new IllegalArgumentException(
                "AutowiredAnnotationBeanPostProcessor requires a AbstractBeanFactory: " + beanFactory);
        }
        this.beanFactory = (AbstractBeanFactory) beanFactory;
    }

}
