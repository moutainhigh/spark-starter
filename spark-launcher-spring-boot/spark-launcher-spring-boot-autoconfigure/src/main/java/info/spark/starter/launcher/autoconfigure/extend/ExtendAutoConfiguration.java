package info.spark.starter.launcher.autoconfigure.extend;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.start.SparkAutoConfiguration;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 扩展装配器  </p>
 * todo-dong4j : (2020年02月28日 18:19) [有问题, 未使用]
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.28 01:26
 * @since 1.0.0
 */
@Slf4j
@EnableConfigurationProperties(ExtendProperties.class)
public class ExtendAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Autowired annotation extend autowired annotation bean post processor
     *
     * @return the autowired annotation bean post processor
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(value = ConfigKey.EXTEND_ENABLE_AUTOWIRED_IS_NULL, havingValue = ConfigDefaultValue.TRUE_STRING)
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationExtend() {
        log.info("全局设置允许 @Autowired 允许注入的 bean 为 null");
        return new AutowiredAnnotationExtend();
    }

    /**
     * Resource annotation extend common annotation bean post processor
     *
     * @return the common annotation bean post processor
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(value = ConfigKey.EXTEND_ENABLE_RESOURCE_IS_NULL, havingValue = ConfigDefaultValue.TRUE_STRING)
    public CommonAnnotationBeanPostProcessor resourceAnnotationExtend() {
        log.info("设置允许 @Resource 允许注入的 bean 为 null");
        return new ResourceAnnotationExtend();
    }
}
