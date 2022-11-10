package info.spark.feign.adapter.registrar;

import info.spark.feign.adapter.constant.FeignAdapter;
import info.spark.feign.adapter.util.FeignUtils;
import info.spark.starter.basic.constant.ConfigKey;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import feign.Feign;
import feign.Target;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 将 Feign Client 注入到 IoC 具体逻辑实现 </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.17 17:53
 * @since 1.0.0
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class FeignClientFactoryBean<T> extends AbstractFactoryBean<T> implements EnvironmentAware, ApplicationContextAware {
    /** Type */
    private Class<T> type;
    /** 服务端是否使用 agent */
    private boolean agent;
    /** Name */
    private String name;
    /** Url */
    private String url;
    /** Context id */
    private String contextId;
    /** Path */
    private String path;
    /** Decode 404 */
    private boolean decode404;
    /** Fallback */
    private Class<?> fallback = void.class;
    /** Fallback factory */
    private Class<?> fallbackFactory = void.class;
    /** Environment */
    private static Environment environment;
    /** Application context */
    private static ApplicationContext applicationContext;
    /** cache */
    private static Map<String, Object> cache = new HashMap<>();
    /** 网关地址前缀, 发起请求时将使用 spark.feign.ribbon.list-of-servers 配置的 ip 地址替换此字符串 */
    private static final String GATEWAY_PREFIX = ConfigKey.AgentConfigKey.GATEWAY_PREFIX;

    /**
     * Sets application context *
     *
     * @param context context
     * @throws BeansException beans exception
     * @since 1.0.0
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    /**
     * Sets environment *
     *
     * @param environment environment
     * @since 1.0.0
     */
    @Override
    public void setEnvironment(@NotNull Environment environment) {
        FeignClientFactoryBean.environment = environment;
    }

    /**
     * Gets object type *
     *
     * @return the object type
     * @since 1.0.0
     */
    @Override
    public Class<T> getObjectType() {
        return this.type;
    }

    /**
     * 通过 builder 创建实例
     *
     * @return a {@link Feign} client created with the specified data and the context information
     * @since 1.0.0
     */
    public T getTarget() {
        return this.createInstance();
    }

    /**
     * 通过 FeignClient 注解创建 Feign 代理类, 此方法只有在注入 client 时才会执行(lazy load).
     *
     * @return the t
     * @see AbstractFactoryBean#getObject()
     * @since 1.0.0
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    protected T createInstance() {
        T singletonInstance = (T) cache.get(this.type.getName());
        if (singletonInstance != null) {
            return singletonInstance;
        }

        // 被 @Deprecated 标记的 feign client
        if (AnnotationUtils.findAnnotation(this.getClass(), Deprecated.class) != null) {
            log.warn("[{}] 已被标记为过时, 请及时删除", this);
        }

        // 优先使用 url
        if (StringUtils.hasText(this.url)) {
            this.url = FeignUtils.getPath(this.url);
        } else if (StringUtils.hasText(this.name)) {
            if (this.name.startsWith(FeignAdapter.HTTP_PREFIX)) {
                // 如果 name 配置成了 url, 则直接替换
                this.url = FeignUtils.getPath(this.name);
                // 修改 name 为类名
                this.name = this.type.getSimpleName();
            } else {
                // name 为 服务名
                this.url = GATEWAY_PREFIX + this.name;
            }
        } else {
            // 如果没有配置 name 和 url, 生成的 url = http://gateway, 在注册 feign client 时, 需要确保 name 不能为空, 因此这里需要判断 name
            this.url = FeignUtils.getPath(GATEWAY_PREFIX);
            this.name = this.type.getSimpleName();
        }

        if (this.agent) {
            log.warn("[{}] 已开启 agent 配置, 请确保服务端支持 agent [需要依赖 spark-starter-agent]", this.type.getSimpleName());
            this.url += ConfigKey.AgentConfigKey.AGENT_SUFFIX;
        }
        log.info("Fiegn client: [{}] [{}]", this.type, this.url);

        singletonInstance = this.loadBalance(new Target.HardCodedTarget<>(this.type, this.name, this.url));
        cache.put(this.type.getName(), singletonInstance);
        return singletonInstance;
    }

    /**
     * todo-dong4j : (2020年01月19日 02:53) [从 IoC 中获取 client]
     *
     * @param target target
     * @return the t
     * @since 1.0.0
     */
    private T loadBalance(Target.HardCodedTarget<T> target) {
        Feign.Builder builder = FeignTargetBuilder.getInstance(applicationContext);
        Targeter targeter = new DefaultTargeter();
        return targeter.target(this, builder, target);
    }

}
