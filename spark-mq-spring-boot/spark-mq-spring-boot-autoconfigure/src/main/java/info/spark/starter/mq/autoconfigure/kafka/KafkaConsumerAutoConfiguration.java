package info.spark.starter.mq.autoconfigure.kafka;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.util.StringUtils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 18:41
 * @since 1.5.0
 */
@Configuration(proxyBeanMethods = false)
@Conditional(value = KafkaAutoConfiguration.ConsumerCondition.class)
@ConditionalOnClass(DefaultKafkaConsumerFactory.class)
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConsumerAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Consumer factory consumer factory.
     *
     * @param properties properties
     * @return the consumer factory
     * @since 1.5.0
     */
    @Bean
    @ConditionalOnMissingBean
    public ConsumerFactory<String, Object> consumerFactory(@NotNull KafkaProperties properties) {
        Map<String, Object> configProps = KafkaAutoConfiguration.buildProperties(properties);

        if (StringUtils.isNotBlank(properties.getConsumer().getGroupId())) {
            configProps.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getConsumer().getGroupId());
        }
        if (null != properties.getConsumer().getAutoCommit()) {
            configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.getConsumer().getAutoCommit());
        }
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Kafka listener container factory concurrent kafka listener container factory.
     *
     * @param consumerFactory consumer factory
     * @return the concurrent kafka listener container factory
     * @since 1.5.0
     */
    @Bean
    @ConditionalOnMissingBean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
        ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
