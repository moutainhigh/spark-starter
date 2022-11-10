package info.spark.starter.cache;

import com.alicp.jetcache.CacheValueHolder;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.common.context.SpringContext;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: redis 订阅抽象类 </p>
 *
 * @param <T> parameter
 * @author liujintao
 * @version 1.7.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.12.24 09:22
 * @since 1.7.0
 */
@Slf4j
public abstract class RedisMessageSubscriber<T extends Serializable> implements MessageListener {

    /** TOPIC_PREFIX */
    public static final String TOPIC_PREFIX = "topic.";
    /** Topic */
    private final String topic;

    /**
     * Redis message subscriber
     *
     * @param topic topic
     * @since 1.7.0
     */
    @Contract(pure = true)
    protected RedisMessageSubscriber(String topic) {
        this.topic = topic;
    }

    /**
     * Topic
     *
     * @return the string
     * @since 1.7.0
     */
    @Contract(pure = true)
    public final @NotNull String topic() {
        return TOPIC_PREFIX + this.topic;
    }

    /**
     * On message
     *
     * @param message message
     * @param pattern pattern
     * @since 1.7.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public final void onMessage(@NotNull Message message, byte[] pattern) {
        try {
            RedisSerializer<?> serializer = Holder.getInstance().getValueSerializer();
            Object deserialize = serializer.deserialize(message.getBody());
            T obj = null;
            if (deserialize != null) {
                obj = (T) deserialize;
            }

            if (deserialize instanceof CacheValueHolder) {
                obj = ((CacheValueHolder<T>) deserialize).getValue();
            }
            log.info("处理 redis 订阅消息, topic:[{}], body:[{}]", new String(message.getChannel(), Charsets.UTF_8), obj);
            this.handle(obj);
        } catch (Exception e) {
            log.error("处理 redis 订阅消息异常", e);
        }
    }

    /**
     * Handle
     * 消息订阅处理方法
     *
     * @param o o
     * @since 1.7.0
     */
    public abstract void handle(T o);

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.12.26 19:53
     * @since 1.7.0
     */
    private static class Holder {
        /** INSTANCE */
        @SuppressWarnings("unchecked")
        private static final RedisTemplate<String, Object> INSTANCE = SpringContext.getInstance(RedisTemplate.class);

        /**
         * Gets instance *
         *
         * @return the instance
         * @since 1.7.0
         */
        @Contract(pure = true)
        private static RedisTemplate<String, Object> getInstance() {
            return INSTANCE;
        }
    }
}
