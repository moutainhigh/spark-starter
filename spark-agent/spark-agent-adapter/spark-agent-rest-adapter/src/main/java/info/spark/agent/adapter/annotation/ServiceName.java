package info.spark.agent.adapter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 用于 agentClient 父接口定义 serviceName, 业务端在定义子接口时可直接继承, 不再重复定义 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.14 21:59
 * @since 1.7.1
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceName {
    /** SERVICE_NAME */
    String SERVICE_NAME = "serviceName";

    /**
     * Value
     *
     * @return the string
     * @since 1.7.1
     */
    String value();

}
