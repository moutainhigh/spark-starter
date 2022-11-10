package info.spark.starter.mq.support;

import com.fasterxml.jackson.annotation.JsonTypeName;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.SparkApplicationListener;
import info.spark.starter.mq.consumer.AbstractMessageHandler;
import info.spark.starter.mq.consumer.AbstractMessageNotifyHandler;
import info.spark.starter.mq.entity.AbstractMessage;
import info.spark.starter.mq.entity.Distribute;
import info.spark.starter.processor.annotation.AutoListener;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 管理器 </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.10 11:16
 * @since 1.7.0
 */
@Slf4j
@AutoListener
public class MessageProcessManager implements MessageRegister, MessageLoader, SparkApplicationListener {

    /**
     * 在 ContextStartedEvent 事件之前将 messageHandler 加载到对应的缓存中
     *
     * @param event event
     * @since 1.7.0
     */
    @Override
    public void onContextRefreshedEvent(ContextRefreshedEvent event) {
        Runner.executeAtLast(this.key(event, this.getClass()), () -> new MessageProcessFactoriesLoader().loadFactories(this));
    }

    /**
     * Register message handler
     *
     * @param messageType    message type
     * @param messageHandler message handler
     * @since 1.7.0
     */
    @Override
    public void registerMessageHandler(String messageType, @NotNull AbstractMessageHandler<?> messageHandler) {
        if (ConsumerCacheMaps.MESSAGE_HANDLER_CACHE.containsKey(messageType)) {
            log.warn("MessageType: {} 已被注册: {}", messageType,
                     ConsumerCacheMaps.MESSAGE_HANDLER_CACHE.get(messageType).getClass().getName());
            return;
        }

        log.info("register messageHandler success, {} -> {}", messageType, messageHandler.getClass().getName());
        ConsumerCacheMaps.MESSAGE_HANDLER_CACHE.put(messageType, messageHandler);
    }

    /**
     * Register message notify handler
     *
     * @param clazz          clazz
     * @param messageHandler message handler
     * @since 1.7.0
     */
    @Override
    public void registerMessageNotifyHandler(Class<?> clazz, @NotNull AbstractMessageNotifyHandler<?> messageHandler) {
        String name = clazz.getName();
        log.info("register messageNotifyHandler success, {} -> {}", name, messageHandler.getClass().getName());
        // 第一层
        ConsumerCacheMaps.MESSAGE_NOTIFY_HANDLER_CACHE.putIfAbsent(name, messageHandler);

        //第二层
        JsonTypeName annotation = AnnotationUtils.findAnnotation(clazz, JsonTypeName.class);
        if (null != annotation) {
            ConsumerCacheMaps.MESSAGE_NOTIFY_JSON_NAME_HANDLER_CACHE.putIfAbsent(annotation.value(), messageHandler);
        }
    }

    /**
     * Load message handler
     *
     * @param name name
     * @return the abstract message handler
     * @since 1.7.0
     */
    @Override
    public AbstractMessageHandler<? extends AbstractMessage> loadMessageHandler(String name) {
        return ConsumerCacheMaps.MESSAGE_HANDLER_CACHE.get(name);
    }

    /**
     * Load message notify handler
     *
     * @param paramMap   param map
     * @param distribute server names
     * @return the abstract message notify handler
     * @since 1.7.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public AbstractMessageNotifyHandler<?> loadMessageNotifyHandler(Map<String, Object> paramMap, Distribute distribute) {
        AbstractMessageNotifyHandler<?> handler = null;
        try {
            String messageTypeKey = (String) paramMap.get(ConsumerCacheMaps.ConsumerKeys.MESSAGE_HANDLER_KEY);
            String serverNameList = (String) ((Map<String, Object>) paramMap
                .get(ConsumerCacheMaps.ConsumerKeys.MESSAGE_RECEIVER))
                .get("serverName");

            // 如果发送方有明确指定`receiver`, 判断应用是否能接收
            if (null != serverNameList) {
                log.debug("消息发送方, 明确指定了消息接收者 [{}], currServer: {}", serverNameList, distribute.getCurrServer());
                if (ArrayUtils.indexOf(serverNameList.split(StringPool.COMMA), distribute.getCurrServer()) < 0) {
                    log.debug("本应用不处理该消息.");
                    return null;
                }
            }
            log.debug("从 notify_handlers 池里面查找handler, messageType: {}", messageTypeKey);
            handler = ConsumerCacheMaps.MESSAGE_NOTIFY_HANDLER_CACHE.get(messageTypeKey);

            // 从第二层缓存中查找，相同 @JsonTypeName value 的，针对不同应用，查找handler，并json序列化
            if (null == handler) {
                handler = this.loadMessageHandlerInJsonTypeMap(paramMap);
            }

        } catch (Exception e) {
            log.warn("从从notify-handlers池里面查找handler时异常", e);
        }
        return handler;
    }

    /**
     * 从第二层缓存中查找，相同 @JsonTypeName value 的，针对不同应用，查找handler，并json序列化
     *
     * @param paramMap param map
     * @return the abstract message notify handler
     * @since 1.7.0
     */
    @SuppressWarnings("unchecked")
    public AbstractMessageNotifyHandler<?> loadMessageHandlerInJsonTypeMap(Map<String, Object> paramMap) {
        // 从第二层缓存里面通过 json相同名称去取
        Object obj = paramMap.get(ConsumerCacheMaps.ConsumerKeys.MESSAGE_SOURCE);
        if (obj instanceof Map) {
            Object type = ((Map<String, Object>) obj).get("type");
            log.debug("从 json_type_notify_handlers 池里面查找handler, @JsonTypeName: {}", type);
            if (null != type) {
                return ConsumerCacheMaps.MESSAGE_NOTIFY_JSON_NAME_HANDLER_CACHE.get(type);
            }
        }
        return null;
    }

    /**
     * Handler size
     *
     * @return the integer
     * @since 1.7.0
     */
    public Integer handlerSize() {
        return ConsumerCacheMaps.MESSAGE_HANDLER_CACHE.size() + ConsumerCacheMaps.MESSAGE_NOTIFY_HANDLER_CACHE.size();
    }
}
