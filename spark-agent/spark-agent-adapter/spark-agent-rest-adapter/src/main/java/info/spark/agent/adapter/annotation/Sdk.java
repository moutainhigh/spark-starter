package info.spark.agent.adapter.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.9.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021 -08-07 17:42
 * @since 1.9.0
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Sdk {

    /**
     * Api service name
     *
     * @return the string
     * @since 1.9.0
     */
    String apiServiceName();
}
