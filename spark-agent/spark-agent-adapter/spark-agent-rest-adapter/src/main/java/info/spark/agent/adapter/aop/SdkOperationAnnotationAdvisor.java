package info.spark.agent.adapter.aop;

import info.spark.agent.adapter.annotation.SdkOperation;

import org.jetbrains.annotations.NotNull;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.7 20:42
 * @since 2.1.0
 */
@Slf4j
@SuppressWarnings("checkstyle:Indentation")
public class SdkOperationAnnotationAdvisor implements MethodBeforeAdvice, AfterReturningAdvice {
    /** 缓存 sdk 上的 api name */
    private static final Map<String, String> AGENT_CLIENT_METHOD_NAME_CACHE = new ConcurrentHashMap<>(64);

    /**
     * Before
     *
     * @param method method
     * @param args   args
     * @param target target
     * @since 2.1.0
     */
    @Override
    public void before(@NotNull Method method, Object[] args, Object target) {
        if (target != null) {
            String apiServiceName = AGENT_CLIENT_METHOD_NAME_CACHE.
                computeIfAbsent(target.getClass().getName() + ":" + method.getName(),
                                k -> {
                                    SdkOperation annotation = AnnotationUtils.findAnnotation(method, SdkOperation.class);
                                    return Objects.requireNonNull(annotation).apiServiceName();
                                });

            SdkAnnotationAdvisor.CONTEXT.set(apiServiceName);
        }
    }

    /**
     * After returning
     *
     * @param returnValue return value
     * @param method      method
     * @param args        args
     * @param target      target
     * @since 2.1.0
     */
    @Override
    public void afterReturning(Object returnValue,
                               @NotNull Method method,
                               Object[] args, Object target) {
        SdkAnnotationAdvisor.CONTEXT.remove();
    }

}
