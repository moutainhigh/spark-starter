package info.spark.starter.agent.autoconfigure;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import info.spark.agent.SparkAgentServiceComponent;
import info.spark.agent.NonceCacheSevice;
import info.spark.agent.SecretCacheService;
import info.spark.agent.SecretService;
import info.spark.agent.core.AgentProperties;
import info.spark.agent.endpoint.AgentClientEndpointHandlerMapping;
import info.spark.agent.endpoint.AgentEndpoint;
import info.spark.agent.endpoint.exception.AgentGlobalExceptionHandler;
import info.spark.agent.plugin.ApiServiceCodec;
import info.spark.agent.plugin.ApiServiceExpandIdsCheck;
import info.spark.agent.plugin.ApiServiceReplayCheck;
import info.spark.agent.plugin.ApiServiceSignCheck;
import info.spark.agent.plugin.ApiServiceValidate;
import info.spark.agent.plugin.impl.DefaultApiServiceExpandIdsCheck;
import info.spark.agent.plugin.impl.DefaultApiServiceReplyCheck;
import info.spark.agent.plugin.impl.DefaultApiServiceSignCheck;
import info.spark.agent.plugin.impl.HibernateValidate;
import info.spark.agent.plugin.impl.JacksonCodec;
import info.spark.agent.sender.AgentService;
import info.spark.agent.sender.impl.EmbeddedAgentServiceImpl;
import info.spark.starter.autoconfigure.task.ConsumerTaskExecutionAutoConfiguration;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.enums.LibraryEnum;
import info.spark.starter.common.exception.StarterException;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.endpoint.EndpointHandlerMapping;
import info.spark.starter.rest.exception.ServletGlobalExceptionHandler;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.5
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.05 09:52
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(AgentEndpoint.class)
@EnableConfigurationProperties(value = {AgentProperties.class})
@AutoConfigureAfter(ConsumerTaskExecutionAutoConfiguration.class)
public class AgentAutoConfiguration implements SparkAutoConfiguration {

    /**
     * 定义组件标识 bean
     *
     * @return the spark agent service component
     * @since 1.7.1
     */
    @Primary
    @Bean(App.Components.SPARK_AGENT_SPRING_BOOT)
    public SparkAgentServiceComponent agentServiceComponent() {
        return new SparkAgentServiceComponent();
    }

    /**
     * Gets library type *
     *
     * @return the library type
     * @since 1.7.1
     */
    @Override
    public List<LibraryEnum> getLibraryTypes() {
        return Arrays.asList(LibraryEnum.AGENT, LibraryEnum.REST);
    }

    /**
     * 自动将被 @Endpoint 标识的类注册为 endpoint
     *
     * @param listObjectProvider list object provider
     * @return the agent client endpoint handler mapping
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(EndpointHandlerMapping.class)
    public EndpointHandlerMapping agentClientEndpointHandlerMapping(
        @NotNull ObjectProvider<List<HandlerInterceptor>> listObjectProvider) {
        List<HandlerInterceptor> clientHttpRequestInterceptors = listObjectProvider.getIfAvailable();
        log.info("添加拦截器: [{}]", clientHttpRequestInterceptors);
        return new AgentClientEndpointHandlerMapping(clientHttpRequestInterceptors);
    }

    /**
     * 默认 json 序列化与反序列化
     *
     * @return the api service codec
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(ApiServiceCodec.class)
    public ApiServiceCodec<?, ?> jacksonCodec() {
        return new JacksonCodec();
    }

    /**
     * 参数验证快速失败, 默认是验证完所有参数然后将所有错误信息一起返回, 这里在生成环境时修改为快速失败模式, 提高验证效率.
     *
     * @return the validator
     * @since 1.9.0
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
     * 默认使用 Hibernate validate
     *
     * @param validator validator
     * @return the api service validate
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(ApiServiceValidate.class)
    public ApiServiceValidate hibernateValidate(Validator validator) {
        return new HibernateValidate(validator);
    }

    /**
     * 参数签名检查
     *
     * @param secretCacheService secret cache service
     * @param agentProperties    agent properties
     * @return the api service sign check
     * @since 1.6.0
     */
    @Bean
    @ConditionalOnMissingBean(ApiServiceSignCheck.class)
    public ApiServiceSignCheck defaultApiServiceSignCheck(SecretCacheService secretCacheService,
                                                          @NotNull AgentProperties agentProperties) {
        return new DefaultApiServiceSignCheck(secretCacheService,
                                              agentProperties.getEndpoint().isEnableSignCheck());
    }

    /**
     * Default api service reply check
     *
     * @param nonceCacheSevice cache secret sevice
     * @param agentProperties  agent properties
     * @return the api service reply check
     * @since 1.6.0
     */
    @Bean
    @ConditionalOnMissingBean(ApiServiceReplayCheck.class)
    public ApiServiceReplayCheck defaultApiServiceReplyCheck(NonceCacheSevice nonceCacheSevice,
                                                             @NotNull AgentProperties agentProperties) {
        return new DefaultApiServiceReplyCheck(nonceCacheSevice,
                                               agentProperties.getEndpoint().isEnableReplyCheck());
    }

    /**
     * 校验扩展的ids，业务端是否必传
     *
     * @param agentProperties agent properties
     * @return the api service expand ids check
     * @since 1.8.0
     */
    @Bean
    @ConditionalOnMissingBean(ApiServiceExpandIdsCheck.class)
    public ApiServiceExpandIdsCheck defaultApiServiceExpandIdsCheck(@NotNull AgentProperties agentProperties) {
        return new DefaultApiServiceExpandIdsCheck(agentProperties.getEndpoint().isEnableExpandIdsCheck());
    }

    /**
     * 默认本地代理
     *
     * @param agentProperties      agent properties
     * @param applicationContext   application context
     * @param boostExecutorService boost executor service
     * @return the agent service
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(AgentService.class)
    @SuppressWarnings("all")
    public AgentService agentService(@NotNull AgentProperties agentProperties,
                                     ApplicationContext applicationContext,
                                     ExecutorService boostExecutorService) {
        return new EmbeddedAgentServiceImpl(agentProperties,
                                            applicationContext,
                                            boostExecutorService);
    }

    /**
     * Secret cache service
     *
     * @param secretService secret service
     * @return the secret cache service
     * @since 1.6.0
     */
    @Bean
    @ConditionalOnMissingBean(SecretCacheService.class)
    public SecretCacheService secretCacheService(SecretService secretService) {
        return new SecretCacheService() {
            final Cache<String, String> cache = CacheBuilder.newBuilder().build();

            /**
             * Gets secret *
             *
             * @return the secret
             * @since 1.6.0
             */
            @SneakyThrows
            @Override
            public String get(String clientId) {
                return this.cache.get(clientId, () -> secretService.load(clientId));
            }

            /**
             * Set
             *
             * @param key   client id
             * @param value secret
             * @since 1.6.0
             */
            @Override
            public void set(String key, String value) {
                this.cache.put(key, value);
            }
        };
    }

    /**
     * Secret service
     *
     * @return the secret service
     * @since 1.6.0
     */
    @Bean
    @ConditionalOnMissingBean(SecretService.class)
    public SecretService secretService() {
        // todo-dong4j : (2020.08.26 18:08) [先使用固定值, 后期从多租户平台获取]
        return clientId -> "ab588a75-ad54-4105-9dbe-30360d4d28c4";
    }

    /**
     * Cache secret sevice
     *
     * @param agentProperties agent properties
     * @return the cache secret sevice
     * @since 1.6.0
     */
    @Bean
    @ConditionalOnMissingBean(NonceCacheSevice.class)
    public NonceCacheSevice nonceCacheSevice(@NotNull AgentProperties agentProperties) {
        return new NonceCacheSevice() {

            final Cache<String, String> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(agentProperties.getEndpoint().getNonceExpiredTime().toMillis(), TimeUnit.MILLISECONDS)
                .build();

            /**
             * Gets secret *
             *
             * @param key client id
             * @return the secret
             * @since 1.6.0
             */
            @Override
            public String get(String key) {
                return this.cache.getIfPresent(key);
            }

            /**
             * Set
             *
             * @param key client id
             * @param value   secret
             * @since 1.6.0
             */
            @Override
            public void set(String key, String value) {
                this.cache.put(key, value);
            }
        };
    }

    /**
         * <p>Description: 检查是否存在 spark-rest-spring-boot 相关依赖 </p>
     * 这里不能用 {@link ConditionalOnClass} 判断, 如果 agent service 项目内有 api 相关的项目,
     * 虽然是独立部署的, 但是由于 classpath 存在 rest 相关依赖将导致 api 启动失败.
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.01.16 02:04
     * @since 1.7.1
     */
    @Configuration(proxyBeanMethods = false)
    static class CheckRestDependent implements SparkAutoConfiguration {
        /** Application context */
        @Resource
        private ApplicationContext applicationContext;

        /**
         * 如果 getBean() 抛出异常说明存在 spark-rest-spring-boot 相关依赖
         *
         * @since 1.7.1
         */
        @Override
        public void execute() {
            try {
                this.applicationContext.getBean(App.Components.SPARK_REST_SPRING_BOOT);
            } catch (Exception e) {
                return;
            }
            throw new StarterException("Agent Service 不需要依赖 'spark-rest-servlet-spring-boot-starter'");
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.05.26 18:29
     * @since 1.9.0
     */
    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
    @AutoConfigureAfter(WebMvcAutoConfiguration.class)
    static class WebMvcStringTrimAutoConfiguration implements SparkAutoConfiguration {

        /**
                 * <p>Description: </p>
         *
         * @author dong4j
         * @version 1.0.0
         * @email "mailto:dong4j@gmail.com"
         * @date 2021.05.25 13:55
         * @since 1.9.0
         */
        @ControllerAdvice
        public static class ControllerStringParamTrimConfig {

            /**
             * 处理 url 或 form 表单中的参数.
             * {@link StringTrimmerEditor}: 构造方法中 boolean 参数含义为如果是空白字符串,是否转换为null, 即如果为true,那么 " " 会被转换为 null,否者为 ""
             *
             * @param binder binder
             * @since 1.9.0
             */
            @InitBinder
            public void initBinder(WebDataBinder binder) {
                StringTrimmerEditor propertyEditor = new StringTrimmerEditor(false);
                binder.registerCustomEditor(String.class, propertyEditor);
            }
        }

    }

    /**
     * Agent global exception handler
     *
     * @return the servlet global exception handler
     * @since 2022.1.1
     */
    @Bean
    public ServletGlobalExceptionHandler agentGlobalExceptionHandler() {
        return new AgentGlobalExceptionHandler();
    }

}
