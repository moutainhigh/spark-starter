package info.spark.starter.mq.support;

import info.spark.starter.mq.consumer.AbstractMessageHandler;
import info.spark.starter.mq.consumer.AbstractMessageNotifyHandler;
import info.spark.starter.mq.entity.AbstractMessage;

import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.10 11:18
 * @since 1.7.0
 */
@Slf4j
public class ConsumerCacheMaps {

    /** messageHandlerRepository */
    static final ConcurrentHashMap<String, AbstractMessageHandler<? extends AbstractMessage>>
        MESSAGE_HANDLER_CACHE = new ConcurrentHashMap<>();

    /** 消息v5-message自定义handler接口实现 */
    static final ConcurrentHashMap<String, AbstractMessageNotifyHandler<? extends info.spark.starter.notify.AbstractMessage<String>>>
        MESSAGE_NOTIFY_HANDLER_CACHE = new ConcurrentHashMap<>();

    /** 消息v5-message自定义 handler key 消息实体上的 JsonTypeName */
    static final ConcurrentHashMap<String, AbstractMessageNotifyHandler<? extends info.spark.starter.notify.AbstractMessage<String>>>
        MESSAGE_NOTIFY_JSON_NAME_HANDLER_CACHE = new ConcurrentHashMap<>();


    /**
         * <p>Description: </p>
     *
     * @author wanghao
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.12.10 14:07
     * @since 1.7.0
     */
    public interface ConsumerKeys {
        /** messageType */
        String MESSAGE_HANDLER_KEY = "messageType";
        /** receiver */
        String MESSAGE_RECEIVER = "receiver";
        /** source */
        String MESSAGE_SOURCE = "source";
    }

}
