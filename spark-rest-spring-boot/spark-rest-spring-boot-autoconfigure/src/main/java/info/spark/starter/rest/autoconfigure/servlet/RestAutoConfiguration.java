package info.spark.starter.rest.autoconfigure.servlet;

import info.spark.starter.common.constant.App;
import info.spark.starter.common.enums.LibraryEnum;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.rest.autoconfigure.JacksonConfiguration;
import info.spark.starter.rest.autoconfigure.XssProperties;
import info.spark.starter.rest.handler.CustomizeReturnValueHandler;
import info.spark.starter.rest.interceptor.AuthenticationInterceptor;
import info.spark.starter.rest.interceptor.CurrentUserInterceptor;
import info.spark.starter.rest.interceptor.TraceInterceptor;
import info.spark.starter.rest.spi.RestLauncherInitiation;
import info.spark.starter.rest.support.SparkRestComponent;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.rest.runner.OpenBrowserRunner;
import info.spark.starter.rest.support.CurrentUserArgumentResolver;
import info.spark.starter.rest.support.CurrentUserService;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: rest 自动装配</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:41
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RestProperties.class)
@ConditionalOnClass(RestLauncherInitiation.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.ANY)
public class RestAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Gets library type *
     *
     * @return the library type
     * @since 1.0.0
     */
    @Override
    public LibraryEnum getLibraryType() {
        return LibraryEnum.REST;
    }

    /**
     * 定义组件标识 bean
     *
     * @return the spark component bean
     * @since 1.7.1
     */
    @Primary
    @Bean(App.Components.SPARK_REST_SPRING_BOOT)
    public SparkRestComponent restComponent() {
        return new SparkRestComponent();
    }

    /**
     * 参数验证快速失败, 默认是验证完所有参数然后将所有错误信息一起返回, 这里在生成环境时修改为快速失败模式, 提高验证效率.
     *
     * @return the validator
     * @since 1.0.0
     */
    @Bean
    @Profile(value = {App.ENV_PROD})
    public Validator validator() {
        log.info("参数验证开启快速失败模式");
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            // 快速失败模式
            .failFast(true)
            // 代替默认的 EL 表达式
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    /**
     * Open browser runner open browser runner
     *
     * @param restProperties rest properties
     * @return the open browser runner
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenBrowserRunner openBrowserRunner(@NotNull RestProperties restProperties) {
        return new OpenBrowserRunner(restProperties.isEnableBrowser());
    }

    /**
         * <p>Description: 使用 {@link ServletWebConfiguration} 配置的 {@link HandlerMethodReturnValueHandler} 的优先级最低, 因此使用这种方式修改</p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.12.07 14:40
     * @since 1.7.0
     */
    @Slf4j
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class ConsumerMethodReturnValueHandlerAutoConfiguration implements SparkAutoConfiguration {
        /** Adapter */
        @Resource
        private RequestMappingHandlerAdapter adapter;

        /**
         * After properties set
         *
         * @since 1.7.0
         */
        @Override
        public void afterPropertiesSet() {
            List<HandlerMethodReturnValueHandler> returnValueHandlers = this.adapter.getReturnValueHandlers();
            List<HandlerMethodReturnValueHandler> handlers = Collections.emptyList();
            if (CollectionUtils.isNotEmpty(returnValueHandlers)) {
                handlers = new ArrayList<>(returnValueHandlers);
            }
            this.decorateHandlers(handlers);
            this.adapter.setReturnValueHandlers(handlers);
        }

        /**
         * 重新设置优先级
         *
         * @param handlers handlers
         * @since 1.7.0
         */
        private void decorateHandlers(List<HandlerMethodReturnValueHandler> handlers) {
            for (HandlerMethodReturnValueHandler handler : handlers) {
                if (handler instanceof RequestResponseBodyMethodProcessor) {
                    CustomizeReturnValueHandler decorator = new CustomizeReturnValueHandler(
                        (RequestResponseBodyMethodProcessor) handler);
                    int index = handlers.indexOf(handler);
                    handlers.set(index, decorator);
                    break;
                }
            }
        }
    }


    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.01.06 16:43
     * @since 1.7.0
     */
    @Configuration(proxyBeanMethods = false)
    @AutoConfigureAfter(JacksonConfiguration.class)
    @EnableConfigurationProperties(value = {ServerProperties.class, XssProperties.class})
    @ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class})
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class ServletAutoConfiguration implements SparkAutoConfiguration {

        /**
         * Current user argument resolver
         *
         * @return the current user argument resolver
         * @since 1.6.0
         */
        @Bean
        @ConditionalOnMissingBean
        public CurrentUserArgumentResolver currentUserArgumentResolver() {
            return new CurrentUserArgumentResolver();
        }

        /**
         * Current user service
         *
         * @return the current user service
         * @since 1.6.0
         */
        @Bean
        @ConditionalOnMissingBean
        public CurrentUserService currentUserService() {
            return new CurrentUserService() {
            };
        }

        /**
         * Current user interceptor
         *
         * @param currentUserService current user service
         * @return the current user interceptor
         * @since 1.6.0
         */
        @Bean
        @ConditionalOnMissingBean
        public CurrentUserInterceptor currentUserInterceptor(CurrentUserService currentUserService) {
            return new CurrentUserInterceptor(currentUserService);
        }

        /**
         * Authentication interceptor
         *
         * @param currentUserService current user service
         * @return the authentication interceptor
         * @since 2.0.0
         */
        @Bean
        @ConditionalOnMissingBean
        public AuthenticationInterceptor authenticationInterceptor(CurrentUserService currentUserService) {
            return new AuthenticationInterceptor(currentUserService);
        }

        /**
         * 默认使用 uuid 作为 traceId, 如果使用了 tracer 组件则会被替换
         *
         * @return the trace interceptor
         * @since 2022.1.1
         */
        @Bean
        @ConditionalOnMissingBean(TraceInterceptor.class)
        public TraceInterceptor restTraceInterceptor() {
            return new TraceInterceptor();
        }

    }
}
