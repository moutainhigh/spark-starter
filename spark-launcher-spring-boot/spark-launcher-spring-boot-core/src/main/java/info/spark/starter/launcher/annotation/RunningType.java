package info.spark.starter.launcher.annotation;

import info.spark.starter.launcher.enums.ApplicationType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 应用运行类型 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.05 18:38
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RunningType {

    /**
     * Value application type
     *
     * @return the application type
     * @since 1.0.0
     */
    ApplicationType value();
}
