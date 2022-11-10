package info.spark.starter.template.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.template.HelloService;
import info.spark.starter.template.HelloServiceImpl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.04 22:32
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(HelloService.class)
@EnableConfigurationProperties(TemplateProperties.class)
public class TemplateAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Hello service
     *
     * @param properties properties
     * @return the hello service
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public HelloService helloService(TemplateProperties properties) {
        return new HelloServiceImpl();
    }

    /**
     * Weight converter
     *
     * @return the template properties . weight converter
     * @since 1.5.0
     */
    @Bean
    @ConfigurationPropertiesBinding
    public TemplateProperties.WeightConverter weightConverter() {
        return new TemplateProperties.WeightConverter();
    }
}
