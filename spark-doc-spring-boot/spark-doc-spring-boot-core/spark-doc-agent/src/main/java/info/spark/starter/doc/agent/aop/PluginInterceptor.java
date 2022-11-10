package info.spark.starter.doc.agent.aop;

import info.spark.starter.common.context.SpringContext;
import info.spark.starter.common.util.JustOnceLogger;
import info.spark.starter.doc.agent.schema.AgentOperationParameterReader;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.spi.service.contexts.OperationContext;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.27 15:28
 * @since 1.7.0
 */
@Slf4j
public class PluginInterceptor implements MethodInterceptor, ApplicationContextAware {

    /** INTERCEPTOR_METHOD_NAME */
    private static final String INTERCEPTOR_METHOD_NAME = "apply";

    /** Application context */
    @Getter
    private ApplicationContext applicationContext;

    /**
     * Sets application context *
     *
     * @param applicationContext application context
     * @throws BeansException beans exception
     * @since 1.7.0
     */
    @Override
    public void setApplicationContext(final @NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Invoke
     *
     * @param invocation invocation
     * @return the object
     * @throws Throwable throwable
     * @since 1.7.0
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (INTERCEPTOR_METHOD_NAME.equals(invocation.getMethod().getName())) {
            JustOnceLogger.infoOnce(PluginInterceptor.class.getName(),
                                    "-> agent-doc 拦截 OperationParameterReader." + INTERCEPTOR_METHOD_NAME);
            AgentOperationParameterReader reader = SpringContext.getInstance(AgentOperationParameterReader.class);
            reader.apply((OperationContext) invocation.getArguments()[0]);
            return null;
        }
        return invocation.proceed();
    }
}
