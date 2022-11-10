package info.spark.start.mqtt.core.annotation;

import info.spark.start.mqtt.core.registrar.MqttCallBackRegistrar;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
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
 * @date 2021.12.16 15:01
 * @since 2.1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({MqttCallBackRegistrar.class})
public @interface EnableMqttClient {


}
