package info.spark.start.mqtt.core.annotation;

import info.spark.start.mqtt.core.common.TopicType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 14:53
 * @since 2.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MqttSubscribe {

    /**
     * 订阅的topic
     *
     * @return the string [ ]
     * @since 2.1.0
     */
    String[] value();

    /**
     * Qos
     *
     * @return the int [ ]
     * @since 2.1.0
     */
    int[] qos() default {};

    /**
     * 订阅的客户端id，如果没有 默认取MqttClient中的id
     *
     * @return the string [ ]
     * @since 2.1.0
     */
    String[] clients() default {};

    /**
     * Topic type
     *
     * @return the topic type
     * @since 2.1.0
     */
    TopicType topicType() default TopicType.NORMAL;
}
