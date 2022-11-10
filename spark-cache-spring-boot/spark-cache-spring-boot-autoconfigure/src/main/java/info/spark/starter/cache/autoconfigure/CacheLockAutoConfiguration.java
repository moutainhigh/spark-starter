package info.spark.starter.cache.autoconfigure;

import com.alicp.jetcache.autoconfigure.RedisLettuceAutoConfiguration;
import info.spark.starter.cache.annotation.CacheLock;
import info.spark.starter.cache.aop.CacheLockAspect;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 基于 Redis 的分布式锁自动配置类 </p>
 * 注意: 只有配置了 redis 相关配置才会开启
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.19 00:27
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import(LettuceConnectionConfiguration.class)
@EnableConfigurationProperties(CacheProperties.class)
@Conditional(RedisLettuceAutoConfiguration.RedisLettuceCondition.class)
public class CacheLockAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Redis lock registry redis lock registry
     *
     * @param redisConnectionFactory redis connection factory
     * @param cacheProperties        cache properties
     * @return the redis lock registry
     * @see CacheProperties#getCacheLockKeyExpire()
     * @see CacheLock#autoReleaseLock()
     * @since 1.0.0
     */
    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory,
                                               @NotNull CacheProperties cacheProperties) {
        return new RedisLockRegistry(redisConnectionFactory,
                                     ConfigKit.getAppName() + ":locks",
                                     cacheProperties.getCacheLockKeyExpire());
    }

    /**
     * Cache lock aop cache lock aop
     *
     * @param cacheProperties   cache properties
     * @param redisLockRegistry redis lock registry
     * @return the cache lock aop
     * @since 1.0.0
     */
    @Bean
    public CacheLockAspect cacheLockAspect(@NotNull CacheProperties cacheProperties,
                                           RedisLockRegistry redisLockRegistry) {
        log.debug("已配置分布式缓存, 初始化 CacheLockAspect");
        return new CacheLockAspect(cacheProperties.getCacheLockKeyExpire(), redisLockRegistry);
    }
}
