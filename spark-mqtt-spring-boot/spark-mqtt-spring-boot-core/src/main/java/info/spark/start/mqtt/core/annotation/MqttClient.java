package info.spark.start.mqtt.core.annotation;

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
 * @date 2021.12.16 14:55
 * @since 2.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MqttClient {

    /**
     * mqtt客户端id
     *
     * @return the string
     * @since 2.1.0
     */
    String value();

}
