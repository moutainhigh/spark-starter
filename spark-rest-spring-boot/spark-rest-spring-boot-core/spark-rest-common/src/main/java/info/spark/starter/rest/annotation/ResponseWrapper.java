package info.spark.starter.rest.annotation;

import info.spark.starter.util.core.api.R;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 返回结果包装, 被此注解标注的 controller 或者 方法, 可以直接返回实体, 最终会通过 {@link R} 对实体进行包装 </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.04 11:18
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface ResponseWrapper {
}
