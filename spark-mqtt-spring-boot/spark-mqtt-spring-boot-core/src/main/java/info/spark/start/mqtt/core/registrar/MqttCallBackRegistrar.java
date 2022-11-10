package info.spark.start.mqtt.core.registrar;

import info.spark.start.mqtt.core.annotation.MqttClient;
import info.spark.start.mqtt.core.annotation.MqttInterceptor;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Set;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 14:58
 * @since 2.1.0
 */
@Data
public class MqttCallBackRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    /** Resource loader */
    private ResourceLoader resourceLoader;

    /**
     * Sets resource loader *
     *
     * @param resourceLoader resource loader
     * @since 2.1.0
     */
    @Override
    public void setResourceLoader(@NotNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Register bean definitions
     *
     * @param importingClassMetadata importing class metadata
     * @param registry               registry
     * @since 2.1.0
     */
    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata importingClassMetadata,
                                        @NotNull BeanDefinitionRegistry registry) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(MqttClientBeanPostProcessor.class);
        registry.registerBeanDefinition(MqttClientBeanPostProcessor.class.getSimpleName(), beanDefinition);
        ClassPathBeanDefinitionScanner definitionScanner = new SparkMqttClassPathBeanDefinitionScanner(registry);
        if (resourceLoader != null) {
            definitionScanner.setResourceLoader(resourceLoader);
        }
        definitionScanner.addIncludeFilter((metadataReader, metadataReaderFactory) ->
                                               metadataReader.getAnnotationMetadata().isAnnotated(MqttClient.class.getName()) ||
                                               metadataReader.getAnnotationMetadata().isAnnotated(MqttInterceptor.class.getName()));
        //获取到扫描的包路径
        String packageName = ClassUtils.getPackageName(importingClassMetadata.getClassName());
        definitionScanner.scan(packageName);
    }

    /**
     * 自定义实现注解扫描后续可增加逻辑
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.16 15:09
     * @since 2.1.0
     */
    public static class SparkMqttClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

        /**
         * Spark mqtt class path bean definition scanner
         *
         * @param registry registry
         * @since 2.1.0
         */
        public SparkMqttClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        /**
         * Do scan
         *
         * @param basePackages base packages
         * @return the set
         * @since 2.1.0
         */
        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            return super.doScan(basePackages);
        }
    }
}
