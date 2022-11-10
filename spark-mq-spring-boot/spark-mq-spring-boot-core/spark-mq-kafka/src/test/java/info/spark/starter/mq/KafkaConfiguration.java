package info.spark.starter.mq;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.23 11:09
 * @since 1.5.0
 */
@EnableKafka
@Configuration(proxyBeanMethods = false)
public class KafkaConfiguration {

    /**
     * ConcurrentKafkaListenerContainerFactory为创建Kafka监听器的工程类, 这里只配置了消费者
     *
     * @return the concurrent kafka listener container factory
     * @since 1.5.0
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    /**
     * 根据consumerProps填写的参数创建消费者工厂
     *
     * @return the consumer factory
     * @since 1.5.0
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(this.consumerProps());
    }

    /**
     * 根据senderProps填写的参数创建生产者工厂
     *
     * @return the producer factory
     * @since 1.5.0
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        DefaultKafkaProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(this.senderProps());
        factory.transactionCapable();
        factory.setTransactionIdPrefix("tran-");
        return factory;
    }

    /**
     * 事务支持
     *
     * @param producerFactory producer factory
     * @return the kafka transaction manager
     * @since 1.5.0
     */
    @Bean
    public KafkaTransactionManager<String, String> transactionManager(ProducerFactory<String, String> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    /**
     * kafkaTemplate实现了Kafka发送接收等功能 (创建出来的 Topic 的 Partition (分区) 数永远只有 1 个)
     *
     * @return the kafka template
     * @since 1.5.0
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(this.producerFactory());
    }

    /**
     * 消费者配置参数
     *
     * @return the map
     * @since 1.5.0
     */
    private @NotNull Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>(8);
        //连接地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //GroupID
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bootKafka");
        //是否自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        //自动提交的频率
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        //Session超时设置
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        //键的反序列化方式
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //值的反序列化方式
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    /**
     * 生产者配置
     *
     * @return the map
     * @since 1.5.0
     */
    private @NotNull Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>(8);
        //连接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //重试, 0为不启用重试机制
        props.put(ProducerConfig.RETRIES_CONFIG, 1);
        //控制批处理大小, 单位为字节
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        //批量发送, 延迟为1毫秒, 启用该功能能有效减少生产者发送消息次数, 从而提高并发量
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        //生产者可以使用的总内存字节来缓冲等待发送到服务器的记录
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1024000);
        //键的序列化方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //值的序列化方式
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

}
