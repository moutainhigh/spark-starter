package info.spark.starter.mq.provider;

import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.mq.entity.AbstractMessage;
import info.spark.starter.mq.entity.MessageWrapper;
import info.spark.starter.mq.entity.MqResults;
import info.spark.starter.mq.entity.SendResultInfo;
import info.spark.starter.mq.entity.AbstractKafkaMessage;

import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.concurrent.ListenableFuture;

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
 * @date 2020.07.14 16:12
 * @since 1.5.0
 */
@Slf4j
public class KafkaProvier extends AbstractProvider<AbstractKafkaMessage> {

    /** Kafka template */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Kafka provier
     *
     * @param kafkaTemplate kafka template
     * @param defaultTopic  default topic
     * @since 1.5.0
     */
    public KafkaProvier(KafkaTemplate<String, Object> kafkaTemplate, String defaultTopic) {
        super();
        this.kafkaTemplate = kafkaTemplate;
        this.defaultTopic = defaultTopic;
    }

    /**
     * 发送 string 类型的数据
     *
     * @param topic   the topic
     * @param message the message
     * @since 1.5.0
     */
    @Override
    public void send(String topic, String message) {
        this.send(topic, null, message);
    }

    /**
     * 指定key, 进行key分区, 发送 string 类型的数据
     *
     * @param topic   the topic
     * @param key     key
     * @param message the message
     * @since 1.5.0
     */
    @Override
    public void send(String topic, @Nullable String key, String message) {
        log.debug("topic = {} message = {}", topic, message);
        ListenableFuture<SendResult<String, Object>> sendResult = this.kafkaTemplate.send(topic, key, message);

        sendResult.addCallback(result -> {
                                   if (result != null) {
                                       log.debug("producerRecord = {}, recordMetadata = {}",
                                                 result.getProducerRecord(),
                                                 result.getRecordMetadata());
                                   }
                               },
                               throwable -> log.error("", throwable));
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
    public MqResults sendSync(AbstractKafkaMessage message, Long timeout, TimeUnit timeUnit) {
        String topic = message.getTopic();
        log.debug("topic = {} message = {}", topic, message);
        ListenableFuture<SendResult<String, Object>> future = this.kafkaTemplate.send(topic, message.getKey(), message);
        long defaultTimeout = DEFAULT_TIMEOUT;
        if (null != timeout && null != timeUnit) {
            defaultTimeout = timeUnit.toMillis(timeout);
        }

        SendResult<String, Object> result = null;
        try {
            result = future.get(defaultTimeout, TimeUnit.MILLISECONDS);
            if (result != null) {
                log.debug("producerRecord = {}, recordMetadata = {}",
                          result.getProducerRecord(),
                          result.getRecordMetadata());
                return MqResults.builder()
                    .sendResultInfo(new SendResultInfo(result.getRecordMetadata().partition(), result.getRecordMetadata().offset()))
                    .result(true)
                    .build();
            }
        } catch (Exception e) {
            log.error("send msg error. result = [{}], topic = [{}]", result, topic);
        }
        return MqResults.builder()
            .result(false)
            .build();
    }

    /**
     * 指定key, 进行key分区, 异步发送消息, callback 返回分区、offset、结果bool
     *
     * @param message  message
     * @param callback callback
     * @throws Exception exception
     * @since 1.7.0
     */
    @Override
    public void sendAsync(AbstractKafkaMessage message, @Nullable Consumer<MqResults> callback) throws Exception {
        String topic = message.getTopic();
        log.debug("topic = {} message = {}", topic, message);
        ListenableFuture<SendResult<String, Object>> sendResult = this.kafkaTemplate.send(topic, message.getKey(), message);
        if (null == callback) {
            return;
        }
        sendResult.addCallback(result -> {
                                   if (result != null) {
                                       log.debug("producerRecord = {}, recordMetadata = {}",
                                                 result.getProducerRecord(),
                                                 result.getRecordMetadata());
                                       callback.accept(MqResults.builder()
                                                           .sendResultInfo(new SendResultInfo(result.getRecordMetadata().partition(),
                                                                                              result.getRecordMetadata().offset()))
                                                           .result(true)
                                                           .build()
                                                      );
                                   } else {
                                       callback.accept(MqResults.builder()
                                                           .result(false)
                                                           .build());
                                   }
                               },
                               throwable -> callback.accept(MqResults.builder()
                                                                .result(false)
                                                                .build()));
    }

    /**
     * 发送实体类型的数据
     *
     * @param topic   topic
     * @param message message
     * @see MessageBuilder
     * @since 1.5.0
     */
    @Override
    public void send(String topic, AbstractMessage message) {
        log.debug("topic = {} message = {}", topic, message);

        Message<AbstractMessage> data = MessageWrapper.withPayload(message)
            .messageType(message.getClass().getName())
            .traceId(MDC.get(BasicConstant.TRACE_ID))
            .header(KafkaHeaders.TOPIC, topic)
            .build();

        ListenableFuture<SendResult<String, Object>> sendResult = this.kafkaTemplate.send(data);

        sendResult.addCallback(result -> {
                                   if (result != null) {
                                       log.debug("producerRecord = {}, recordMetadata = {}",
                                                 result.getProducerRecord(),
                                                 result.getRecordMetadata());
                                   }
                               },
                               throwable -> log.error("", throwable));
    }

}
