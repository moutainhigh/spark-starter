package info.spark.feign.adapter.registrar;

import info.spark.feign.adapter.annotation.EnableFeignClients;
import info.spark.feign.adapter.annotation.FeignClient;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Objects;
import java.util.Set;

/**
 * <p>Description: 扫描指定注解, 注入 IoC </p>
 * 这种扫描方式将可能会导致 v4 找不到 bean 的问题, 已使用 {@link EnableFeignClients} 方式代替, 此类不再使用.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.17 05:52
 * @see EnableFeignClients
 * @see FeignClientsRegistrar
 * @since 1.0.0
 */
@Deprecated
public class AnnotationScanner extends ClassPathBeanDefinitionScanner {

    /**
     * Annotation scanner
     *
     * @param registry registry
     * @since 1.0.0
     */
    AnnotationScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * 扫描规则
     *
     * @since 1.0.0
     */
    @Override
    protected void registerDefaultFilters() {
        this.addIncludeFilter(new AnnotationTypeFilter(FeignClient.class));
    }

    /**
     * 注入自定义 bean 的逻辑实现, 这里使用自定义 FactoryBean 来注入.
     * 在使用 BeanFactory.getBean 时会判断是否是 FactroyBean 类型的, 如果是从 FactroyBean.getObejct获取
     *
     * @param basePackages base packages
     * @return the set
     * @since 1.0.0
     */
    @NotNull
    @Override
    protected Set<BeanDefinitionHolder> doScan(@NotNull String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            definition.setBeanClass(FeignClientFactoryBean.class);
            Objects.requireNonNull(this.getRegistry()).registerBeanDefinition(holder.getBeanName(), definition);
        }
        return beanDefinitions;
    }

    /**
     * Is candidate component boolean
     *
     * @param beanDefinition bean definition
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    protected boolean isCandidateComponent(@NotNull AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

}
