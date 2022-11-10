package info.spark.starter.mq;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description: 相对于 KafkaTemplate 只能创建一个分区, 此方式可以对 topic 定制化支持更好 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.23 11:25
 * @since 1.5.0
 */
@Configuration(proxyBeanMethods = false)
public class KafkaInitialConfiguration {

    /**
     * 自动创建 TopicName 为 topic.quick.initial 的 Topic 并设置分区数为 8 以及副本数为 1
     *
     * @return the new topic
     * @since 1.5.0
     */
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic("topic.quick.initial", 8, (short) 1);
    }

    /**
     * Kafka admin
     *
     * @return the kafka admin
     * @since 1.5.0
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>(2);
        // 配置 Kafka 实例的连接地址
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        return new KafkaAdmin(props);
    }

    /**
     * Admin client
     *
     * @return the admin client
     * @since 1.5.0
     */
    @Bean
    public AdminClient adminClient(KafkaAdmin kafkaAdmin) {
        return AdminClient.create(kafkaAdmin.getConfig());
    }
}
