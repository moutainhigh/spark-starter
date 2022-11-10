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
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.30 18:31
 * @since 2.0.0
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface AgentManager {

}
