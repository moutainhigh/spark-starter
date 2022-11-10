package info.spark.starter.mq.support;

import info.spark.starter.mq.consumer.AbstractMessageHandler;
import info.spark.starter.mq.consumer.AbstractMessageNotifyHandler;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.10 11:37
 * @since 1.7.0
 */
public interface MessageRegister {

    /**
     * Register message handler
     *
     * @param messageType    message type
     * @param messageHandler message handler
     * @since 1.7.0
     */
    void registerMessageHandler(String messageType, @NotNull AbstractMessageHandler<?> messageHandler);

    /**
     * Register message notify handler
     *
     * @param clazz          clazz
     * @param messageHandler message handler
     * @since 1.7.0
     */
    void registerMessageNotifyHandler(Class<?> clazz, @NotNull AbstractMessageNotifyHandler<?> messageHandler);
}
