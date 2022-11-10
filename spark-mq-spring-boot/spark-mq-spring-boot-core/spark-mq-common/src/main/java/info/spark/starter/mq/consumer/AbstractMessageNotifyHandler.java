package info.spark.starter.mq.consumer;

import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.core.util.AopTargetUtils;
import info.spark.starter.mq.entity.MessageExtAdapter;
import info.spark.starter.util.ReflectionUtils;
import info.spark.starter.notify.AbstractMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.30 14:07
 * @since 1.7.0
 */
@Slf4j
public abstract class AbstractMessageNotifyHandler<T extends AbstractMessage<String>> {

    /** Message type */
    protected Class<T> messageType;

    {
        //noinspection unchecked
        this.messageType = (Class<T>) ReflectionUtils.getSuperClassGenericType(this.getClass(), 0);
    }

    /**
     * 接收消息并主题提交 ack
     *
     * @param content content
     * @return the boolean
     * @since 1.7.0
     */
    protected abstract boolean handler(T content);

    /**
     * 默认应该实现 {@link this#handler(T)}
     * 只要子类实现了该方法，消息优先将分发到该方法下，而忽略 {@link this#handler(T) }方法
     *
     * @param content           content
     * @param messageExtAdapter message ext adapter
     * @return the boolean
     * @since 1.8.0
     */
    protected boolean handler(T content, MessageExtAdapter messageExtAdapter) {
        return this.handler(content);
    }

    /**
     * 接收消息并主动提交 ack
     *
     * @param content           content
     * @param messageExtAdapter 消息元数据
     * @return the boolean
     * @throws Exception exception
     * @since 1.7.0
     */
    boolean handler(String content, MessageExtAdapter messageExtAdapter) throws Exception {
        if (null == this.messageType) {
            Object target = AopTargetUtils.getTarget(this);
            //noinspection unchecked
            this.messageType = (Class<T>) ReflectionUtils.getSuperClassGenericType(target.getClass(), 0);
        }
        return this.handler((JsonUtils.parse(content, this.messageType)), messageExtAdapter);
    }
}
