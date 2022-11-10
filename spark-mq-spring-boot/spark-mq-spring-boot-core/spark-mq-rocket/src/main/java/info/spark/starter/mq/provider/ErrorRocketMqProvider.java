package info.spark.starter.mq.provider;

import info.spark.starter.mq.entity.AbstractMessage;
import info.spark.starter.mq.entity.AbstractRocketMessage;
import info.spark.starter.mq.entity.MqResults;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>Description: </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.08 10:00
 * @since 1.7.0
 */
@Slf4j
public class ErrorRocketMqProvider extends RocketMqProvider implements ErrorMqProvider {

    /**
     * Rocket mq provider
     *
     * @since 1.7.0
     */
    public ErrorRocketMqProvider() {
        super(null);
    }

    /**
     * 同步发送消息，发送时出现异常，业务端自行处理，返回消息发送的结果
     *
     * @param message  message
     * @param timeout  timeout
     * @param timeUnit time unit
     * @return the mq results
     * @since 1.7.0
     */
    @Override
    public MqResults sendSync(AbstractRocketMessage message, Long timeout, TimeUnit timeUnit) {
        this.errorMsg();
        return null;
    }

    /**
     * 异步发送消息，callback 返回分区、offset、结果bool
     *
     * @param message  message
     * @param callback callback
     * @since 1.7.0
     */
    @Override
    public void sendAsync(AbstractRocketMessage message, @Nullable Consumer<MqResults> callback) {
        this.errorMsg();
    }

    /**
     * Send message
     *
     * @param message message
     * @since 1.7.0
     */
    @Override
    public void sendMessage(AbstractMessage message) {
        this.errorMsg();
    }

    /**
     * Send message
     *
     * @param topic   topic
     * @param message message
     * @since 1.7.0
     */
    @Override
    public void sendMessage(String topic, AbstractMessage message) {
        this.errorMsg();
    }

    /**
     * Send message
     *
     * @param topic       topic
     * @param messageType message type
     * @param content     content
     * @since 1.7.0
     */
    @Override
    public void sendMessage(String topic, @NotNull String messageType, String content) {
        this.errorMsg();
    }

    /**
     * Send delay message
     *
     * @param message message
     * @param time    time
     * @since 1.7.0
     */
    @Override
    public void sendDelayMessage(AbstractRocketMessage message, Long time) {
        this.errorMsg();
    }

    /**
     * Send delay message
     *
     * @param topic   topic
     * @param message message
     * @param time    time
     * @since 1.7.0
     */
    @Override
    public void sendDelayMessage(String topic, AbstractRocketMessage message, Long time) {
        this.errorMsg();
    }

    /**
     * Send delay message
     *
     * @param topic       topic
     * @param messageType message type
     * @param content     content
     * @param time        time
     * @since 1.7.0
     */
    @Override
    public void sendDelayMessage(String topic, @NotNull String messageType, String content, Long time) {
        this.errorMsg();
    }

    /**
     * Send
     *
     * @param topic   topic
     * @param message message
     * @since 1.7.0
     */
    @Override
    public void send(String topic, String message) {
        this.errorMsg();
    }

    /**
     * Send
     *
     * @param topic   topic
     * @param key     key
     * @param message message
     * @since 1.7.0
     */
    @Override
    public void send(String topic, @org.jetbrains.annotations.Nullable String key, String message) {
        this.errorMsg();
    }

    /**
     * Send
     *
     * @param topic   topic
     * @param message message
     * @since 1.7.0
     */
    @Override
    public void send(String topic, AbstractMessage message) {
        this.errorMsg();
    }

    /**
     * Delay send
     *
     * @param topic   topic
     * @param message message
     * @param time    time
     * @since 1.7.0
     */
    @Override
    public void delaySend(String topic, String message, Long time) {
        this.errorMsg();
    }

    /**
     * Delay send
     *
     * @param topic   topic
     * @param message message
     * @param time    time
     * @since 1.7.0
     */
    @Override
    public void delaySend(String topic, AbstractRocketMessage message, Long time) {
        this.errorMsg();
    }
}
