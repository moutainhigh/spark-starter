package info.spark.starter.cache.autoconfigure;

import com.alicp.jetcache.autoconfigure.RedisLettuceAutoConfiguration;
import info.spark.starter.cache.KeyExpirationListenerAdapter;
import info.spark.starter.cache.RedisMessageSubscriber;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.util.CollectionUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.5.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.09 10:46
 * @since 1.5.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import(LettuceConnectionConfiguration.class)
@Conditional(RedisLettuceAutoConfiguration.RedisLettuceCondition.class)
public class RedisKeyExpirationAutoConfiguration implements SparkAutoConfiguration {
    /** Converter customizers */
    private final List<RedisMessageListenerContainerCustomizer> containerCustomizers;
    /** Redis message subscribers */
    private final List<RedisMessageSubscriber<?>> redisMessageSubscribers;

    /**
     * Redis jey expiration auto configuration
     *
     * @param containerCustomizers    container customizers
     * @param redisMessageSubscribers container customizers
     * @since 1.7.0
     */
    RedisKeyExpirationAutoConfiguration(@NotNull ObjectProvider<List<RedisMessageListenerContainerCustomizer>> containerCustomizers,
                                        @NotNull ObjectProvider<List<RedisMessageSubscriber<?>>> redisMessageSubscribers) {
        this.containerCustomizers = containerCustomizers.getIfAvailable();
        this.redisMessageSubscribers = redisMessageSubscribers.getIfAvailable();
    }

    /**
     * 处理 RedisKeyExpiredEvent 事件
     *
     * @return the key expiration listener
     * @since 1.5.0
     */
    @Bean
    KeyExpirationListenerAdapter keyExpirationListener() {
        return new KeyExpirationListenerAdapter();
    }

    /**
     * 开启监听器用于发送 {@link RedisKeyExpiredEvent} 事件
     *
     * @param container container
     * @return the key expiration event message listener
     * @since 1.5.0
     */
    @Bean
    KeyExpirationEventMessageListener keyExpirationEventMessageListener(RedisMessageListenerContainer container) {
        return new KeyExpirationEventMessageListener(container);
    }

    /**
     * Container
     *
     * @param connectionFactory connection factory
     * @return the redis message listener container
     * @since 1.0.0
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setErrorHandler(t -> log.warn("key 过期事件监听器内部错误: {}", t.getClass()));
        container.setConnectionFactory(connectionFactory);

        if (CollectionUtils.isNotEmpty(this.containerCustomizers)) {
            this.containerCustomizers.forEach(c -> c.customize(container));
        }
        if (CollectionUtils.isNotEmpty(this.redisMessageSubscribers)) {
            this.redisMessageSubscribers.forEach(c -> container.addMessageListener(c, new PatternTopic(c.topic())));
        }

        return container;
    }
}
