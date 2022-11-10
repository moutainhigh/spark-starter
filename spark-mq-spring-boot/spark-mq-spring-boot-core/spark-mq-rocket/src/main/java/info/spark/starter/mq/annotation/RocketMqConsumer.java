package info.spark.starter.mq.annotation;

import info.spark.starter.mq.enums.ConsumeModeEnum;
import info.spark.starter.mq.enums.MessageModeEnum;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yipin on 2017/6/27.
 * RocketMQ消费者自动装配注解
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.11 18:56
 * @since 1.7.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RocketMqConsumer {
    /**
     * Consumer group
     *
     * @return the string
     * @since 1.7.0
     */
    String consumerGroup();

    /**
     * Topic
     *
     * @return the string
     * @since 1.7.0
     */
    String topic();

    /**
     * 广播模式消费:  BROADCASTING
     * 集群模式消费:  CLUSTERING
     *
     * @return 消息模式 string
     * @since 1.7.0
     */
    MessageModeEnum messageMode() default MessageModeEnum.MESSAGE_MODE_CLUSTERING;

    /**
     * 使用线程池并发消费: CONCURRENTLY("CONCURRENTLY"),
     * 单线程消费: ORDERLY("ORDERLY");
     *
     * @return 消费模式 string
     * @since 1.7.0
     */
    ConsumeModeEnum consumeMode() default ConsumeModeEnum.CONSUME_MODE_ORDERLY;

    /**
     * Tag
     *
     * @return the string [ ]
     * @since 1.7.0
     */
    String[] tag() default {"*"};
}
