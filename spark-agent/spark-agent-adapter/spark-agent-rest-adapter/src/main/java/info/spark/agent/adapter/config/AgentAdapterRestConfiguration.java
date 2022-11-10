package info.spark.agent.adapter.config;

import info.spark.agent.adapter.ClientService;
import info.spark.agent.adapter.TenantService;
import info.spark.agent.adapter.annotation.Sdk;
import info.spark.agent.adapter.annotation.SdkOperation;
import info.spark.agent.adapter.aop.SdkAnnotationAdvisor;
import info.spark.agent.adapter.aop.SdkOperationAnnotationAdvisor;
import info.spark.agent.adapter.client.AgentTemplate;
import info.spark.agent.adapter.exception.AgentResponseErrorHandler;
import info.spark.agent.adapter.interceptor.ApplicationNameInterceptor;
import info.spark.agent.adapter.interceptor.ClientHostInterceptor;
import info.spark.agent.adapter.interceptor.ClientIdInterceptor;
import info.spark.agent.adapter.interceptor.SignatureInterceptor;
import info.spark.agent.adapter.interceptor.TenantIdInterceptor;
import info.spark.agent.adapter.interceptor.TraceInterceptor;
import info.spark.agent.adapter.registrar.AgentClientProxy;
import info.spark.agent.validation.Validater;

import info.spark.starter.basic.util.Charsets;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.id.service.IdService;

import org.aopalliance.aop.Advice;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 装配 RestTemplate </p>
 *
 * @author dong4j
 * @version 1.0.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.06 23:36
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class AgentAdapterRestConfiguration {

    /**
     * Agent adapter rest configuration
     *
     * @since 1.0.0
     */
    AgentAdapterRestConfiguration() {
        log.info("加载 Agent Rest 主配置类: [{}]", AgentAdapterRestConfiguration.class);
        try {
            Class.forName("info.spark.starter.util.core.support.SpringVersionCheck");
        } catch (ClassNotFoundException ignored) {
            // 未找到则说明未引入 core 及以上依赖, 如果找到则会执行静态代码, 然后抛出 RuntimeException
            log.info("v5 框架依赖检查通过: 未引入 spark-element-core 及以上依赖");
        }
    }

    /**
     * {@link Sdk} 注解拦截器, 用于获取 {@link Sdk#apiServiceName()}
     * 并在 {@link AgentClientProxy#createAgentRequestBuilder} 自动拼接 apiName,
     * 以后不再需要 root() 和 function().
     *
     * @return the advisor
     * @since 2.0.0
     */
    @Bean
    @SuppressWarnings("JavadocReference")
    public Advisor sdkAnnotationAdvisor() {
        Pointcut pointcut = new AnnotationMatchingPointcut(Sdk.class);
        Advice advice = new SdkAnnotationAdvisor();
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    /**
     * {@link SdkOperation} 注解拦截器, 用于获取 {@link SdkOperation#apiServiceName()}
     * 并在 {@link AgentClientProxy#createAgentRequestBuilder} 自动拼接 apiName,
     * 以后不再需要 root() 和 function().
     * 主要是为了解决业务上可能会将多个 agent client 方法组装到同一个 sdk client 中。
     *
     * @return the advisor
     * @since 2.1.0
     */
    @Bean
    @SuppressWarnings("JavadocReference")
    public Advisor sdkOperationAnnotationAdvisor() {
        Pointcut pointcut = new AnnotationMatchingPointcut(null, SdkOperation.class);
        Advice advice = new SdkOperationAnnotationAdvisor();
        return new DefaultPointcutAdvisor(pointcut, advice);
    }


    /**
     * 参数验证快速失败, 默认是验证完所有参数然后将所有错误信息一起返回, 这里在生成环境时修改为快速失败模式, 提高验证效率.
     * 为避免未引入 javax.el 导致：Check that you have the EL dependencies on the classpath, or use ParameterMessageInterpolator instead
     * 这里使用 {@link ParameterMessageInterpolator} 代替
     * https://cloud.tencent.com/developer/article/1497728
     *
     * @return the validator
     * @since 1.9.0
     */
    @Bean
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
     * Validater
     *
     * @param validator validator
     * @return the validater
     * @since 1.9.0
     */
    @Bean
    public Validater validater(Validator validator) {
        return new Validater(validator);
    }

    /**
         * <p>Description: 自定义 AgentTemplate </p>
     *
     * @author dong4j
     * @version 1.0.4
     * @email "mailto:dongshijie@gmail.com"
     * @date 2020.02.08 11:49
     * @since 1.0.0
     */
    @Slf4j
    @Import(AgentRestProperties.class)
    @Configuration
    static class AgentTemplateAutoConfiguration {
        /** Agent rest properties */
        private final AgentRestProperties agentRestProperties;

        /**
         * Rest template auto configuration
         *
         * @param agentRestProperties agent rest properties
         * @since 1.6.0
         */
        @Contract(pure = true)
        AgentTemplateAutoConfiguration(AgentRestProperties agentRestProperties) {
            this.agentRestProperties = agentRestProperties;
        }

        /**
         * 实例化 {@link RestTemplate} 的拦截器
         *
         * @param idServiceObjectProvider 分布式 id 服务接口
         * @return the load balancer interceptor
         * @since 1.0.0
         */
        @Bean
        public TraceInterceptor contextInterceptor(@NotNull ObjectProvider<IdService> idServiceObjectProvider) {
            return new TraceInterceptor(idServiceObjectProvider.getIfAvailable());
        }

        /**
         * 用于业务端实现怎么去获取 clientId.
         *
         * @param serviceObjectProvider service object provider
         * @return the client id interceptor
         * @since 1.8.0
         */
        @Bean
        public ClientIdInterceptor clientIdInterceptor(@NotNull ObjectProvider<ClientService> serviceObjectProvider) {
            ClientService clientService = serviceObjectProvider.getIfAvailable();
            if (clientService == null) {
                clientService = new ClientService() {};
            }
            return new ClientIdInterceptor(clientService);
        }

        /**
         * Tenant id interceptor
         *
         * @param serviceObjectProvider service object provider
         * @return the tenant id interceptor
         * @since 1.8.0
         */
        @Bean
        public TenantIdInterceptor tenantIdInterceptor(@NotNull ObjectProvider<TenantService> serviceObjectProvider) {
            TenantService tenantService = serviceObjectProvider.getIfAvailable();
            if (tenantService == null) {
                tenantService = new TenantService() {};
            }
            return new TenantIdInterceptor(tenantService);
        }

        /**
         * Application name interceptor
         *
         * @return the application name interceptor
         * @since 1.8.0
         */
        @Bean
        public ApplicationNameInterceptor applicationNameInterceptor() {
            return new ApplicationNameInterceptor();
        }

        /**
         * Client host interceptor
         *
         * @return the client host interceptor
         * @since 1.8.0
         */
        @Bean
        public ClientHostInterceptor clientHostInterceptor() {
            return new ClientHostInterceptor();
        }

        /**
         * Agent trace interceptor
         *
         * @param serviceObjectProvider service object provider
         * @return the agent trace interceptor
         * @since 1.0.0
         */
        @Bean
        public SignatureInterceptor agentTraceInterceptor(@NotNull ObjectProvider<ClientService> serviceObjectProvider) {
            ClientService clientService = serviceObjectProvider.getIfAvailable();

            if (clientService == null) {
                clientService = new ClientService() {};
            }

            return new SignatureInterceptor(clientService);
        }

        /**
         * Consumer rest template customizer
         *
         * @param listObjectProvider list object provider
         * @return the rest template customizer
         * @see AnnotationAwareOrderComparator#sort(java.util.List)
         * @since 1.5.0
         */
        @Bean
        public RestTemplateCustomizer agentConsumerRestTemplateCustomizer(
            ObjectProvider<List<ClientHttpRequestInterceptor>> listObjectProvider) {
            return restTemplate -> {
                Set<ClientHttpRequestInterceptor> set = new HashSet<>(restTemplate.getInterceptors());
                List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors = listObjectProvider.getIfAvailable();
                if (clientHttpRequestInterceptors != null && !clientHttpRequestInterceptors.isEmpty()) {
                    set.addAll(clientHttpRequestInterceptors);
                }
                // 自带排序
                restTemplate.setInterceptors(new ArrayList<>(set));
            };
        }

        /**
         * 在 bean 实例化后对 bean 进行处理, 遍历每个 RestTemplate, 将自定义拦截器添加到 RestTemplate.
         *
         * @param agentConsumerRestTemplateCustomizer agent consumer rest template customizer
         * @param agentTemplate                       agentTemplate
         * @return the smart initializing singleton
         * @since 1.8.0
         */
        @Bean
        public SmartInitializingSingleton agentTemplateInitalizing(RestTemplateCustomizer agentConsumerRestTemplateCustomizer,
                                                                   AgentTemplate agentTemplate) {
            return () -> agentConsumerRestTemplateCustomizer.customize(agentTemplate);
        }

        /**
         * Rest template rest template
         *
         * @param environment environment
         * @return the rest template
         * @since 1.0.0
         */
        @Bean
        @Primary
        public AgentTemplate agentTemplate(Environment environment) {
            // 交由 LoadBalancerAutoConfiguration 注入 ClientHttpRequestFactory
            AgentTemplate agentTemplate = new AgentTemplate(environment, this.agentRestProperties);

            // 添加错误处理器
            agentTemplate.setErrorHandler(new AgentResponseErrorHandler());
            // 添加转换器
            addConverters(agentTemplate);

            return agentTemplate;
        }

        /**
         * 添加消息转换器
         *
         * @param restTemplate agent template
         * @since 1.0.0
         */
        private static void addConverters(@NotNull RestTemplate restTemplate) {
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charsets.UTF_8));
            // 设置 json 转换器
            MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(JsonUtils.getCopyMapper());
            // 删除默认的 MappingJackson2HttpMessageConverter 转换器
            Iterator<HttpMessageConverter<?>> httpMessageConverterIterator = restTemplate.getMessageConverters().iterator();
            while (httpMessageConverterIterator.hasNext()) {
                if (httpMessageConverterIterator.next().getClass().isAssignableFrom(MappingJackson2HttpMessageConverter.class)) {
                    httpMessageConverterIterator.remove();
                    break;
                }
            }
            // 使用自定义 MappingJackson2HttpMessageConverter 转换器
            restTemplate.getMessageConverters().add(messageConverter);
        }
    }

}
