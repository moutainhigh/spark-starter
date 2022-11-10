package info.spark.starter.mq;

import info.spark.starter.util.ThreadUtils;
import info.spark.starter.test.SparkTest;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.23 11:10
 * @since 1.5.0
 */
@Slf4j
@SparkTest(classes = KafkaConfiguration.class)
@Import(KafkaConfigurationTest.KafkaSendResultHandler.class)
class KafkaConfigurationTest {
    /** Kafka template */
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /** Kafka send result handler */
    @Resource
    private KafkaSendResultHandler kafkaSendResultHandler;

    /**
     * 同步发送消息
     *
     * @throws ExecutionException   execution exception
     * @throws InterruptedException interrupted exception
     * @since 1.5.0
     */
    @Test
    void testSyncSend() throws ExecutionException, InterruptedException {
        this.kafkaTemplate.send("topic.quick.demo", "test sync send message").get();
    }

    /**
     * kafkaTemplate 默认异步发送.
     *
     * @since 1.5.0
     */
    @Test
    void test_send() {
        this.kafkaTemplate.setProducerListener(this.kafkaSendResultHandler);
        this.kafkaTemplate.send("topic.quick.demo", "this is my first demo");

        // 发送带有时间戳的消息
        this.kafkaTemplate.send("topic.quick.demo", 0, System.currentTimeMillis(), "key", "send message with timestamp");

        // 使用ProducerRecord发送消息
        ProducerRecord<String, String> record = new ProducerRecord<>("topic.quick.demo", "use ProducerRecord to send message");
        this.kafkaTemplate.send(record);

        //使用Message发送消息
        Map<String, Object> map = new HashMap<>(4);
        map.put(KafkaHeaders.TOPIC, "topic.quick.demo");
        map.put(KafkaHeaders.PARTITION_ID, 0);
        map.put(KafkaHeaders.MESSAGE_KEY, "xxx");
        GenericMessage<String> message = new GenericMessage<>("use Message to send message", new MessageHeaders(map));
        this.kafkaTemplate.send(message);

        ThreadUtils.join();
    }

    /**
     * 事务测试
     *
     * @throws InterruptedException interrupted exception
     * @since 1.5.0
     */
    @Test
    @Transactional
    void testTransactionalAnnotation() throws InterruptedException {
        this.kafkaTemplate.send("topic.quick.tran", "test transactional annotation");
        throw new RuntimeException("fail");
    }

    /**
     * 此事务方式不需要配置事务管理器
     *
     * @throws InterruptedException interrupted exception
     * @since 1.5.0
     */
    @Test
    void testExecuteInTransaction() throws InterruptedException {
        this.kafkaTemplate.executeInTransaction(new KafkaOperations.OperationsCallback<String, String, String>() {
            /**
             * Do in operations
             *
             * @param kafkaOperations kafka operations
             * @return the object
             * @since 1.5.0
             */
            @Override
            public @NotNull String doInOperations(@NotNull KafkaOperations<String, String> kafkaOperations) {
                kafkaOperations.send("topic.quick.tran", "test executeInTransaction");
                throw new RuntimeException("fail");
            }
        });
    }

    /**
     * 声明consumerID为demo, 监听topicName为topic.quick.demo的Topic
     * 其他可用参数:
     * listen1(String data)
     * listen2(ConsumerRecord<K,V> data)
     * listen3(ConsumerRecord<K,V> data, Acknowledgment acknowledgment)
     * listen4(ConsumerRecord<K,V> data, Acknowledgment acknowledgment, Consumer<K,V> consumer)
     * listen5(List<String> data)
     * listen6(List<ConsumerRecord<K,V>> data)
     * listen7(List<ConsumerRecord<K,V>> data, Acknowledgment acknowledgment)
     * listen8(List<ConsumerRecord<K,V>> data, Acknowledgment acknowledgment, Consumer<K,V> consumer)
     *
     * @param msgData msg data
     * @since 1.5.0
     */
    @KafkaListener(id = "demo", topics = "topic.quick.demo")
    public void listen(String msgData) {
        log.info("demo receive : " + msgData);
    }

    /**
     * Test send 2
     *
     * @since 1.5.0
     */
    @Test
    void test_send_2() {
        this.kafkaTemplate.send("topic.quick.anno", "this is my first anno");
    }

    /**
     * Anno listener
     *
     * @param data      data
     * @param key       key
     * @param partition partition
     * @param topic     topic
     * @param ts        ts
     * @since 1.5.0
     */
    @KafkaListener(id = "anno", topics = "topic.quick.anno")
    public void annoListener(@Payload String data,
                             @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key,
                             @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                             @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
        log.info("topic.quick.anno receive : \n" +
                 "data : " + data + "\n" +
                 "key : " + key + "\n" +
                 "partitionId : " + partition + "\n" +
                 "topic : " + topic + "\n" +
                 "timestamp : " + ts + "\n"
                );

    }

    /**
         * <p>Description: 回调 </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.23 11:43
     * @since 1.5.0
     */
    @Slf4j
    static class KafkaSendResultHandler implements ProducerListener<String, String> {

        /**
         * On success
         *
         * @param producerRecord producer record
         * @param recordMetadata record metadata
         * @since 1.5.0
         */
        @Override
        public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
            log.info("Message send success : " + producerRecord.toString());
        }

        /**
         * On error
         *
         * @param producerRecord producer record
         * @param exception      exception
         * @since 1.5.0
         */
        @Override
        public void onError(ProducerRecord producerRecord, Exception exception) {
            log.info("Message send error : " + producerRecord.toString());
        }
    }
}
