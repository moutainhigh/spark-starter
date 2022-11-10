package info.spark.starter.dubbo.filter;

import info.spark.starter.basic.context.Trace;
import info.spark.starter.basic.exception.BasicException;
import info.spark.starter.basic.exception.ServiceInternalException;
import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.core.util.NetUtils;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.ListenableFilter;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.service.GenericService;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 重写 {@link org.apache.dubbo.rpc.filter.ExceptionFilter}, 不将 v5 框架异常包装为 RPC 异常 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.17 14:04
 * @since 1.5.0
 */
@Activate(group = CommonConstants.PROVIDER, order = 3)
public class ConsumerExceptionFilter extends ListenableFilter {

    /**
     * Exception filter
     *
     * @since 1.5.0
     */
    public ConsumerExceptionFilter() {
        super.listener = new ExceptionListener();
    }

    /**
     * Invoke
     *
     * @param invoker    invoker
     * @param invocation invocation
     * @return the result
     * @throws RpcException rpc exception
     * @since 1.5.0
     */
    @Override
    public Result invoke(@NotNull Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.07.17 14:15
     * @since 1.5.0
     */
    @Slf4j
    static class ExceptionListener implements Listener {

        /** JAVA_STR */
        private static final String JAVA_STR = "java.";
        /** JAVAX_STR */
        private static final String JAVAX_STR = "javax.";

        /**
         * 重写异常拦截逻辑, 将部分异常包装为 BasicException 异常.
         *
         * @param appResponse app response
         * @param invoker     invoker
         * @param invocation  invocation
         * @since 1.5.0
         */
        @Override
        @SuppressWarnings("PMD.UndefineMagicConstantRule")
        public void onResponse(@NotNull Result appResponse, Invoker<?> invoker, Invocation invocation) {
            if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
                try {
                    Throwable exception = appResponse.getException();

                    // 如果是 checked 异常, 全部包装为 BasicException
                    if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                        appResponse.setException(new BasicException());
                        return;
                    }
                    // 在方法签名上有声明, 直接抛出
                    try {
                        Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                        Class<?>[] exceptionClassses = method.getExceptionTypes();
                        for (Class<?> exceptionClass : exceptionClassses) {
                            if (exception.getClass().equals(exceptionClass)) {
                                return;
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        return;
                    }

                    // 如果系统自定义异常, 直接抛出
                    if (exception instanceof BasicException) {
                        return;
                    }

                    // 未在方法签名上定义的异常, 在服务器端打印 ERROR 日志
                    this.onError(exception, invoker, invocation);

                    // 异常类和接口类在同一 jar 包里, 直接抛出
                    String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                    String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                    if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)) {
                        return;
                    }
                    // 是 JDK 自带的异常, 直接抛出
                    String className = exception.getClass().getName();
                    if (className.startsWith(JAVA_STR) || className.startsWith(JAVAX_STR)) {
                        return;
                    }

                    // 是 Dubbo 本身的异常, 直接抛出
                    if (exception instanceof RpcException) {
                        return;
                    }

                    // 其他的异常包装为 ServiceInternalException
                    ServiceInternalException serviceInternalException = new ServiceInternalException(exception);
                    serviceInternalException.setApplicationName(ConfigKit.getAppName());
                    serviceInternalException.setIp(NetUtils.getLocalHost());
                    serviceInternalException.setTraceId(Trace.context().get());
                    serviceInternalException.setEnv(ConfigKit.getEnv().getName());
                    serviceInternalException.setPort(ConfigKit.getDubboPort());
                    serviceInternalException.setRpc(true);
                    appResponse.setException(serviceInternalException);
                } catch (Throwable e) {
                    String warnMessage = "执行 ConsumerExceptionFilter case: {}. service: {}, method: {}, exception: {} :{}";
                    log.warn(StrFormatter.format(warnMessage,
                                                 RpcContext.getContext().getRemoteHost(),
                                                 invoker.getInterface().getName(),
                                                 invocation.getMethodName(),
                                                 e.getClass().getName(),
                                                 e.getMessage()),
                             e);
                }
            }
        }

        /**
         * On error
         *
         * @param exception  exception
         * @param invoker    invoker
         * @param invocation invocation
         * @since 1.5.0
         */
        @Override
        public void onError(Throwable exception, @NotNull Invoker<?> invoker, @NotNull Invocation invocation) {
            String message = "捕获未处理异常 {}. service: {}, method: {}, exception: {} :{}";
            log.error(StrFormatter.format(message,
                                          RpcContext.getContext().getRemoteHost(),
                                          invoker.getInterface().getName(),
                                          invocation.getMethodName(),
                                          exception.getClass().getName(),
                                          exception.getMessage()),
                      exception);
        }

    }
}

