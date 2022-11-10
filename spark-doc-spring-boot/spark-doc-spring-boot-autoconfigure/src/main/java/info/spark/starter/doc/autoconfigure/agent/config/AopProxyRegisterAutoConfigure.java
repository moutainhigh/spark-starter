package info.spark.starter.doc.autoconfigure.agent.config;

import info.spark.starter.doc.agent.aop.PluginAdviser;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.27 15:17
 * @since 1.0.0
 */
public class AopProxyRegisterAutoConfigure implements ImportBeanDefinitionRegistrar, ApplicationContextAware {

    /** Application context */
    @Getter
    private ApplicationContext applicationContext;
    /** Annotation attributes */
    private AnnotationAttributes annotationAttributes;

    /**
     * Sets application context *
     *
     * @param applicationContext application context
     * @throws BeansException beans exception
     * @since 1.0.0
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Register bean definitions
     *
     * @param importingClassMetadata importing class metadata
     * @param registry               registry
     * @since 1.0.0
     */
    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(PluginAdviser.class);
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(PluginAdviser.class.getName(), beanDefinition);
    }

}
