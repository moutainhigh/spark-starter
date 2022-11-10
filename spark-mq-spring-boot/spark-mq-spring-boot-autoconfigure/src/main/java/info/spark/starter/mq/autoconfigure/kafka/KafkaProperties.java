package info.spark.starter.mq.autoconfigure.kafka;

import info.spark.starter.mq.autoconfigure.common.MessageProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 16:15
 * @since 1.5.0
 */
@Data
@ConfigurationProperties(prefix = KafkaProperties.PREFIX)
public class KafkaProperties {
    /** Prefix */
    static final String PREFIX = MessageProperties.PREFIX + ".kafka";

    /** Topic */
    private String topic;
    /** Servers */
    private String bootstrapServers = "kafka.server:9092";

    /** Consumer */
    private Consumer consumer = new Consumer();
    /** Producer */
    private Producer producer = new Producer();

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.07.14 19:45
     * @since 1.5.0
     */
    @Data
    public static class Consumer {
        /** Group id */
        private String groupId;
        /** Auto commit */
        private Boolean autoCommit;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.07.14 20:13
     * @since 1.5.0
     */
    @Data
    public static class Producer {

    }
}
