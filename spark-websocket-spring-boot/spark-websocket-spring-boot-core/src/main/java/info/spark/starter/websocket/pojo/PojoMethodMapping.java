package info.spark.starter.websocket.pojo;

import info.spark.starter.websocket.annotation.BeforeHandshake;
import info.spark.starter.websocket.annotation.OnBinary;
import info.spark.starter.websocket.annotation.OnClose;
import info.spark.starter.websocket.annotation.OnError;
import info.spark.starter.websocket.annotation.OnEvent;
import info.spark.starter.websocket.annotation.OnMessage;
import info.spark.starter.websocket.annotation.OnOpen;
import info.spark.starter.websocket.exception.DeploymentException;
import info.spark.starter.websocket.support.ByteMethodArgumentResolver;
import info.spark.starter.websocket.support.EventMethodArgumentResolver;
import info.spark.starter.websocket.support.HttpHeadersMethodArgumentResolver;
import info.spark.starter.websocket.support.MethodArgumentResolver;
import info.spark.starter.websocket.support.PathVariableMapMethodArgumentResolver;
import info.spark.starter.websocket.support.PathVariableMethodArgumentResolver;
import info.spark.starter.websocket.support.RequestParamMapMethodArgumentResolver;
import info.spark.starter.websocket.support.RequestParamMethodArgumentResolver;
import info.spark.starter.websocket.support.SessionMethodArgumentResolver;
import info.spark.starter.websocket.support.TextMethodArgumentResolver;
import info.spark.starter.websocket.support.ThrowableMethodArgumentResolver;

import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:07
 * @since 2022.1.1
 */
@SuppressWarnings("all")
public class PojoMethodMapping {

    /** parameterNameDiscoverer */
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /** Before handshake */
    private final Method beforeHandshake;
    /** On open */
    private final Method onOpen;
    /** On close */
    private final Method onClose;
    /** On error */
    private final Method onError;
    /** On message */
    private final Method onMessage;
    /** On binary */
    private final Method onBinary;
    /** On event */
    private final Method onEvent;
    /** Before handshake parameters */
    private final MethodParameter[] beforeHandshakeParameters;
    /** On open parameters */
    private final MethodParameter[] onOpenParameters;
    /** On close parameters */
    private final MethodParameter[] onCloseParameters;
    /** On error parameters */
    private final MethodParameter[] onErrorParameters;
    /** On message parameters */
    private final MethodParameter[] onMessageParameters;
    /** On binary parameters */
    private final MethodParameter[] onBinaryParameters;
    /** On event parameters */
    private final MethodParameter[] onEventParameters;
    /** Before handshake arg resolvers */
    private final MethodArgumentResolver[] beforeHandshakeArgResolvers;
    /** On open arg resolvers */
    private final MethodArgumentResolver[] onOpenArgResolvers;
    /** On close arg resolvers */
    private final MethodArgumentResolver[] onCloseArgResolvers;
    /** On error arg resolvers */
    private final MethodArgumentResolver[] onErrorArgResolvers;
    /** On message arg resolvers */
    private final MethodArgumentResolver[] onMessageArgResolvers;
    /** On binary arg resolvers */
    private final MethodArgumentResolver[] onBinaryArgResolvers;
    /** On event arg resolvers */
    private final MethodArgumentResolver[] onEventArgResolvers;
    /** Pojo clazz */
    private final Class<?> endpointClass;
    /** Application context */
    private final ApplicationContext applicationContext;
    /** Bean factory */
    private final AbstractBeanFactory beanFactory;

    /**
     * Pojo method mapping
     *
     * @param endpointClass pojo clazz
     * @param context       context
     * @param beanFactory   bean factory
     * @throws DeploymentException deployment exception
     * @since 2022.1.1
     */
    public PojoMethodMapping(Class<?> endpointClass, ApplicationContext context, AbstractBeanFactory beanFactory)
        throws DeploymentException {
        this.applicationContext = context;
        this.endpointClass = endpointClass;
        this.beanFactory = beanFactory;
        Method handshake = null;
        Method open = null;
        Method close = null;
        Method error = null;
        Method message = null;
        Method binary = null;
        Method event = null;
        Method[] pojoClazzMethods = null;
        Class<?> currentClazz = endpointClass;
        while (!currentClazz.equals(Object.class)) {
            Method[] currentClazzMethods = currentClazz.getDeclaredMethods();
            if (currentClazz == endpointClass) {
                pojoClazzMethods = currentClazzMethods;
            }
            for (Method method : currentClazzMethods) {
                if (method.getAnnotation(BeforeHandshake.class) != null) {
                    checkPublic(method);
                    if (handshake == null) {
                        handshake = method;
                    } else {
                        if (currentClazz == endpointClass ||
                            !isMethodOverride(handshake, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                "pojoMethodMapping.duplicateAnnotation BeforeHandshake");
                        }
                    }
                } else if (method.getAnnotation(OnOpen.class) != null) {
                    checkPublic(method);
                    if (open == null) {
                        open = method;
                    } else {
                        if (currentClazz == endpointClass ||
                            !isMethodOverride(open, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                "pojoMethodMapping.duplicateAnnotation OnOpen");
                        }
                    }
                } else if (method.getAnnotation(OnClose.class) != null) {
                    checkPublic(method);
                    if (close == null) {
                        close = method;
                    } else {
                        if (currentClazz == endpointClass ||
                            !isMethodOverride(close, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                "pojoMethodMapping.duplicateAnnotation OnClose");
                        }
                    }
                } else if (method.getAnnotation(OnError.class) != null) {
                    checkPublic(method);
                    if (error == null) {
                        error = method;
                    } else {
                        if (currentClazz == endpointClass ||
                            !isMethodOverride(error, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                "pojoMethodMapping.duplicateAnnotation OnError");
                        }
                    }
                } else if (method.getAnnotation(OnMessage.class) != null) {
                    checkPublic(method);
                    if (message == null) {
                        message = method;
                    } else {
                        if (currentClazz == endpointClass ||
                            !isMethodOverride(message, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                "pojoMethodMapping.duplicateAnnotation onMessage");
                        }
                    }
                } else if (method.getAnnotation(OnBinary.class) != null) {
                    checkPublic(method);
                    if (binary == null) {
                        binary = method;
                    } else {
                        if (currentClazz == endpointClass ||
                            !isMethodOverride(binary, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                "pojoMethodMapping.duplicateAnnotation OnBinary");
                        }
                    }
                } else if (method.getAnnotation(OnEvent.class) != null) {
                    checkPublic(method);
                    if (event == null) {
                        event = method;
                    } else {
                        if (currentClazz == endpointClass ||
                            !isMethodOverride(event, method)) {
                            // Duplicate annotation
                            throw new DeploymentException(
                                "pojoMethodMapping.duplicateAnnotation OnEvent");
                        }
                    }
                } else {
                    // Method not annotated
                }
            }
            currentClazz = currentClazz.getSuperclass();
        }
        // If the methods are not on endpointClass and they are overridden
        // by a non annotated method in endpointClass, they should be ignored
        if (handshake != null && handshake.getDeclaringClass() != endpointClass) {
            if (isOverridenWithoutAnnotation(pojoClazzMethods, handshake, BeforeHandshake.class)) {
                handshake = null;
            }
        }
        if (open != null && open.getDeclaringClass() != endpointClass) {
            if (isOverridenWithoutAnnotation(pojoClazzMethods, open, OnOpen.class)) {
                open = null;
            }
        }
        if (close != null && close.getDeclaringClass() != endpointClass) {
            if (isOverridenWithoutAnnotation(pojoClazzMethods, close, OnClose.class)) {
                close = null;
            }
        }
        if (error != null && error.getDeclaringClass() != endpointClass) {
            if (isOverridenWithoutAnnotation(pojoClazzMethods, error, OnError.class)) {
                error = null;
            }
        }
        if (message != null && message.getDeclaringClass() != endpointClass) {
            if (isOverridenWithoutAnnotation(pojoClazzMethods, message, OnMessage.class)) {
                message = null;
            }
        }
        if (binary != null && binary.getDeclaringClass() != endpointClass) {
            if (isOverridenWithoutAnnotation(pojoClazzMethods, binary, OnBinary.class)) {
                binary = null;
            }
        }
        if (event != null && event.getDeclaringClass() != endpointClass) {
            if (isOverridenWithoutAnnotation(pojoClazzMethods, event, OnEvent.class)) {
                event = null;
            }
        }

        this.beforeHandshake = handshake;
        this.onOpen = open;
        this.onClose = close;
        this.onError = error;
        this.onMessage = message;
        this.onBinary = binary;
        this.onEvent = event;
        beforeHandshakeParameters = getParameters(beforeHandshake);
        onOpenParameters = getParameters(onOpen);
        onCloseParameters = getParameters(onClose);
        onMessageParameters = getParameters(onMessage);
        onErrorParameters = getParameters(onError);
        onBinaryParameters = getParameters(onBinary);
        onEventParameters = getParameters(onEvent);
        beforeHandshakeArgResolvers = getResolvers(beforeHandshakeParameters);
        onOpenArgResolvers = getResolvers(onOpenParameters);
        onCloseArgResolvers = getResolvers(onCloseParameters);
        onMessageArgResolvers = getResolvers(onMessageParameters);
        onErrorArgResolvers = getResolvers(onErrorParameters);
        onBinaryArgResolvers = getResolvers(onBinaryParameters);
        onEventArgResolvers = getResolvers(onEventParameters);
    }

    /**
     * Check public
     *
     * @param m m
     * @throws DeploymentException deployment exception
     * @since 2022.1.1
     */
    private void checkPublic(Method m) throws DeploymentException {
        if (!Modifier.isPublic(m.getModifiers())) {
            throw new DeploymentException(
                "pojoMethodMapping.methodNotPublic " + m.getName());
        }
    }

    /**
     * Is method override
     *
     * @param method1 method 1
     * @param method2 method 2
     * @return the boolean
     * @since 2022.1.1
     */
    private boolean isMethodOverride(Method method1, Method method2) {
        return (method1.getName().equals(method2.getName())
                && method1.getReturnType().equals(method2.getReturnType())
                && Arrays.equals(method1.getParameterTypes(), method2.getParameterTypes()));
    }

    /**
     * Is overriden without annotation
     *
     * @param methods          methods
     * @param superclazzMethod superclazz method
     * @param annotation       annotation
     * @return the boolean
     * @since 2022.1.1
     */
    private boolean isOverridenWithoutAnnotation(Method[] methods, Method superclazzMethod, Class<? extends Annotation> annotation) {
        for (Method method : methods) {
            if (isMethodOverride(method, superclazzMethod)
                && (method.getAnnotation(annotation) == null)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get parameters
     *
     * @param m m
     * @return the method parameter [ ]
     * @since 2022.1.1
     */
    private static MethodParameter[] getParameters(Method m) {
        if (m == null) {
            return new MethodParameter[0];
        }
        int count = m.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            MethodParameter methodParameter = new MethodParameter(m, i);
            methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
            result[i] = methodParameter;
        }
        return result;
    }

    /**
     * Get resolvers
     *
     * @param parameters parameters
     * @return the method argument resolver [ ]
     * @throws DeploymentException deployment exception
     * @since 2022.1.1
     */
    private MethodArgumentResolver[] getResolvers(MethodParameter[] parameters) throws DeploymentException {
        MethodArgumentResolver[] methodArgumentResolvers = new MethodArgumentResolver[parameters.length];
        List<MethodArgumentResolver> resolvers = getDefaultResolvers();
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            for (MethodArgumentResolver resolver : resolvers) {
                if (resolver.supportsParameter(parameter)) {
                    methodArgumentResolvers[i] = resolver;
                    break;
                }
            }
            if (methodArgumentResolvers[i] == null) {
                throw new DeploymentException("pojoMethodMapping.paramClassIncorrect parameter name : " + parameter.getParameterName());
            }
        }
        return methodArgumentResolvers;
    }

    /**
     * Gets default resolvers *
     *
     * @return the default resolvers
     * @since 2022.1.1
     */
    private List<MethodArgumentResolver> getDefaultResolvers() {
        List<MethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new SessionMethodArgumentResolver());
        resolvers.add(new HttpHeadersMethodArgumentResolver());
        resolvers.add(new TextMethodArgumentResolver());
        resolvers.add(new ThrowableMethodArgumentResolver());
        resolvers.add(new ByteMethodArgumentResolver());
        resolvers.add(new RequestParamMapMethodArgumentResolver());
        resolvers.add(new RequestParamMethodArgumentResolver(beanFactory));
        resolvers.add(new PathVariableMapMethodArgumentResolver());
        resolvers.add(new PathVariableMethodArgumentResolver(beanFactory));
        resolvers.add(new EventMethodArgumentResolver(beanFactory));
        return resolvers;
    }

    /**
     * Gets endpoint instance *
     *
     * @return the endpoint instance
     * @since 2022.1.1
     */
    Object getEndpointInstance() {
        return applicationContext.getBean(endpointClass);
    }

    /**
     * Gets before handshake *
     *
     * @return the before handshake
     * @since 2022.1.1
     */
    Method getBeforeHandshake() {
        return beforeHandshake;
    }

    /**
     * Get before handshake args
     *
     * @param channel channel
     * @param req     req
     * @return the object [ ]
     * @throws Exception exception
     * @since 2022.1.1
     */
    Object[] getBeforeHandshakeArgs(Channel channel, FullHttpRequest req) throws Exception {
        return getMethodArgumentValues(channel, req, beforeHandshakeParameters, beforeHandshakeArgResolvers);
    }

    /**
     * Get method argument values
     *
     * @param channel    channel
     * @param object     object
     * @param parameters parameters
     * @param resolvers  resolvers
     * @return the object [ ]
     * @throws Exception exception
     * @since 2022.1.1
     */
    private Object[] getMethodArgumentValues(Channel channel, Object object, MethodParameter[] parameters,
                                             MethodArgumentResolver[] resolvers) throws Exception {
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            MethodArgumentResolver resolver = resolvers[i];
            Object arg = resolver.resolveArgument(parameter, channel, object);
            objects[i] = arg;
        }
        return objects;
    }

    /**
     * Gets on open *
     *
     * @return the on open
     * @since 2022.1.1
     */
    Method getOnOpen() {
        return onOpen;
    }

    /**
     * Get on open args
     *
     * @param channel channel
     * @param req     req
     * @return the object [ ]
     * @throws Exception exception
     * @since 2022.1.1
     */
    Object[] getOnOpenArgs(Channel channel, FullHttpRequest req) throws Exception {
        return getMethodArgumentValues(channel, req, onOpenParameters, onOpenArgResolvers);
    }

    /**
     * Get on open arg resolvers
     *
     * @return the method argument resolver [ ]
     * @since 2022.1.1
     */
    MethodArgumentResolver[] getOnOpenArgResolvers() {
        return onOpenArgResolvers;
    }

    /**
     * Gets on close *
     *
     * @return the on close
     * @since 2022.1.1
     */
    Method getOnClose() {
        return onClose;
    }

    /**
     * Get on close args
     *
     * @param channel channel
     * @return the object [ ]
     * @throws Exception exception
     * @since 2022.1.1
     */
    Object[] getOnCloseArgs(Channel channel) throws Exception {
        return getMethodArgumentValues(channel, null, onCloseParameters, onCloseArgResolvers);
    }

    /**
     * Gets on error *
     *
     * @return the on error
     * @since 2022.1.1
     */
    Method getOnError() {
        return onError;
    }

    /**
     * Get on error args
     *
     * @param channel   channel
     * @param throwable throwable
     * @return the object [ ]
     * @throws Exception exception
     * @since 2022.1.1
     */
    Object[] getOnErrorArgs(Channel channel, Throwable throwable) throws Exception {
        return getMethodArgumentValues(channel, throwable, onErrorParameters, onErrorArgResolvers);
    }

    /**
     * Gets on message *
     *
     * @return the on message
     * @since 2022.1.1
     */
    Method getOnMessage() {
        return onMessage;
    }

    /**
     * Get on message args
     *
     * @param channel            channel
     * @param textWebSocketFrame text web socket frame
     * @return the object [ ]
     * @throws Exception exception
     * @since 2022.1.1
     */
    Object[] getOnMessageArgs(Channel channel, TextWebSocketFrame textWebSocketFrame) throws Exception {
        return getMethodArgumentValues(channel, textWebSocketFrame, onMessageParameters, onMessageArgResolvers);
    }

    /**
     * Gets on binary *
     *
     * @return the on binary
     * @since 2022.1.1
     */
    Method getOnBinary() {
        return onBinary;
    }

    /**
     * Get on binary args
     *
     * @param channel              channel
     * @param binaryWebSocketFrame binary web socket frame
     * @return the object [ ]
     * @throws Exception exception
     * @since 2022.1.1
     */
    Object[] getOnBinaryArgs(Channel channel, BinaryWebSocketFrame binaryWebSocketFrame) throws Exception {
        return getMethodArgumentValues(channel, binaryWebSocketFrame, onBinaryParameters, onBinaryArgResolvers);
    }

    /**
     * Gets on event *
     *
     * @return the on event
     * @since 2022.1.1
     */
    Method getOnEvent() {
        return onEvent;
    }

    /**
     * Get on event args
     *
     * @param channel channel
     * @param evt     evt
     * @return the object [ ]
     * @throws Exception exception
     * @since 2022.1.1
     */
    Object[] getOnEventArgs(Channel channel, Object evt) throws Exception {
        return getMethodArgumentValues(channel, evt, onEventParameters, onEventArgResolvers);
    }
}
