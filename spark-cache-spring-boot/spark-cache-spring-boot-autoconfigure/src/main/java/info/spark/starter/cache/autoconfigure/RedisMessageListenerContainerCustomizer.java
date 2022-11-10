package info.spark.starter.cache.autoconfigure;

import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * <p>Description: {@link RedisMessageListenerContainer} 定制接口 </p>
 *
 * @author dong4j
 * @version 1.0.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.07 00:14
 * @since 1.0.0
 */
@FunctionalInterface
public interface RedisMessageListenerContainerCustomizer {

    /**
     * 用于自定义 {@link RedisMessageListenerContainer} 实例的回调
     *
     * @param container container
     * @since 1.0.0
     */
    void customize(RedisMessageListenerContainer container);

}
