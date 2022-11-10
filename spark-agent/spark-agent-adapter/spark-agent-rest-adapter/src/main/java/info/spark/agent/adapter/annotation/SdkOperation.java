package info.spark.agent.adapter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.07 20:17
 * @since 2.1.0
 */
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SdkOperation {

    /**
     * Api service name
     *
     * @return the string
     * @since 2.1.0
     */
    String apiServiceName();
}
