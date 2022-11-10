package info.spark.starter.mq.autoconfigure.kafka;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.mq.provider.ErrorKafkaMqProvider;
import info.spark.starter.mq.provider.KafkaProvier;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 16:15
 * @since 1.5.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DefaultKafkaProducerFactory.class)
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaProviderAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Producer factory producer factory.
     *
     * @param properties the properties
     * @return the producer factory
     * @since 1.5.0
     */
    @Bean
    @Conditional(value = KafkaAutoConfiguration.ProviderCondition.class)
    public ProducerFactory<String, Object> producerFactory(@NotNull KafkaProperties properties) {
        Map<String, Object> configProps = KafkaAutoConfiguration.buildProperties(properties);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Kafka template kafka template.
     *
     * @param producerFactory the producerFactory
     * @return the kafka template
     * @since 1.5.0
     */
    @Bean
    @Conditional(value = KafkaAutoConfiguration.ProviderCondition.class)
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Kafka service kafka service.
     *
     * @param kafkaTemplate the kafka template
     * @param properties    properties
     * @return the kafka service
     * @since 1.5.0
     */
    @Bean
    @Conditional(value = KafkaAutoConfiguration.ProviderCondition.class)
    public KafkaProvier kafkaProvider(KafkaTemplate<String, Object> kafkaTemplate, @NotNull KafkaProperties properties) {
        return new KafkaProvier(kafkaTemplate, properties.getTopic());
    }

    /**
     * 如果引用了mq starter，但是没有配置，注入一个运行时打印错误的提供者
     *
     * @return the abstract provider
     * @since 1.7.0
     */
    @Bean
    @ConditionalOnMissingBean(KafkaProvier.class)
    public KafkaProvier errorKafkaProvider() {
        return new ErrorKafkaMqProvider();
    }

}
