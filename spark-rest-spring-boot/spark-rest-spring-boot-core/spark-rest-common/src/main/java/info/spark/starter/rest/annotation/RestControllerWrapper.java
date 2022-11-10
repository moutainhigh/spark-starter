package info.spark.starter.rest.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 组合注解, 用于简化 controller 层的注解 </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.04 12:41
 * @since 1.0.0
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@ResponseWrapper
@RequestMapping
public @interface RestControllerWrapper {

    /**
     * 定义映射路径URL
     *
     * @return string [ ]
     * @since 1.0.0
     */
    @AliasFor(annotation = RequestMapping.class, value = "path")
    String[] value() default {};

    /**
     * 定义 spring 的 bean name
     *
     * @return string string
     * @since 1.0.0
     */
    @AliasFor(annotation = RestController.class, value = "value")
    String name() default "";

}
