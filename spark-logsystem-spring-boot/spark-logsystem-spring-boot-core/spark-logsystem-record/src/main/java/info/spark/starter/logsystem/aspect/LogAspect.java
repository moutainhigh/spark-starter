package info.spark.starter.logsystem.aspect;

import info.spark.starter.logsystem.annotation.OperationLog;
import info.spark.starter.logsystem.annotation.RestLog;
import info.spark.starter.logsystem.publisher.ApiLogPublisher;
import info.spark.starter.logsystem.publisher.SystemLogPublisher;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 操作日志使用 spring event 异步入库 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 04:54
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class LogAspect extends AbstractLoggerDescParser {

    /**
     * 拦截 API, 生成日志
     *
     * @param point   point
     * @param restLog api log
     * @return the object
     * @throws Throwable throwable
     * @since 1.0.0
     */
    @Around("@annotation(restLog)")
    public Object restLogAround(@NotNull ProceedingJoinPoint point, RestLog restLog) throws Throwable {
        // 获取类名
        String className = point.getTarget().getClass().getName();
        // 获取方法
        String methodName = point.getSignature().getName();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 执行方法
        Object result = point.proceed();
        stopWatch.stop();
        // 记录日志
        ApiLogPublisher.publishEvent(methodName, className, restLog, stopWatch.getTotalTimeMillis());
        return result;
    }

    /**
     * 拦截系统敏感操作, 生成日志
     *
     * @param point        point
     * @param operationLog operation log
     * @return the object
     * @throws Throwable throwable
     * @since 1.0.0
     */
    @Around("@annotation(operationLog)")
    public Object errorLogAround(@NotNull ProceedingJoinPoint point, @NotNull OperationLog operationLog) throws Throwable {
        // 执行方法
        Object result;
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        String desc = super.parseDescription(method, point.getArgs(), operationLog.value());
        result = point.proceed();
        // 记录日志
        SystemLogPublisher.publishEvent(operationLog.action(), desc);
        return result;
    }

}
