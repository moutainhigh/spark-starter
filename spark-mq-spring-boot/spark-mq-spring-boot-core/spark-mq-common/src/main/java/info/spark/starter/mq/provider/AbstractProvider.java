package info.spark.starter.mq.provider;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.mq.MessageConstant;
import info.spark.starter.mq.entity.AbstractMessage;
import info.spark.starter.mq.entity.MqResults;
import info.spark.starter.mq.exception.MessageException;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 向外暴露的发送消息接口 </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 15:54
 * @since 1.5.0
 */
@Slf4j
public abstract class AbstractProvider<T extends AbstractMessage> {
    /** Pool */
    private final ExecutorService pool;
    /** DEFAULT_TIMEOUT */
    protected static final Long DEFAULT_TIMEOUT = 3000L;

    /**
     * Instantiates a new Base message service.
     *
     * @since 1.5.0
     */
    public AbstractProvider() {
        // 初始化线程池
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("provider-%d").build();
        int corePoolSize = 2 * Runtime.getRuntime().availableProcessors() + 1;
        this.pool = new ThreadPoolExecutor(corePoolSize,
                                           Math.max(50, corePoolSize),
                                           100L,
                                           TimeUnit.MILLISECONDS,
                                           new LinkedBlockingQueue<>(1024),
                                           namedThreadFactory,
                                           new ThreadPoolExecutor.AbortPolicy());

    }

    /** 默认 topic, 由子类设置 */
    protected String defaultTopic;

    /**
     * To string
     *
     * @param messageType message type
     * @param content     content
     * @return the string
     * @since 1.5.0
     */
    private @NotNull String toString(@NotNull String messageType, String content) {
        Map<String, String> stringMap = new HashMap<>(4);
        stringMap.put(MessageConstant.MESSAGE_TYPE, messageType);
        stringMap.put(MessageConstant.MESSAGE_TRACE_ID, MDC.get(BasicConstant.TRACE_ID));
        stringMap.put(MessageConstant.MESSAGE_CONTENT, content);
        return JsonUtils.toJson(stringMap);
    }

    /**
     * Check and set trace
     *
     * @param topic topic
     * @since 1.5.0
     */
    private void checkTopic(String topic) {
        if (StringUtils.isBlank(topic)) {
            throw new MessageException("topic 不能为空");
        }
    }

    /**
     * 对外暴露的操作, 发送消息到默认 topic
     *
     * @param message the message       消息实体
     * @since 1.5.0
     */
    public void sendMessage(AbstractMessage message) {
        this.sendMessage(this.defaultTopic, message);
    }

    /**
     * 发送实体类型的数据, 将 type 和 traceId 写入 header 中.
     *
     * @param topic   the topic
     * @param message the message
     * @since 1.5.0
     */
    public void sendMessage(String topic, AbstractMessage message) {
        this.checkTopic(topic);

        try {
            this.pool.execute(() -> {
                // 点对点发送消息
                AbstractProvider.this.send(topic, message);
            });
        } catch (Exception e) {
            log.error("send message error.", e);
        }
    }

    /**
     * 发送的是 string 类型的数据, 则将 type 和 traceId 写入到消息体中.
     *
     * @param topic       the topic
     * @param messageType the message type
     * @param content     the content
     * @since 1.5.0
     */
    public void sendMessage(String topic, @NotNull String messageType, String content) {
        String message = this.toString(messageType, content);
        try {
            this.pool.execute(() -> {
                log.debug("starting send message, type = {}, content = {}", messageType, content);
                // 点对点发送消息
                AbstractProvider.this.send(topic, message);
            });
        } catch (Exception e) {
            log.error("send message error.", e);
        }
    }

    /**
     * 对外暴露的操作, 发送消息到默认 topic
     *
     * @param message the message       消息实体
     * @param time    time
     * @since 1.5.0
     */
    public void sendDelayMessage(T message, Long time) {
        this.sendDelayMessage(this.defaultTopic, message, time);
    }

    /**
     * 对外暴露的操作, 发送消息到指定 topic
     *
     * @param topic   the topic
     * @param message the message
     * @param time    time
     * @since 1.5.0
     */
    public void sendDelayMessage(String topic, T message, Long time) {
        this.checkTopic(topic);

        try {
            this.pool.execute(() -> {
                // 点对点发送消息
                AbstractProvider.this.delaySend(topic, message, time);
            });
        } catch (Exception e) {
            log.error("send message error.", e);
        }
    }

    /**
     * Send message.
     *
     * @param topic       the topic
     * @param messageType the message type
     * @param content     the content
     * @param time        time
     * @since 1.5.0
     */
    public void sendDelayMessage(String topic, @NotNull String messageType, String content, Long time) {
        String message = this.toString(messageType, content);
        try {
            this.pool.execute(() -> {
                log.debug("starting send message, type = {}, message = {}", messageType, content);
                // 点对点发送消息
                AbstractProvider.this.delaySend(topic, message, time);
            });
        } catch (Exception e) {
            log.error("send message error.", e);
        }
    }

    /**
     * 调用子类发送消息
     *
     * @param topic   the topic
     * @param message the message
     * @since 1.5.0
     */
    protected void send(String topic, String message) {
        log.warn("未实现 provider.info.spark.starter.mq.AbstractProvider.send(java.lang.String, java.lang.String)");
    }

    /**
     * 调用子类发送消息
     *
     * @param topic   the topic
     * @param key     key
     * @param message the message
     * @since 1.5.0
     */
    protected void send(String topic, @Nullable String key, String message) {
        log.warn("未实现 provider.info.spark.starter.mq.AbstractProvider.send(java.lang.String, java.lang.String, java.lang.String)");
    }

    /**
     * Send
     *
     * @param topic   topic
     * @param message message
     * @since 1.5.0
     */
    protected void send(String topic, AbstractMessage message) {
        log.warn("未实现 provider.info.spark.starter.mq.AbstractProvider.send(java.lang.String, "
                 + "entity.info.spark.starter.mq.AbstractMessage)");
    }

    /**
     * Delay send.
     *
     * @param topic   the topic
     * @param message the text message
     * @param time    the time
     * @since 1.5.0
     */
    protected void delaySend(String topic, String message, Long time) {
        log.warn("未实现 provider.info.spark.starter.mq.AbstractProvider.delaySend(java.lang.String, java.lang.String, java.lang.Long)");
    }

    /**
     * 异步发送消息, callback 返回分区、offset、结果bool
     * callback 可以传null, 如果null, 只需发消息
     *
     * @param message  message
     * @param callback callback
     * @throws Exception exception
     * @since 1.7.0
     */
    protected abstract void sendAsync(T message, @Nullable Consumer<MqResults> callback) throws Exception;

    /**
     * 同步发送消息, 发送时出现异常, 业务端自行处理, 返回消息发送的结果
     *
     * @param message  message
     * @param timeout  timeout
     * @param timeUnit time unit
     * @return the mq results
     * @since 1.7.0
     */
    protected abstract MqResults sendSync(T message, Long timeout, TimeUnit timeUnit);

    /**
     * Delay send
     *
     * @param topic   topic
     * @param message message
     * @param time    time
     * @since 1.5.0
     */
    protected void delaySend(String topic, T message, Long time) {
        log.warn("未实现 provider.info.spark.starter.mq.AbstractProvider.delaySend(java.lang.String, "
                 + "entity.info.spark.starter.mq.AbstractMessage, "
                 + "java.lang.Long)");
    }

}
