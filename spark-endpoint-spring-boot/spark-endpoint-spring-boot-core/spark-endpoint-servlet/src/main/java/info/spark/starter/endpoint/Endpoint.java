package info.spark.starter.endpoint;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 与 @Controller 功能类似, 但仅用于框架提供的端点 (因此它不会与业务端用 @Controller 定义的端点冲突),
 * 与 @RequestMapping 和所有其他 @Controller 功能一起使用 (并与 servlet 上下文中的 {@link EndpointHandlerMapping} 匹配) </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 15:10
 * @since 1.0.0
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Endpoint {

}
