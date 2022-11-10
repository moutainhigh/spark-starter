package info.spark.starter.launcher.autoconfigure;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * <p>Description: 懒加载, 增加项目启动速度</p>
 * todo-dong4j : (2020年04月02日 00:07) [有问题, 会造成一些 jar 中的自动装配类失效]
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 10:29
 * @since 1.0.0
 */
public class LazyInitBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    /**
     * Post process bean factory *
     *
     * @param beanFactory bean factory
     * @throws BeansException beans exception
     * @since 1.0.0
     */
    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            beanFactory.getBeanDefinition(beanName).setLazyInit(true);
        }
    }
}
