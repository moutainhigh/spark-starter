package info.spark.starter.mq.consumer;

import info.spark.starter.mq.entity.AbstractRocketMessage;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * RocketMQ的消费者(Push模式)处理消息的接口
 *
 * @param <T> parameter
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.11 19:10
 * @since 1.7.0
 */
@Slf4j
public abstract class AbstractRocketPushConsumer<T extends AbstractRocketMessage> extends AbstractRocketConsumer<T> {

    /** Consumer */
    @Getter @Setter
    private DefaultMQPushConsumer consumer;

    /**
     * 并发多线程消费
     *
     * @param list                       消息列表
     * @param consumeConcurrentlyContext 上下文
     * @return 消费状态 consume concurrently status
     * @since 1.7.0
     */
    public ConsumeConcurrentlyStatus processMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        // todo-by-zhuBo: 暂不支持并发多线程消费, 仅支持 有序消费
        for (MessageExt messageExt : list) {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 原生dealMessage方法, 可以重写此方法自定义序列化和返回消费成功的相关逻辑
     *
     * @param list                  消息列表
     * @param consumeOrderlyContext 上下文
     * @return 处理结果 consume orderly status
     * @since 1.7.0
     */
    public ConsumeOrderlyStatus processMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
        for (MessageExt messageExt : list) {
            log.info("receive msgId: {}, tags: {}, msg: {}", messageExt.getMsgId(), messageExt.getTags(), messageExt);
            try {
                T t = this.parseMessage(messageExt);
                if (null != t) {
                    super.dispatcher(t);
                }
            } catch (Exception e) {
                log.warn("consume fail , ask for re-consume , msgId: {}", messageExt.getMsgId());
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
