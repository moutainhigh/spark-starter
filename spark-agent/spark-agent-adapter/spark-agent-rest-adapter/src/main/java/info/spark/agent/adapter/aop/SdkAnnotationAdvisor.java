package info.spark.agent.adapter.aop;

import com.alibaba.ttl.TransmittableThreadLocal;
import info.spark.agent.adapter.annotation.Sdk;
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
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.28 19:27
 * @since 2.0.0
 */
@Slf4j
@SuppressWarnings("checkstyle:Indentation")
public class SdkAnnotationAdvisor implements MethodBeforeAdvice, AfterReturningAdvice {
    /** CONTEXT */
    public static final TransmittableThreadLocal<String> CONTEXT = new TransmittableThreadLocal<>();
    /** 缓存 sdk 上的 api name */
    private static final Map<String, String> AGENT_CLIENT_GLOBAL_API_NAME_CACHE = new ConcurrentHashMap<>(64);

    /**
     * 优先使用 {@link SdkOperation#apiServiceName()}
     *
     * @param method method
     * @param args   args
     * @param target target
     * @since 2.0.0
     */
    @Override
    public void before(@NotNull Method method, Object[] args, Object target) {
        if (target != null) {
            String apiServiceName = AGENT_CLIENT_GLOBAL_API_NAME_CACHE
                .computeIfAbsent(target.getClass().getName() + "." + method.getName(),
                                 k -> {
                                     Sdk sdk = AnnotationUtils.findAnnotation(target.getClass(), Sdk.class);
                                     final SdkOperation sdkOperation = AnnotationUtils.findAnnotation(method, SdkOperation.class);

                                     // 如果存在 SdkOperation 则优先使用
                                     if (sdkOperation != null) {
                                         return sdkOperation.apiServiceName();
                                     }

                                     return Objects.requireNonNull(sdk).apiServiceName();
                                 });

            CONTEXT.set(apiServiceName);
        }
    }

    /**
     * After returning
     *
     * @param returnValue return value
     * @param method      method
     * @param args        args
     * @param target      target
     * @since 2.0.0
     */
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
        CONTEXT.remove();
    }

}
