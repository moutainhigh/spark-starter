package info.spark.start.mqtt.core.annotation;

import org.springframework.core.convert.converter.Converter;

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
 * @date 2021.12.17 09:39
 * @since 2.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Payload {

    /**
     * 是否必穿
     *
     * @return the boolean
     * @since 2.1.0
     */
    boolean require() default true;

    /**
     * 转换器
     *
     * @return the class [ ]
     * @since 2.1.0
     */
    Class<? extends Converter<?, ?>>[] converter() default {};

}
