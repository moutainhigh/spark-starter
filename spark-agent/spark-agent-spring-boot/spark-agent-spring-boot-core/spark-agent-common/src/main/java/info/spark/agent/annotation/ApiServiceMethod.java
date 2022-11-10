package info.spark.agent.annotation;

import info.spark.agent.enums.ProtocolType;
import info.spark.agent.plugin.impl.JacksonCodec;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 使用此注解标识的方法, 第一个参数为入参实体 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 10:47
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface ApiServiceMethod {
    /** SIGN */
    String SIGN = "sign";

    /**
     * 方法级别的 api name,
     *
     * @return the string
     * @since 1.0.0
     */
    String value() default "";

    /**
     * Api Service Method 唯一编码，例如："Method_001" 与业务method名称无关就行
     *
     * @return the string
     * @since 1.8.0
     */
    String code() default "";

    /**
     * Version string
     *
     * @return the string
     * @since 1.0.0
     */
    String version() default "1.0.0";

    /**
     * Json string
     *
     * @return the string
     * @since 1.0.0
     */
    String json() default "";

    /**
     * 序列化与反序列化类型, 默认 json, 将使用 {@link JacksonCodec}
     *
     * @return the protocol type
     * @since 1.0.0
     */
    ProtocolType protocol() default ProtocolType.JSON;

    /**
     * 是否需要签名, 默认 false
     *
     * @return the boolean
     * @since 1.6.0
     */
    boolean sign() default false;

    /**
     * 是否进行防重放攻击检查
     *
     * @return the boolean
     * @since 1.6.0
     */
    boolean nonce() default false;

    /**
     * 请求超时时间, 如果 {@link ApiService#timeout()} 为 0 将使用此字段的值作为超时时间,
     * 如果此字段值为 0,将使用 spark.agent.endpoint.request-timeout 作为超时时间, 默认 15 秒, (单位毫秒)
     * 注意: agent client 的请求超时时间必须大于 agent service 的处理超时
     *
     * @return the int
     * @since 1.7.1
     */
    long timeout() default 0L;
}
