package info.spark.starter.security.autoconfigure;

import com.alibaba.nacos.api.config.ConfigService;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.security.spi.SecurityLauncherInitiation;
import info.spark.starter.util.core.api.BaseCodes;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:55
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SecurityLauncherInitiation.class)
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration implements SparkAutoConfiguration {
    /** Security properties */
    @Resource
    private SecurityProperties securityProperties;

    /**
     * Execute
     *
     * @since 1.0.0
     */
    @Override
    public void execute() {
        BaseCodes.CONFIG_ERROR.notBlank(this.securityProperties.getSigningKey(),
                                        "必须配置 spark.security.signing-key");
    }

    /**
     * Dynamic security url dynamic security url
     *
     * @param applicationEventPublisher application event publisher
     * @param securityProperties        security properties
     * @param environment               environment
     * @return the dynamic security url
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnClass(ConfigService.class)
    public DynamicSecurityUrl dynamicSecurityUrl(ApplicationEventPublisher applicationEventPublisher,
                                                 SecurityProperties securityProperties,
                                                 ConfigurableEnvironment environment) {
        return new DynamicSecurityUrl(applicationEventPublisher, securityProperties, environment);
    }

    /**
     * Environment change event handler environment change event handler
     *
     * @return the environment change event handler
     * @since 1.0.0
     */
    @Bean
    public EnvironmentChangeEventHandler environmentChangeEventHandler() {
        return new EnvironmentChangeEventHandler();
    }

    /**
     * Security refresh event handler
     *
     * @return the refresh event handler
     * @since 1.6.0
     */
    @Bean
    public SecurityRefreshEventHandler securityRefreshEventHandler() {
        return new SecurityRefreshEventHandler();
    }

    /**
     * Refresh scope refreshed event handler refresh scope refreshed event handler
     *
     * @param dynamicSecurityUrl dynamic security url
     * @return the refresh scope refreshed event handler
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnClass(ConfigService.class)
    public SecurityRefreshScopeRefreshedEventHandler refreshScopeRefreshedEventHandler(DynamicSecurityUrl dynamicSecurityUrl) {
        return new SecurityRefreshScopeRefreshedEventHandler(dynamicSecurityUrl);
    }
}
