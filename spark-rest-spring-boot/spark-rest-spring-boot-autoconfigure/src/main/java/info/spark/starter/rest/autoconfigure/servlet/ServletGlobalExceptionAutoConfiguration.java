package info.spark.starter.rest.autoconfigure.servlet;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.rest.advice.RestGlobalExceptionHandler;
import info.spark.starter.rest.handler.SparkServletExceptionErrorAttributes;
import info.spark.starter.rest.exception.ServletGlobalExceptionHandler;
import info.spark.starter.rest.handler.ServletErrorController;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:23
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties(ServerProperties.class)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletGlobalExceptionAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Error attributes default error attributes
     *
     * @return the default error attributes
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    public DefaultErrorAttributes errorAttributes() {
        return new SparkServletExceptionErrorAttributes(!ConfigKit.isProd());
    }

    /**
     * Basic error controller basic error controller
     *
     * @param errorAttributes  error attributes
     * @param serverProperties server properties
     * @return the basic error controller
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes,
                                                     @NotNull ServerProperties serverProperties) {
        return new ServletErrorController(errorAttributes, serverProperties.getError());
    }

    /**
     * Rest global exception handler
     *
     * @return the servlet global exception handler
     * @since 2022.1.1
     */
    @Bean
    public ServletGlobalExceptionHandler restGlobalExceptionHandler() {
        return new RestGlobalExceptionHandler();
    }
}
