package info.spark.agent.register;

import info.spark.agent.exception.AgentServiceException;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 将 Agent service 注入到 IoC 具体逻辑实现 </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.17 17:53
 * @since 1.0.0
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class AgentServiceFactoryBean<T> extends AbstractFactoryBean<T> implements EnvironmentAware, ApplicationContextAware {
    /** Type */
    private Class<T> type;
    /** Name */
    private String name;
    /** Api name */
    private String apiName;
    /** Url */
    private String version;
    /** Environment */
    private Environment environment;
    /** Application context */
    private ApplicationContext applicationContext;
    /** cache */
    private static Map<String, Object> cache = new HashMap<>();

    /**
     * Sets application context *
     *
     * @param context context
     * @throws BeansException beans exception
     * @since 1.0.0
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    /**
     * Sets environment *
     *
     * @param environment environment
     * @since 1.0.0
     */
    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    /**
     * 获取 bean 的方式, 这里先通过反射提前调用父类的 'afterPropertiesSet' 方法进行初始化 {@link AgentServiceFactoryBean#createInstance()},
     * 主要解决使用 {@link ApiServiceBuilder} 创建 ApiService 失败的问题.
     *
     * @return the target
     * @throws Exception exception
     * @see AbstractFactoryBean#afterPropertiesSet()
     * @since 1.0.0
     */
    T getTarget() throws Exception {
        // 判断父类是否执行了初始化
        Field field = this.getClass().getSuperclass().getDeclaredField("initialized");
        field.setAccessible(true);
        if (!(boolean) field.get(this)) {
            this.getClass().getSuperclass().getDeclaredMethod("afterPropertiesSet").invoke(this);
        }

        return this.getObject();
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
     * 手动实例化, Spring 获取此实例注入到 IoC
     *
     * @return the t
     * @see AbstractFactoryBean#getObject()
     * @since 1.0.0
     */
    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    protected T createInstance() {
        T singletonInstance = (T) cache.get(this.type.getName());
        if (singletonInstance != null) {
            return singletonInstance;
        }
        try {
            Constructor<T> constructor = this.type.getConstructor();
            singletonInstance = constructor.newInstance();
            cache.put(this.type.getName(), singletonInstance);
            return singletonInstance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new AgentServiceException("实例化 [{}] 失败", this.type, e);
        }
    }
}
