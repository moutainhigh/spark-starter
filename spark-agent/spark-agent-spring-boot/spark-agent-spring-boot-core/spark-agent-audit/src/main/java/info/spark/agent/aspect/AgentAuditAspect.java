package info.spark.agent.aspect;


import info.spark.agent.annotation.AgentAudit;
import info.spark.agent.publisher.AgentAuditPublisher;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 操作日志使用 spring event 异步入库 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 04:54
 * @since 1.6.0
 */
@Slf4j
@Aspect
public class AgentAuditAspect {

    /**
     * 拦截 {@link AgentAudit}
     *
     * @param point      point
     * @param agentAudit api log
     * @return the object
     * @throws Throwable throwable
     * @since 1.6.0
     */
    @Around("@annotation(agentAudit)")
    public Object agentAuditAround(@NotNull ProceedingJoinPoint point, @NotNull AgentAudit agentAudit) throws Throwable {
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
        AgentAuditPublisher.publishEvent(methodName, className, agentAudit, stopWatch.getTotalTimeMillis());
        return result;
    }

}
