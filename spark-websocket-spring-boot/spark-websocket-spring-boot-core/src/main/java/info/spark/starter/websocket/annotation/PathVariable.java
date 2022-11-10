package info.spark.starter.websocket.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 19:55
 * @since 2022.1.1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {

    /**
     * Alias for {@link #name}.
     *
     * @return the string
     * @since 2022.1.1
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The name of the path variable to bind to.
     *
     * @return the string
     * @since 2022.1.1
     */
    @AliasFor("value")
    String name() default "";

}
