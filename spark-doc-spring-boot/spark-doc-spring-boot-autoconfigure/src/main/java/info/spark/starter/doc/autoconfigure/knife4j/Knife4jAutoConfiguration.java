package info.spark.starter.doc.autoconfigure.knife4j;

import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendSetting;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import com.github.xiaoymin.knife4j.spring.filter.ProductionSecurityFilter;
import com.github.xiaoymin.knife4j.spring.filter.SecurityBasicAuthFilter;
import com.github.xiaoymin.knife4j.spring.model.MarkdownFiles;

import info.spark.starter.common.constant.App;
import info.spark.starter.common.enums.LibraryEnum;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.doc.common.schema.SparkOperationModelsProviderPlugin;
import info.spark.starter.doc.common.schema.SparkOperationResponseMessagePlugin;
import info.spark.starter.doc.knife4j.EnableKnife4j;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.web.Swagger2ControllerWebMvc;

/**
 * <p>Description: Knife4j 基础自动配置类 </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.08 16:14
 * @since 1.4.0
 */
@Configuration(proxyBeanMethods = false)
@Profile(value = {App.ENV_LOCAL, App.ENV_DEV, App.ENV_TEST, App.ENV_PREV})
@ConditionalOnClass(value = {
    EnableKnife4j.class,
    Servlet.class,
    DispatcherServlet.class,
    Swagger2ControllerWebMvc.class
})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(value = {info.spark.starter.doc.autoconfigure.knife4j.Knife4jProperties.class})
@ComponentScan(basePackages = "com.github.xiaoymin.knife4j.spring.plugin")
@Slf4j
public class Knife4jAutoConfiguration implements SparkAutoConfiguration {

    /**
     * 配置Cors
     *
     * @return cors filter
     * @since 2.0.4
     */
    @Bean
    @ConditionalOnMissingBean(CorsFilter.class)
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setMaxAge(10000L);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    /**
     * Markdown resolver
     *
     * @param knife4jProperties knife 4 j properties
     * @return the open api extension resolver
     * @since 1.4.0
     */
    @Bean(initMethod = "start")
    @ConditionalOnMissingBean(OpenApiExtensionResolver.class)
    @ConditionalOnProperty(name = "knife4j.enable", havingValue = "true")
    public OpenApiExtensionResolver markdownResolver(info.spark.starter.doc.autoconfigure.knife4j.Knife4jProperties knife4jProperties) {
        OpenApiExtendSetting setting = knife4jProperties.getSetting();
        if (setting == null) {
            setting = new OpenApiExtendSetting();
        }
        return new OpenApiExtensionResolver(setting, knife4jProperties.getDocuments());
    }

    /**
     * 初始化自定义Markdown特性
     *
     * @param knife4jProperties 配置文件
     * @return markdownFiles markdown files
     * @since 1.4.0
     */
    @Bean(initMethod = "init")
    public MarkdownFiles markdownFiles(@NotNull info.spark.starter.doc.autoconfigure.knife4j.Knife4jProperties knife4jProperties) {
        return new MarkdownFiles(knife4jProperties.getMarkdowns() == null ? "" : knife4jProperties.getMarkdowns());
    }

    /**
     * Security basic auth filter
     *
     * @param knife4jProperties knife 4 j properties
     * @return the security basic auth filter
     * @since 1.4.0
     */
    @Bean
    public SecurityBasicAuthFilter securityBasicAuthFilter(@NotNull Knife4jProperties knife4jProperties) {

        return new SecurityBasicAuthFilter(knife4jProperties.getBasic().isEnable(),
                                           knife4jProperties.getBasic().getUsername(),
                                           knife4jProperties.getBasic().getPassword());
    }

    /**
     * Production security filter
     *
     * @return the production security filter
     * @since 1.4.0
     */
    @Bean
    public ProductionSecurityFilter productionSecurityFilter() {
        return new ProductionSecurityFilter(!ConfigKit.isLocalLaunch() && ConfigKit.isProd());
    }

    /**
     * Mvc operation response message plugin
     *
     * @return the mvc operation response message plugin
     * @since 1.7.0
     */
    @Bean
    @ConditionalOnMissingBean
    public SparkOperationResponseMessagePlugin mvcOperationResponseMessagePlugin() {
        return new SparkOperationResponseMessagePlugin(null);
    }

    /**
     * Mvc operation models provider plugin
     *
     * @return the mvc operation models provider plugin
     * @since 1.7.0
     */
    @Bean
    @ConditionalOnMissingBean
    public SparkOperationModelsProviderPlugin mvcOperationModelsProviderPlugin() {
        return new SparkOperationModelsProviderPlugin(null);
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.4.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.05.08 16:52
     * @since 1.4.0
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnResource(resources = "classpath:META-INF/resources/doc.html")
    static class Knife4jUiAutoConfiguration implements SparkAutoConfiguration {

        /**
         * Gets library type *
         *
         * @return the library type
         * @since 1.4.0
         */
        @Override
        public LibraryEnum getLibraryType() {
            return LibraryEnum.SWAGGER_REST_BOOTSTRAP;
        }
    }
}
