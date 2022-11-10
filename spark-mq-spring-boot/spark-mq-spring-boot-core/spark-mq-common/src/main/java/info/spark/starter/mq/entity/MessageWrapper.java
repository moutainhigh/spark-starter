package info.spark.starter.mq.entity;

import info.spark.starter.mq.MessageConstant;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;

/**
 * <p>Description:  </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.15 01:39
 * @since 1.5.0
 */
public final class MessageWrapper<T> {
    /** Payload */
    private final T payload;
    /** Header accessor */
    private final MessageHeaderAccessor headerAccessor;

    /**
     * Message wrapper
     *
     * @param payload payload
     * @since 1.5.0
     */
    private MessageWrapper(T payload) {
        Assert.notNull(payload, "Payload must not be null");
        this.payload = payload;
        this.headerAccessor = new MessageHeaderAccessor();
    }


    /**
     * With payload
     *
     * @param <T>     parameter
     * @param payload payload
     * @return the @ not null message wrapper
     * @since 1.5.0
     */
    @Contract("_ -> new")
    public static <T> @NotNull MessageWrapper<T> withPayload(T payload) {
        return new MessageWrapper<>(payload);
    }

    /**
     * Trace id
     *
     * @param traceId trace id
     * @return the message wrapper
     * @since 1.5.0
     */
    @Contract("_ -> this")
    public MessageWrapper<T> traceId(String traceId) {
        this.headerAccessor.setHeader(MessageConstant.MESSAGE_TRACE_ID, traceId);
        return this;
    }

    /**
     * Message type
     *
     * @param messageType message type
     * @return the message wrapper
     * @since 1.5.0
     */
    @Contract("_ -> this")
    public MessageWrapper<T> messageType(String messageType) {
        this.headerAccessor.setHeader(MessageConstant.MESSAGE_TYPE, messageType);
        return this;
    }

    /**
     * Header
     *
     * @param key   key
     * @param value value
     * @return the message wrapper
     * @since 1.5.0
     */
    @Contract("_, _ -> this")
    public MessageWrapper<T> header(String key, Object value) {
        this.headerAccessor.setHeader(key, value);
        return this;
    }

    /**
     * Build
     *
     * @return the message
     * @since 1.5.0
     */
    public @NotNull Message<T> build() {
        MessageHeaders headersToUse = this.headerAccessor.toMessageHeaders();
        return new GenericMessage<>(this.payload, headersToUse);
    }

}
