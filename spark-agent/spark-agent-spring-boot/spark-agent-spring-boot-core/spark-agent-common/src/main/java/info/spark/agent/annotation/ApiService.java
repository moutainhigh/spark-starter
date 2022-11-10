package info.spark.agent.annotation;

import info.spark.agent.enums.ProtocolType;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 使用此注解标识需要注入 IoC 的类 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 02:24
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApiService {
    /** VALUE */
    String VALUE = "value";
    /** NAME */
    String NAME = "name";
    /** API_NAME */
    String API_NAME = "apiName";
    /** TYPE */
    String TYPE = "type";
    /** VERSION */
    String VERSION = "version";
    /** PROTOCOL */
    String PROTOCOL = "protocol";
    /** JSON */
    String JSON = "json";
    /** PRIMARY */
    String PRIMARY = "primary";
    /** SIGN */
    String SIGN = "sign";

    /**
     * Name string
     *
     * @return the string
     * @since 1.0.0
     */
    @AliasFor("name")
    String value() default "";

    /**
     * 注入到 IoC 的 bean name, 与 Api name 没有直接关系
     * 不指定默认使用 类名且首字母小写
     *
     * @return the string
     * @since 1.0.0
     */
    @AliasFor("value")
    String name() default "";

    /**
     * 手动指定 api name, 如果不指定将使用 className_version: info.spark.agent.local.service.TestService_1.0.0
     *
     * @return the string
     * @since 1.0.0
     */
    String apiName() default "";

    /**
     * Api Service 唯一编码，例如："API_001" 与业务bean名称无关就行
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
     * 序列化与反序列化类型, 默认 json
     *
     * @return the protocol type
     * @since 1.0.0
     */
    ProtocolType protocol() default ProtocolType.JSON;

    /**
     * 当一个 bean 的 primary 设置为 true, 然后容器中有多个与该 bean 相同类型的其他 bean,
     * 此时, 当使用 @Autowired 想要注入一个这个类型的 bean 时, 就不会因为容器中存在多个该类型的 bean 而出现异常, 而是优先使用 primary 为 true 的 bean.
     *
     * @return the boolean
     * @since 1.0.0
     */
    boolean primary() default true;

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
     * 如果此字段值为 0, 将使用 spark.agent.endpoint.request-timeout 作为超时时间, 默认 15 秒 (单位毫秒)
     * 注意: agent client 的请求超时时间必须大于 agent service 的处理超时
     *
     * @return the int
     * @since 1.7.1
     */
    long timeout() default 0L;
}
