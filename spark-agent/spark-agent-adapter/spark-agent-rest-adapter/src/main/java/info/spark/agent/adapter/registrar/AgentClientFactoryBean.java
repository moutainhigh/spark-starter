package info.spark.agent.adapter.registrar;

import info.spark.agent.adapter.client.AgentTemplate;
import info.spark.agent.adapter.config.AgentRestProperties;
import info.spark.agent.validation.Validater;

import info.spark.agent.adapter.annotation.Client;
import info.spark.starter.basic.constant.ConfigKey;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 将 agent Client 注入到 IoC 具体逻辑实现 </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.22 09:00
 * @since 1.6.0
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class AgentClientFactoryBean<T> extends AbstractFactoryBean<T> implements EnvironmentAware, ApplicationContextAware {
    /** Singleton */
    private boolean singleton = true;
    /** Type */
    private Class<T> type;
    /**
     * 通过 {@link Client} 透传的自定义 url
     *
     * @since 1.7.0
     */
    private String endpoint;
    /** Name */
    private String serviceName;
    /** Environment */
    private static Environment environment;
    /** Application context */
    private static ApplicationContext applicationContext;
    /** Agent templae */
    private static AgentTemplate agentTemplate;
    /** Agent rest properties */
    private static AgentRestProperties agentRestProperties;
    /** Validater */
    private static Validater validater;
    /** cache */
    private static Map<String, Object> cache = new HashMap<>();
    /** 网关地址前缀, 发起请求时将使用 spark.gateway.servers 配置的 ip 地址替换此字符串 */
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
        AgentClientFactoryBean.applicationContext = context;
    }

    /**
     * Sets environment *
     *
     * @param environment environment
     * @since 1.0.0
     */
    @Override
    public void setEnvironment(@NotNull Environment environment) {
        AgentClientFactoryBean.environment = environment;
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
     * 通过 @Client 注解创建 agent 代理类, 此方法只有在注入 client 时才会执行(lazy load).
     *
     * @return the t
     * @see AbstractFactoryBean#getObject() AbstractFactoryBean#getObject()
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

        // 被 @Deprecated 标记的 agent client
        if (AnnotationUtils.findAnnotation(this.getClass(), Deprecated.class) != null) {
            log.warn("[{}] 已被标记为过时, 请及时删除", this);
        }

        if (agentTemplate == null) {
            agentTemplate = applicationContext.getBean(AgentTemplate.class);
        }

        if (agentRestProperties == null) {
            agentRestProperties = applicationContext.getBean(AgentRestProperties.class);
        }

        if (validater == null) {
            validater = applicationContext.getBean(Validater.class);
        }

        // 这里主要是创建接口对应的实例, 便于注入到 spring 容器中
        InvocationHandler handler = new AgentClientProxy(this.endpoint,
                                                         this.serviceName,
                                                         agentTemplate,
                                                         agentRestProperties,
                                                         environment,
                                                         validater);

        singletonInstance = (T) Proxy.newProxyInstance(this.type.getClassLoader(),
                                                       new Class[] {this.type},
                                                       handler);

        cache.put(this.type.getName(), singletonInstance);
        return singletonInstance;

    }

}
