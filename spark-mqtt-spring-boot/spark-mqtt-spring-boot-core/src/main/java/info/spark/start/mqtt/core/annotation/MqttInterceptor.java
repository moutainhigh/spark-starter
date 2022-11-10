package info.spark.start.mqtt.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 17:50
 * @since 2.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface MqttInterceptor {


    /**
     * 需要拦截的客户端
     *
     * @return the string [ ]
     * @since 2.1.0
     */
    String[] value();
}
