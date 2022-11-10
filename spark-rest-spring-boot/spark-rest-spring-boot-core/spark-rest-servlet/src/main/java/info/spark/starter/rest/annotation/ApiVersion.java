package info.spark.starter.rest.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: api version </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.09.26 10:48
 * @since 2.0.0
 */
@Mapping
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ApiVersion {

    /**
     * Value
     *
     * @return the int
     * @since 2.0.0
     */
    int[] value() default 1;
}
