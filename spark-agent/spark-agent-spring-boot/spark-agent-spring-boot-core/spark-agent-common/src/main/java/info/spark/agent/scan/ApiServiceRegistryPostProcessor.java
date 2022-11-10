package info.spark.agent.scan;

import info.spark.agent.annotation.ApiService;
import info.spark.agent.core.ApiServiceDefinition;
import info.spark.agent.exception.AgentServiceException;
import info.spark.agent.invoker.ApiServiceInvoker;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * <p>Description: ApiService 注解处理器 </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.31 00:27
 * @since 1.0.0
 */
@Deprecated
public class ApiServiceRegistryPostProcessor extends CustomerRegistryPostProcessor {

    /**
     * Customer interface registry post processor
     *
     * @param basePackage base package
     * @since 1.0.0
     */
    public ApiServiceRegistryPostProcessor(String[] basePackage) {
        super(basePackage);
    }

    /**
     * Bean name generator annotation bean name generator
     *
     * @return the annotation bean name generator
     * @since 1.0.0
     */
    @Override
    public AnnotationBeanNameGenerator beanNameGenerator() {
        return new ApiServiceBeanNameGenerator();
    }

    /**
     * Traget annotation class boolean
     *
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    protected Class<ApiService> tragetAnnotationClass() {
        return ApiService.class;
    }

    /**
         * <p>Description: 自定义 apiservice bean name </p>
     *
     * @author dong4j
     * @version 1.0.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2019.12.31 00:26
     * @since 1.0.0
     */
    private static class ApiServiceBeanNameGenerator extends AnnotationBeanNameGenerator {
        /**
         * Generate bean name string
         *
         * @param beanDefinition         bean definition
         * @param beanDefinitionRegistry bean definition registry
         * @return the string
         * @since 1.0.0
         */
        @NotNull
        @Override
        public String generateBeanName(@NotNull BeanDefinition beanDefinition, @NotNull BeanDefinitionRegistry beanDefinitionRegistry) {
            try {
                String beanClassName = beanDefinition.getBeanClassName();
                Class<?> apiServiceClass = Class.forName(beanClassName);
                if (apiServiceClass.isAnnotationPresent(ApiService.class) && ApiServiceDefinition.class.isAssignableFrom(apiServiceClass)) {
                    ApiService apiService = apiServiceClass.getAnnotation(ApiService.class);
                    // beanName = className_version
                    return ApiServiceInvoker.name(beanClassName, apiService.version());
                } else {
                    throw new AgentServiceException(StringUtils.format("[{}] is not ApiService", apiServiceClass));
                }
            } catch (Exception ex) {
                throw new AgentServiceException(ex);
            }
        }
    }
}
