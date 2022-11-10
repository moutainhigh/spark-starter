package info.spark.starter.pay.callback;

import info.spark.starter.common.context.SpringContext;
import info.spark.starter.util.StringUtils;
import info.spark.starter.util.ThreadUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;

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
@Component
public class CallbackAop {

    /** Redis lock registry */
    @Resource
    private RedisLockRegistry redisLockRegistry;

    /**
     * Cache lock point cut *
     *
     * @param callback callback
     * @since 1.0.0
     */
    @Pointcut("@annotation(callback)")
    public void cacheLockPointCut(Callback callback) {
        // nothing to do
    }

    /**
     * Around object
     *
     * @param joinPoint join point
     * @param callback  callback
     * @return the object
     * @throws Throwable throwable
     * @since 1.0.0
     */
    @Around(value = "cacheLockPointCut(callback)", argNames = "joinPoint,callback")
    public Object around(@NotNull ProceedingJoinPoint joinPoint, @NotNull Callback callback)
        throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Class<? extends CallbackService> callbackServiceClass = callback.value();

        // 获取分布式锁的 key
        String key = method.getName() + ":" + callbackServiceClass.getName();

        Lock lock = this.redisLockRegistry.obtain(key);

        boolean lockFlag = false;
        int retries = callback.retries() <= 0 ? 1 : callback.retries();
        // 循环 retries 次去尝试获取锁
        for (int i = 0; i < retries; i++) {
            lockFlag = lock.tryLock(callback.timeout(), callback.timeUnit());
            if (lockFlag) {
                break;
            }
        }

        if (!lockFlag) {
            // 如果没有获取到分布式锁, 说明已有线程正在处理回调业务, 这里直接返回失败, 如果第三方存在回调重试, 可由一下次回调继续处理(需要保证回调业务幂等性),
            // 如果第三方回调没有重试机制, 此时正在处理的线程会保证当前回调能执行成功
            throw new RuntimeException(StringUtils.format("尝试 {} 次获取分布式锁失败, 耗时: [{} {}] 直接返回失败 ",
                                                          retries,
                                                          retries * callback.timeout(),
                                                          callback.timeUnit().name()));
        }

        //获取参数
        Object[] args = joinPoint.getArgs();
        log.info("回调参数: [{}]", Arrays.toString(args));

        try {
            log.info("[{}] 成功获取到分布式锁, 开始检查", Thread.currentThread().getName());

            CallbackService callbackService = SpringContext.getInstance(callbackServiceClass.getSimpleName());

            for (int i = 0; i < retries; i++) {
                if (callbackService.check(args)) {
                    log.info("业务检查通过, 开始执行回调业务逻辑");
                    // 满足业务要求, 可执行业务回调逻辑
                    return joinPoint.proceed();
                } else {
                    // todo-dong4j : (2020年04月04日 17:31) [记录回调日志失败(未处理回调业务)]
                    log.error("记录回调失败日志");
                }

                // 保存重试日志
                ThreadUtils.sleep(1000);
            }

            // 循环结束后, 还没有执行回调逻辑, 说明一直不满足业务要求, 这里将进行日志记录, 便于后期补偿.
            throw new RuntimeException("回调失败");
        } finally {
            try {
                lock.unlock();
            } catch (Exception e) {
                // 如果 redis < 4.0, 将抛出 The UNLINK command has failed (not supported on the Redis server?)
                log.error(e.getMessage(), e);
            }
        }
    }
}
