package info.spark.starter.es.register;

import info.spark.starter.es.mapper.container.EsMapperContextual;
import info.spark.starter.es.proxy.EsSimpleProxy;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.21 17:20
 * @since 1.7.1
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class EsProFactoryBean<T> extends AbstractFactoryBean<T> implements ApplicationContextAware {
    /** Type */
    private Class<T> type;
    /** Application context */
    private static ApplicationContext applicationContext;
    /** cache */
    private static Map<String, Object> cache = new HashMap<>();
    /** Es mapper contextual */
    private EsMapperContextual esMapperContextual;

    /**
     * Sets application context *
     *
     * @param context context
     * @throws BeansException beans exception
     * @since 1.7.1
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    /**
     * Gets object type *
     *
     * @return the object type
     * @since 1.7.1
     */
    @Override
    public Class<T> getObjectType() {
        return this.type;
    }

    /**
     * Create instance
     *
     * @return the t
     * @since 1.7.1
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    protected T createInstance() {
        T singletonInstance = (T) cache.get(this.type.getName());
        if (singletonInstance != null) {
            return singletonInstance;
        }
        if (AnnotationUtils.findAnnotation(this.getClass(), Deprecated.class) != null) {
            log.warn("[{}] 已被标记为过时, 请及时删除", this);
        }

        // 这里主要是创建接口对应的实例, 便于注入到 spring 容器中
        InvocationHandler handler = new EsSimpleProxy(this.type, esMapperContextual);
        singletonInstance = (T) Proxy.newProxyInstance(this.type.getClassLoader(), new Class[] {this.type}, handler);
        cache.put(this.type.getName(), singletonInstance);
        return singletonInstance;
    }

}
