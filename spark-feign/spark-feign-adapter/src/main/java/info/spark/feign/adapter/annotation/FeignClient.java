package info.spark.feign.adapter.annotation;

import info.spark.feign.adapter.constant.FeignAdapter;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: feign 标识, 用于标记 api 接口:
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
public @interface FeignClient {
    /** AGENT */
    String AGENT = "agent";
    /** VALUE */
    String VALUE = "value";
    /** NAME */
    String NAME = "name";
    /** CONTEXT_ID */
    String CONTEXT_ID = "contextId";
    /** URL */
    String URL = "url";
    /** PATH */
    String PATH = "path";
    /** TYPE */
    String TYPE = "type";
    /** DECODE_404 */
    String DECODE_404 = "decode404";
    /** FALLBACK */
    String FALLBACK = "fallback";
    /** FALLBACK_FACTORY */
    String FALLBACK_FACTORY = "fallbackFactory";
    /** PRIMARY */
    String PRIMARY = "primary";

    /**
     * 接口注解类型, 默认原生
     *
     * @return the string
     * @since 1.0.0
     */
    String agreement() default FeignAdapter.AGREEMENT;

    /**
     * 接口请求客户端, 默认 okhttp
     *
     * @return the string
     * @since 1.0.0
     */
    String client() default FeignAdapter.OKHTTP;

    /**
     * 服务端是否使用 agent, 默认为 false, 如果设置为 true 将会在生成的 url 后加上 /agent 后缀
     *
     * @return the boolean
     * @since 1.0.0
     */
    boolean agent() default false;

    /**
     * 定义容错的处理类, 当调用远程接口失败或超时时, 会调用对应接口的容错逻辑, fallback 指定的类必须实现 @FeignClient 标记的接口
     * todo-dong4j : (2020年01月19日 03:38) [未完成]
     *
     * @return the class
     * @since 1.0.0
     */
    Class<?> fallback() default Object.class;

    /**
     * 具有可选协议前缀的服务的名称. name*的同义词. 无论是否提供url, 都必须为所有客户端指定名称. 可以指定为属性键, 例如: ${property key}
     *
     * @return the string
     * @since 1.0.0
     */
    @AliasFor("name")
    String value() default "";

    /**
     * 指定 FeignClient 的名称, 如果项目使用了 Ribbon, name 属性会作为微服务的名称, 用于服务发现
     *
     * @return the string
     * @since 1.0.0
     */
    @AliasFor("value")
    String name() default "";

    /**
     * 如果存在, 它将用作bean名称而不是名称, 但不会用作服务id
     *
     * @return the string
     * @since 1.0.0
     */
    String contextId() default "";

    /**
     * 为外部客户端设置 @Qualifier 值
     *
     * @return the string
     * @since 1.0.0
     */
    String qualifier() default "";

    /**
     * 绝对URL或可解析主机名 (协议是可选的)
     * 一般用于调试, 可以手动指定 @FeignClient 调用的地址
     *
     * @return the string
     * @since 1.0.0
     */
    String url() default "";

    /**
     * 当发生 http 404 错误时, 如果该字段位 true, 会调用 decoder 进行解码, 否则抛出 FeignException
     * todo-dong4j : (2020年01月19日 03:37) [未完成]
     *
     * @return the boolean
     * @since 1.0.0
     */
    boolean decode404() default false;

    /**
     * Feign 配置类, 可以自定义 Feign 的 Encoder、Decoder、LogLevel、Contract
     * todo-dong4j : (2020年01月19日 03:37) [未完成]
     *
     * @return the class [ ]
     * @since 1.0.0
     */
    Class<?>[] configuration() default {};

    /**
     * 工厂类, 用于生成 fallback 类示例, 通过这个属性我们可以实现每个接口通用的容错逻辑, 减少重复的代码
     * todo-dong4j : (2020年01月19日 03:38) [未完成]
     *
     * @return the class
     * @since 1.0.0
     */
    Class<?> fallbackFactory() default void.class;

    /**
     * 要由所有方法级映射使用的路径前缀. 可以与 @RibbonClient 一起使用, 也可以不使用 @RibbonClient
     * todo-dong4j : (2020年01月19日 03:38) [未完成]
     *
     * @return the string
     * @since 1.0.0
     */
    String path() default "";

    /**
     * 当一个 bean 的 primary 设置为 true, 然后容器中有多个与该 bean 相同类型的其他 bean,
     * 此时, 当使用 @Autowired 想要注入一个这个类型的 bean 时, 就不会因为容器中存在多个该类型的 bean 而出现异常, 而是优先使用 primary 为 true 的 bean.
     *
     * @return the boolean
     * @since 1.0.0
     */
    boolean primary() default true;
}
