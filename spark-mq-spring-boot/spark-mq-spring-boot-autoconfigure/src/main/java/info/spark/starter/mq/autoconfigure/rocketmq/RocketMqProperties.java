package info.spark.starter.mq.autoconfigure.rocketmq;

import info.spark.starter.mq.RoleType;
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
@ConfigurationProperties(prefix = RocketMqProperties.PREFIX)
public class RocketMqProperties {
    /** Prefix */
    static final String PREFIX = MessageProperties.PREFIX + ".rocketmq";
    /** Role */
    private RoleType role;
    /** Name srv address */
    private String nameServerAddress;
    /** Instance name */
    private String instanceName = "DEFAULT";
    /** Producer */
    private Producer producer = new Producer();
    /** Consumer */
    private Consumer consumer = new Consumer();

    /**
         * <p>Description: </p>
     *
     * @author zhubo
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.11.11 09:15
     * @since 1.7.0
     */
    @Data
    public static class Producer {
        /** Group id */
        private String group = "DEFAULT";
        /** Send msg timeout */
        private Integer sendMsgTimeout = 3000;
        /** Retry times when send failed */
        private Integer retryTimesWhenSendFailed = 2;
    }

    /**
         * <p>Description: </p>
     *
     * @author zhubo
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.11.11 09:18
     * @since 1.7.0
     */
    @Data
    public static class Consumer {
        /** Group */
        private String group;
        /** Message model */
        private String messageModel = "CLUSTERING";
        /** Consume thread min */
        private int consumeThreadMin = 20;
        /** Consume thread max */
        private int consumeThreadMax = 64;
        /** Pull threshold for queue */
        private int pullThresholdForQueue = 1000;
        /** Pull threshold size for queue */
        private int pullThresholdSizeForQueue = 100;
        /** Consume message batch max size */
        private int consumeMessageBatchMaxSize = 1;
        /** Pull batch size */
        private int pullBatchSize = 32;
        /** Max reconsume times */
        private int maxReconsumeTimes = -1;
        /** Consume timeout */
        private long consumeTimeout = 15;
    }
}
