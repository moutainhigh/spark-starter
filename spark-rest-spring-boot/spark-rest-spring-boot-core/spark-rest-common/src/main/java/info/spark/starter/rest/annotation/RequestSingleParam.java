package info.spark.starter.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 解析 json 格式的参数, 可直接注入单个参数到 controller </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.03 10:40
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestSingleParam {
    /**
     * Value string
     *
     * @return the string
     * @since 1.0.0
     */
    String value();

    /**
     * Required boolean
     *
     * @return the boolean
     * @since 1.0.0
     */
    boolean required() default true;

    /**
     * Default value string
     *
     * @return the string
     * @since 1.0.0
     */
    String defaultValue() default "";
}
