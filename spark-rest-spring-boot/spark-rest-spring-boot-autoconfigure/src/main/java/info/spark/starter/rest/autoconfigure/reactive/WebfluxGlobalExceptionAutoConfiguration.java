package info.spark.starter.rest.autoconfigure.reactive;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.rest.handler.SparkWebfluxExceptionErrorAttributes;
import info.spark.starter.rest.handler.JsonErrorWebExceptionHandler;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:24
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(WebFluxConfigurer.class)
@AutoConfigureBefore(WebFluxAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(value = {ServerProperties.class, ResourceProperties.class})
public class WebfluxGlobalExceptionAutoConfiguration implements SparkAutoConfiguration {
    /** Server properties */
    private final ServerProperties serverProperties;
    /** Application context */
    private final ApplicationContext applicationContext;
    /** Resource properties */
    private final ResourceProperties resourceProperties;
    /** View resolvers */
    private final List<ViewResolver> viewResolvers;
    /** Server codec configurer */
    private final ServerCodecConfigurer serverCodecConfigurer;

    /**
     * Instantiates a new Custom error web flux auto configuration.
     *
     * @param serverProperties      the server properties
     * @param resourceProperties    the resource properties
     * @param viewResolversProvider the view resolvers provider
     * @param serverCodecConfigurer the server codec configurer
     * @param applicationContext    the application context
     * @since 1.0.0
     */
    public WebfluxGlobalExceptionAutoConfiguration(ServerProperties serverProperties,
                                                   ResourceProperties resourceProperties,
                                                   @NotNull ObjectProvider<ViewResolver> viewResolversProvider,
                                                   ServerCodecConfigurer serverCodecConfigurer,
                                                   ApplicationContext applicationContext) {
        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
        this.resourceProperties = resourceProperties;
        this.viewResolvers = viewResolversProvider.orderedStream().collect(Collectors.toList());
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * Server codec configurer server codec configurer
     *
     * @return the server codec configurer
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return new DefaultServerCodecConfigurer();
    }

    /**
     * 自定义全局异常处理, 替代 DefaultErrorWebExceptionHandler
     *
     * @param errorAttributes the error attributes
     * @return the error web exception handler
     * @since 1.0.0
     */
    @Bean
    @Order(-2)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
        log.info("初始化自定义全局异常处理器: {}", JsonErrorWebExceptionHandler.class);
        JsonErrorWebExceptionHandler exceptionHandler = new JsonErrorWebExceptionHandler(errorAttributes,
                                                                                         this.resourceProperties,
                                                                                         this.serverProperties.getError(),
                                                                                         this.applicationContext);
        exceptionHandler.setViewResolvers(this.viewResolvers);
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }

    /**
     * Error attributes default error attributes.
     *
     * @return the default error attributes
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    public DefaultErrorAttributes errorAttributes() {
        return new SparkWebfluxExceptionErrorAttributes(!ConfigKit.isProd());
    }

}

