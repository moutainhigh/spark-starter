package info.spark.starter.mq.provider;

import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.basic.util.StringUtils;
import info.spark.starter.mq.entity.AbstractRocketMessage;
import info.spark.starter.mq.entity.MqResults;
import info.spark.starter.mq.entity.SendResultInfo;
import info.spark.starter.mq.exception.MessageException;
import info.spark.starter.mq.utils.RocketDelayLevelUtils;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 16:27
 * @since 1.5.0
 */
@Slf4j
public class RocketMqProvider extends AbstractProvider<AbstractRocketMessage> {
    /** Default mq producer */
    private final DefaultMQProducer producer;

    /**
     * Rocket mq provider
     *
     * @param producer default mq producer
     * @since 1.7.0
     */
    public RocketMqProvider(DefaultMQProducer producer) {
        super();
        this.producer = producer;
    }

    /**
     * 同步发送消息
     *
     * @param message message
     * @since 1.5.0
     */
    public void send(AbstractRocketMessage message) {
        Message msg = this.checkAndBuildMsg(message);
        this.sendMsg(msg);
    }

    /**
     * 同步发送消息, 发送时出现异常, 业务端自行处理, 返回消息发送的结果
     *
     * @param message  message
     * @param timeout  timeout
     * @param timeUnit time unit
     * @return the mq results
     * @since 1.7.0
     */
    @Override
    public MqResults sendSync(AbstractRocketMessage message, Long timeout, TimeUnit timeUnit) {
        return this.sendSync(message, timeout, timeUnit, 0L);
    }

    /**
     * 异步发送消息, callback 返回分区、offset、结果bool
     *
     * @param message  message
     * @param callback callback
     * @throws Exception exception
     * @since 1.7.0
     */
    @Override
    public void sendAsync(AbstractRocketMessage message, @Nullable Consumer<MqResults> callback) throws Exception {
        Message msg = this.checkAndBuildMsg(message);
        this.producer.send(msg, (mqs, msg1, arg) -> {
            int value = arg.hashCode();
            if (value < 0) {
                value = Math.abs(value);
            }
            value = value % mqs.size();
            return mqs.get(value);
        }, message, new SendCallback() {

            @Override
            public void onSuccess(SendResult sendResult) {
                MqResults info = MqResults.builder()
                    .sendResultInfo(new SendResultInfo(sendResult.getMessageQueue().getQueueId(), sendResult.getQueueOffset()))
                    .build();
                if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    log.info("send msg result = [{}], topic = [{}]", sendResult, msg.getTopic());
                    info.setResult(true);
                } else {
                    log.error("send msg error. result = [{}], topic = [{}]", sendResult, msg.getTopic());
                    info.setResult(false);
                }
                if (null != callback) {
                    callback.accept(info);
                }
            }

            @Override
            public void onException(Throwable e) {
                log.error("send msg error.", e);
                if (null != callback) {
                    callback.accept(MqResults.builder()
                                        .result(false)
                                        .build());
                }
            }
        });
    }

    /**
     * Check and build msg
     *
     * @param message message
     * @return the message
     * @since 1.0.0
     */
    @NotNull
    private Message checkAndBuildMsg(AbstractRocketMessage message) {
        Assertions.notNull(message, "发送得消息 message 不能为空");
        Assertions.notBlank(message.getTopic(), "请设置 message topic 值");
        if (StringUtils.isBlank(message.getTag())) {
            message.setTag(StringPool.ASTERISK);
            log.debug("未指定消息的 tag, 默认设置为 *");
        }
        log.info("topic = {} message = {}", message.getTopic(), message);
        return new Message(message.getTopic(), message.getTag(),
                           message.getKey(),
                           JsonUtils.toJsonAsBytes(message));
    }


    /**
     * 发送同步请求
     *
     * @param message   message
     * @param timeout   timeout
     * @param timeUnit  time unit
     * @param delayTime delay time
     * @return the mq results
     * @since 1.7.0
     */
    public MqResults sendSync(AbstractRocketMessage message, Long timeout, TimeUnit timeUnit, Long delayTime) {
        Message msg = this.checkAndBuildMsg(message);
        if (null != delayTime && delayTime > 0) {
            msg.setDelayTimeLevel(RocketDelayLevelUtils.getSimilarLevel(delayTime));
        }
        long defaultTimeout = (null != timeout && null != timeUnit) ? timeUnit.toMillis(timeout) : DEFAULT_TIMEOUT;
        if (StringUtils.isBlank(message.getKey())) {
            message.setKey(StringUtils.randomUid());
        }
        SendResult sendResult;
        log.debug("send message: [{}]", message);
        try {
            sendResult = this.producer.send(msg, (mqs, msg1, arg) -> {
                int value = arg.hashCode();
                if (value < 0) {
                    value = Math.abs(value);
                }
                value = value % mqs.size();
                return mqs.get(value);
            }, message.getKey(), defaultTimeout);

            if (sendResult != null && SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                log.info("send msg result = [{}], topic = [{}]", sendResult, msg.getTopic());
                return MqResults.builder()
                    .sendResultInfo(new SendResultInfo(sendResult.getMessageQueue().getQueueId(), sendResult.getQueueOffset()))
                    .result(true)
                    .build();
            }
        } catch (Exception e) {
            final String errorMessage = StrFormatter.mergeFormat("消息发送失败: [{}:{}]", message.getKey(), message.getTopic());
            throw new MessageException(errorMessage, e);
        }
        return MqResults.builder()
            .result(false)
            .build();
    }

    /**
     * 通过MqProducer发送消息
     *
     * @param msg msg
     * @since 1.7.0
     */
    private void sendMsg(org.apache.rocketmq.common.message.Message msg) {
        try {
            SendResult sendResult = this.producer.send(msg);
            if (sendResult != null && SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                log.info("send msg result = [{}], topic = [{}], message = [{}]", sendResult, msg.getTopic(), msg);
            } else {
                log.error("send msg error. result = [{}], topic = [{}]", sendResult, msg.getTopic());
                // todo-by-zhuBo: 消息发送异常后应该重试或者其他某种机制处理异常得消息
            }
        } catch (Exception e) {
            log.error("send msg exception", e);
        }
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
        this.sendSync(message, null, null, time);

    }

}
