package info.spark.agent.scan;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 扫描自定义注解并注册为 bean  </p>
 * {@link BeanDefinitionRegistryPostProcessor} 的作用是: 允许在正常的 BeanFactoryPostProcessor 检测开始之前注册更多的自定义 bean
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 11:56
 * @since 1.0.0
 */
@Slf4j
@Deprecated
public abstract class CustomerRegistryPostProcessor implements PriorityOrdered, BeanDefinitionRegistryPostProcessor {
    /** 搜索条件: 扫描包路径数组 */
    private final String[] basePackage;

    /**
     * Customer interface registry post processor
     *
     * @param basePackage base package
     * @since 1.0.0
     */
    @Contract(pure = true)
    CustomerRegistryPostProcessor(String[] basePackage) {
        super();
        this.basePackage = basePackage;
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * 自定义 beanname
     *
     * @return the annotation bean name generator
     * @since 1.0.0
     */
    protected abstract AnnotationBeanNameGenerator beanNameGenerator();

    /**
     * Traget annotation class boolean
     *
     * @return the boolean
     * @since 1.0.0
     */
    protected abstract Class<? extends Annotation> tragetAnnotationClass();

    /**
     * Post process bean factory *
     *
     * @param beanFactory bean factory
     * @throws BeansException beans exception
     * @since 1.0.0
     */
    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (log.isDebugEnabled()) {
            log.debug("postProcessBeanFactory");
        }

        // 获取 bean 工厂并转换为 DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        Set<BeanDefinitionHolder> beans = this.getBeanDefinitionHolderSet(this.basePackage);
        for (BeanDefinitionHolder bdh : beans) {
            // 获取 bean 的类型
            try {
                Class<?> cls = Class.forName(bdh.getBeanDefinition().getBeanClassName());
                // 将全限定名注册为别名
                this.registerAlias(beanFactory, bdh.getBeanName(), cls.getName());

                log.info("register bean by custom, bean name = [{}]", bdh.getBeanName());
                // 如果不做 registerBeanDefinition,那么 bean 内的注解会无效 (如Autowired)
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(cls);
                defaultListableBeanFactory.registerBeanDefinition(bdh.getBeanName(), beanDefinition);

            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * 扫描指定的注解
     *
     * @param basePackages base packages
     * @return the bean definition holder set
     * @since 1.0.0
     */
    private Set<BeanDefinitionHolder> getBeanDefinitionHolderSet(String... basePackages) {
        Set<BeanDefinitionHolder> result;
        BeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
        CustomerClassPathBeanDefinitionScanner scanner = new CustomerClassPathBeanDefinitionScanner(registry, false);
        // 配置过滤条件
        scanner.addIncludeFilter(new AnnotationTypeFilter(this.tragetAnnotationClass()));
        // 自定义 beanName 的生成方式
        scanner.setBeanNameGenerator(this.beanNameGenerator());
        result = scanner.doScan(basePackages);
        return result;
    }

    /**
     * Post process bean definition registry *
     *
     * @param registry registry
     * @throws BeansException beans exception
     * @since 1.0.0
     */
    @Override
    public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry) throws BeansException {
        if (log.isDebugEnabled()) {
            log.debug("postProcessBeanDefinitionRegistry");
        }
    }

    /**
     * 为bean注册别名
     * 注意: 如果别名与bean的ID冲突,放弃别名注册
     *
     * @param factory ConfigurableListableBeanFactory
     * @param beanId  bean id
     * @param value   Interface的value
     * @since 1.0.0
     */
    private void registerAlias(@NotNull ConfigurableListableBeanFactory factory, String beanId, String value) {
        // 防止别名覆盖 bean 的 ID
        if (factory.containsBeanDefinition(value)) {
            if (log.isDebugEnabled()) {
                log.debug("[failed] because value = [{}] is existed", value);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("[success] beanId = [{}] value = [{}]", beanId, value);
            }
            factory.registerAlias(beanId, value);
        }
    }

    /**
         * <p>Description: 自定义类扫描 </p>
     *
     * @author dong4j
     * @version 1.0.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2019.12.31 14:22
     * @since 1.0.0
     */
    private static class CustomerClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

        /**
         * Customer class path bean definition scanner
         *
         * @param registry          registry
         * @param useDefaultFilters use default filters
         * @since 1.0.0
         */
        CustomerClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
            super(registry, useDefaultFilters);
        }

        /**
         * 扫描包路径,通过 include 和 exclude 等过滤条件,返回符合条件的 bean 定义
         * 由于这个方法在父类是 protected, 所以只能通过继承类获取结果
         *
         * @param basePackages 需要扫描的包路径
         * @return the set
         * @since 1.0.0
         */
        @NotNull
        @Override
        @SuppressWarnings("all")
        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
            return super.doScan(basePackages);
        }
    }
}
