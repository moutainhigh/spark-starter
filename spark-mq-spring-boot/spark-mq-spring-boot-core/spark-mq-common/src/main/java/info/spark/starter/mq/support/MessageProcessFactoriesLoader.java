package info.spark.starter.mq.support;

import info.spark.starter.common.context.EarlySpringContext;
import info.spark.starter.util.ClassUtils;
import info.spark.starter.mq.annotation.MessageHandler;
import info.spark.starter.mq.consumer.AbstractMessageHandler;
import info.spark.starter.mq.consumer.AbstractMessageNotifyHandler;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: messageHandler 装载器 </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.10 11:52
 * @since 1.7.0
 */
@Slf4j
public class MessageProcessFactoriesLoader {

    /**
     * Load
     *
     * @param processManager process manager
     * @since 1.7.0
     */
    public void loadFactories(MessageProcessManager processManager) {
        // 获取所有被 @MessageHandler 标识的 handler
        Map<String, Object> handlerMap = EarlySpringContext.getBeansWithAnnotation(MessageHandler.class);

        for (Object messageHandler : handlerMap.values()) {
            if (messageHandler instanceof AbstractMessageHandler) {
                Class<?> superClassT = this.getSuperClassNotProxy(messageHandler.getClass(), 0);
                String messageType = superClassT.getName();
                AbstractMessageHandler<?> handler = (AbstractMessageHandler<?>) messageHandler;

                processManager.registerMessageHandler(messageType, handler);

            } else if (messageHandler instanceof AbstractMessageNotifyHandler) {
                Class<?> superClassT = this.getSuperClassNotProxy(messageHandler.getClass(), 0);

                processManager.registerMessageNotifyHandler(superClassT, (AbstractMessageNotifyHandler<?>) messageHandler);

            } else {
                log.warn("被 @MessageHandler 标识的类必须继承 AbstractMessageHandler");
            }
        }
    }

    /**
     * 如果是代理对象，获取父类，再找继承的父类的实际泛型
     *
     * @param clazz clazz
     * @param index index
     * @return the super class not proxy
     * @since 1.7.0
     */
    public Class<?> getSuperClassNotProxy(Class<?> clazz, int index) {
        return ClassUtils.getSuperClassT(ClassUtils.getClass(clazz), index);
    }

}
