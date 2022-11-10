package info.spark.starter.cache.aop;

import info.spark.starter.basic.support.NamedThreadFactory;
import info.spark.starter.cache.annotation.CacheLock;
import info.spark.starter.cache.el.AspectSupportUtils;
import info.spark.starter.cache.exception.CacheLockException;
import info.spark.starter.cache.util.CacheLockUtils;
import info.spark.starter.common.timer.HashedWheelTimer;
import info.spark.starter.util.StringUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: spring-integration 对 redis 分布锁的支持,底层应该也是 lua 脚本的实现,可完美解决线程挂掉造成的死锁,以及执行时间过长锁释放掉,误删别人的锁 </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 12:26
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class CacheLockAspect implements DisposableBean {
    /** Hashed wheel timer */
    private final HashedWheelTimer hashedWheelTimer;
    /** Cache lock key expire */
    private final Long cacheLockKeyExpire;
    /** Redis lock registry */
    private final RedisLockRegistry redisLockRegistry;

    /**
     * Cache lock aspect
     *
     * @param cacheLockKeyExpire cache lock key expire
     * @param redisLockRegistry  redis lock registry
     * @since 2.1.0
     */
    @SuppressWarnings("PMD.UndefineMagicConstantRule")
    public CacheLockAspect(Long cacheLockKeyExpire, RedisLockRegistry redisLockRegistry) {
        this.cacheLockKeyExpire = cacheLockKeyExpire;
        this.redisLockRegistry = redisLockRegistry;

        int wheelSize = cacheLockKeyExpire >= 2000L ? Integer.parseInt(cacheLockKeyExpire / 1000 + "") : 1;

        this.hashedWheelTimer =
            new HashedWheelTimer(new NamedThreadFactory("LockReleaseTimer", true),
                                 1L, TimeUnit.SECONDS,
                                 wheelSize);
    }

    /**
     * Cache lock point cut *
     *
     * @param cacheLock cache lock
     * @since 1.0.0
     */
    @Pointcut("@annotation(cacheLock)")
    public void cacheLockPointCut(CacheLock cacheLock) {
        // nothing to do
    }

    /**
     * Around object
     *
     * @param joinPoint join point
     * @param cacheLock cache lock
     * @return the object
     * @throws Throwable xxx
     * @since 1.0.0
     */
    @Around(value = "cacheLockPointCut(cacheLock)", argNames = "joinPoint,cacheLock")
    public Object around(@NotNull ProceedingJoinPoint joinPoint, @NotNull CacheLock cacheLock)
        throws Throwable {

        if (StringUtils.isEmpty(cacheLock.prefix())) {
            throw new CacheLockException("prefix 不能为空, 请在 @CacheLock 中定义 prefix");
        }

        String key = this.buildKey(joinPoint, cacheLock);
        Lock lock = CacheLockUtils.lock(redisLockRegistry,
                                        key,
                                        cacheLock.retries(),
                                        cacheLock.timeout(),
                                        cacheLock.timeUnit());
        try {
            log.info("[{}] 成功获取到分布式锁: [{}], 开始执行...", Thread.currentThread().getName(), key);
            return joinPoint.proceed();
        } finally {
            // 如果设置了 autoReleaseLock 则立即释放，否则使用 RedisLockRegistry 配置。
            if (cacheLock.autoReleaseLock()) {
                try {
                    log.info("任务结束删除分布式锁: [{}]", key);
                    lock.unlock();
                } catch (Exception e) {
                    // 如果 redis <4.0, 将抛出 The UNLINK command has failed (not supported on the Redis server?)
                    log.error(e.getMessage(), e);
                }
            } else {
                hashedWheelTimer.newTimeout(timeout -> {
                                                log.info("[{} ms] 超时自动删除分布式锁: [{}]", cacheLockKeyExpire, key);
                                                redisLockRegistry.expireUnusedOlderThan(100L);
                                            },
                                            cacheLockKeyExpire,
                                            TimeUnit.MILLISECONDS);

            }
        }
    }

    /**
     * Gets cache keys *
     *
     * @param joinPoint join point
     * @param cacheLock cache lock
     * @return the cache keys
     * @since 1.0.0
     */
    @NotNull
    private String buildKey(@NotNull ProceedingJoinPoint joinPoint,
                            @NotNull CacheLock cacheLock) {
        return cacheLock.prefix() + AspectSupportUtils.getKeyValue(joinPoint, cacheLock.key());
    }

    /**
     * Destroy
     *
     * @since 2.1.0
     */
    @Override
    public void destroy() {
        log.info("关闭 [{}]", hashedWheelTimer);
        hashedWheelTimer.stop();
    }
}
