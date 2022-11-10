package info.spark.agent.adapter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 用于标记 api 接口:
 * <code>@FeignClient(value = "${ms.feign.url.xxxx}", client = FeignConstant.OKHTTP, agreement = FeignConstant.JAXRS)</code>
 * client 默认 okHttp, 注解协议默认 jaxrs, 使用 JAX-RS 标准注解代替 Feign 原生注解
 * 熔断功能需要实现接口, 实现具体逻辑, 可记录日志, 可发送邮件等, 需要自己实现逻辑
 * </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.16 15:50
 * @since 1.0.0
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Client {
    /** VALUE */
    String VALUE = "value";
    /** TYPE */
    String TYPE = "type";
    /** URL */
    String ENDPOINT = "endpoint";

    /**
     * Value
     *
     * @return the string
     * @since 1.6.0
     */
    String value() default "";

    /**
     * 自定义调用地址, 可使用 ${} 占位符.
     *
     * @return the string
     * @since 1.7.0
     * @deprecated 请直接使用 spark.gateway.endpoint 配置
     */
    @Deprecated
    String endpoint() default "";

    /**
     * 定义容错的处理类, 当调用远程接口失败或超时时, 会调用对应接口的容错逻辑, fallback 指定的类必须实现 @Client 标记的接口
     * todo-dong4j : (2020年01月19日 03:38) [未完成]
     *
     * @return the class
     * @since 1.0.0
     */
    Class<?> fallback() default void.class;
}
