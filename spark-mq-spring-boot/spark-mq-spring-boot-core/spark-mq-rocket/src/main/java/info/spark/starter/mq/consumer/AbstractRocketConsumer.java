package info.spark.starter.mq.consumer;

import com.google.gson.JsonSyntaxException;

import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.common.util.GsonUtils;
import info.spark.starter.mq.entity.AbstractRocketMessage;

import org.apache.rocketmq.common.message.MessageExt;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: RocketMQ消费抽象基类
 *
 * @param <T> parameter
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.11 19:19
 * @since 1.7.0
 */
@Slf4j
public abstract class AbstractRocketConsumer<T extends AbstractRocketMessage> extends AbstractConsumer {
    /**
     * 反序列化解析消息
     *
     * @param message 消息体
     * @return 序列化结果 t
     * @since 1.7.0
     */
    protected T parseMessage(MessageExt message) {
        if (message == null || message.getBody() == null) {
            return null;
        }
        Type type = this.getMessageType();
        if (type instanceof Class) {
            try {
                return GsonUtils.fromJson(new String(message.getBody()), type);
            } catch (JsonSyntaxException e) {
                log.error("parse message json fail : {}", e.getMessage());
            }
        } else {
            log.warn("Parse msg error. {}", message);
        }
        return null;
    }

    /**
     * 解析消息类型
     *
     * @return 消息类型 message type
     * @since 1.7.0
     */
    protected Type getMessageType() {
        Type superType = this.getClass().getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Assertions.isTrue(actualTypeArguments.length == 1, "Number of type arguments must be 1");
            return actualTypeArguments[0];
        } else {
            // 如果没有定义泛型, 解析为Object
            return Object.class;
        }
    }
}
