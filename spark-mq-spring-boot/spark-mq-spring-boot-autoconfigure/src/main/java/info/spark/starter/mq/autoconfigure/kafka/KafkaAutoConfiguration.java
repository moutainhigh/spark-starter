package info.spark.starter.mq.autoconfigure.kafka;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.mq.autoconfigure.common.MessageProperties;
import info.spark.starter.mq.autoconfigure.common.ProviderPropCondition;
import info.spark.starter.mq.autoconfigure.common.RoleTypeCondition;
import info.spark.starter.mq.RoleType;
import info.spark.starter.mq.provider.KafkaProvier;
import info.spark.starter.mq.serialization.KafkaMessageDeserializer;
import info.spark.starter.mq.serialization.KafkaMessageSerializer;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 18:40
 * @since 1.5.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KafkaProvier.class)
@Import(value = {KafkaConsumerAutoConfiguration.class, KafkaProviderAutoConfiguration.class})
@EnableConfigurationProperties(MessageProperties.class)
public class KafkaAutoConfiguration implements SparkAutoConfiguration {

    /** BOOTSTRAP_SERVERS_CONFIG */
    public static final String BOOTSTRAP_SERVERS_CONFIG = "bootstrap.servers";
    /** KEY_SERIALIZER_CLASS_CONFIG */
    public static final String KEY_SERIALIZER_CLASS_CONFIG = "key.serializer";
    /** VALUE_SERIALIZER_CLASS_CONFIG */
    public static final String VALUE_SERIALIZER_CLASS_CONFIG = "value.serializer";
    /** KEY_DESERIALIZER_CLASS_CONFIG */
    public static final String KEY_DESERIALIZER_CLASS_CONFIG = "key.deserializer";
    /** VALUE_DESERIALIZER_CLASS_CONFIG */
    public static final String VALUE_DESERIALIZER_CLASS_CONFIG = "value.deserializer";

    /**
     * Build properties
     *
     * @param properties properties
     * @return the map
     * @since 1.5.0
     */
    @NotNull
    public static Map<String, Object> buildProperties(@NotNull KafkaProperties properties) {
        Map<String, Object> configProps = new HashMap<>(4);
        configProps.put(BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaMessageSerializer.class);
        configProps.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(VALUE_DESERIALIZER_CLASS_CONFIG, KafkaMessageDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return configProps;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.07.14 19:05
     * @since 1.5.0
     */
    static class ProviderCondition extends ProviderPropCondition {

        /**
         * Provider condition
         *
         * @since 1.5.0
         */
        ProviderCondition() {
            super(RoleType.PROVIDER, ConfigKey.MqConfigKey.BOOTSTRAP_SERVERS);
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.07.14 19:05
     * @since 1.5.0
     */
    static class ConsumerCondition extends RoleTypeCondition {

        /**
         * Consumer condition
         *
         * @since 1.5.0
         */
        ConsumerCondition() {
            super(RoleType.CONSUMER);
        }
    }
}
