package info.spark.starter.cache.annotation;

import info.spark.starter.cache.exception.CacheLockException;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 12:21
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {

    /**
     * key 前缀
     *
     * @return the string
     * @since 1.0.0
     */
    String prefix() default "";

    /**
     * 支持 EL 表达式
     *
     * @return the string
     * @since 1.6.0
     */
    String key() default "";

    /**
     * 尝试获取锁的阻塞等待时间, 如果超过此时间还未获取到锁则会抛出 {@link CacheLockException}
     *
     * @return the int
     * @since 1.7.0
     */
    int timeout() default 3;

    /**
     * 超时释放锁的时间单位
     *
     * @return the time unit
     * @since 1.0.0
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 重试次数
     *
     * @return the int
     * @since 1.0.0
     */
    int retries() default 3;

    /**
     * 逻辑执行完成后是否自动释放锁, 可以通过 jetcache.cache-lock-key-expire 配置(默认 20 秒), 一旦应用启动则无法修改
     * 且只在 autoReleaseLock = false 时生效
     *
     * @return the boolean
     * @see info.spark.starter.cache.autoconfigure.CacheLockAutoConfiguration#redisLockRegistry
     * @see info.spark.starter.cache.autoconfigure.CacheProperties#cacheLockKeyExpire
     * @since 1.7.0
     */
    @SuppressWarnings("JavadocReference")
    boolean autoReleaseLock() default true;

}
