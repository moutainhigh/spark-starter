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
public @interface RequestParam {

    /**
     * Alias for {@link #name}.
     *
     * @return the string
     * @since 2022.1.1
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The name of the request parameter to bind to.
     *
     * @return the string
     * @since 2022.1.1
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Whether the parameter is required.
     * <p>Defaults to {@code true}, leading to an exception being thrown
     * if the parameter is missing in the request. Switch this to
     * {@code false} if you prefer a {@code null} value if the parameter is
     * not present in the request.
     * <p>Alternatively, provide a {@link #defaultValue}, which implicitly
     * sets this flag to {@code false}.
     *
     * @return the boolean
     * @since 2022.1.1
     */
    boolean required() default true;

    /**
     * The default value to use as a fallback when the request parameter is
     * not provided or has an empty value.
     * <p>Supplying a default value implicitly sets {@link #required} to
     * {@code false}.
     *
     * @return the string
     * @since 2022.1.1
     */
    String defaultValue() default "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n";

}
