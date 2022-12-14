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
 * <p>Description: ?????? RestTemplate </p>
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
        log.info("?????? Agent Rest ????????????: [{}]", AgentAdapterRestConfiguration.class);
        try {
            Class.forName("info.spark.starter.util.core.support.SpringVersionCheck");
        } catch (ClassNotFoundException ignored) {
            // ??????????????????????????? core ???????????????, ????????????????????????????????????, ???????????? RuntimeException
            log.info("v5 ????????????????????????: ????????? spark-element-core ???????????????");
        }
    }

    /**
     * {@link Sdk} ???????????????, ???????????? {@link Sdk#apiServiceName()}
     * ?????? {@link AgentClientProxy#createAgentRequestBuilder} ???????????? apiName,
     * ?????????????????? root() ??? function().
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
     * {@link SdkOperation} ???????????????, ???????????? {@link SdkOperation#apiServiceName()}
     * ?????? {@link AgentClientProxy#createAgentRequestBuilder} ???????????? apiName,
     * ?????????????????? root() ??? function().
     * ???????????????????????????????????????????????? agent client ???????????????????????? sdk client ??????
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
     * ????????????????????????, ?????????????????????????????????????????????????????????????????????, ???????????????????????????????????????????????????, ??????????????????.
     * ?????????????????? javax.el ?????????Check that you have the EL dependencies on the classpath, or use ParameterMessageInterpolator instead
     * ???????????? {@link ParameterMessageInterpolator} ??????
     * https://cloud.tencent.com/developer/article/1497728
     *
     * @return the validator
     * @since 1.9.0
     */
    @Bean
    public Validator validator() {
        log.info("????????????????????????????????????");
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            // ??????????????????
            .failFast(true)
            // ??????????????? EL ?????????
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
         * <p>Description: ????????? AgentTemplate </p>
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
         * ????????? {@link RestTemplate} ????????????
         *
         * @param idServiceObjectProvider ????????? id ????????????
         * @return the load balancer interceptor
         * @since 1.0.0
         */
        @Bean
        public TraceInterceptor contextInterceptor(@NotNull ObjectProvider<IdService> idServiceObjectProvider) {
            return new TraceInterceptor(idServiceObjectProvider.getIfAvailable());
        }

        /**
         * ???????????????????????????????????? clientId.
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
                // ????????????
                restTemplate.setInterceptors(new ArrayList<>(set));
            };
        }

        /**
         * ??? bean ??????????????? bean ????????????, ???????????? RestTemplate, ?????????????????????????????? RestTemplate.
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
            // ?????? LoadBalancerAutoConfiguration ?????? ClientHttpRequestFactory
            AgentTemplate agentTemplate = new AgentTemplate(environment, this.agentRestProperties);

            // ?????????????????????
            agentTemplate.setErrorHandler(new AgentResponseErrorHandler());
            // ???????????????
            addConverters(agentTemplate);

            return agentTemplate;
        }

        /**
         * ?????????????????????
         *
         * @param restTemplate agent template
         * @since 1.0.0
         */
        private static void addConverters(@NotNull RestTemplate restTemplate) {
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charsets.UTF_8));
            // ?????? json ?????????
            MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(JsonUtils.getCopyMapper());
            // ??????????????? MappingJackson2HttpMessageConverter ?????????
            Iterator<HttpMessageConverter<?>> httpMessageConverterIterator = restTemplate.getMessageConverters().iterator();
            while (httpMessageConverterIterator.hasNext()) {
                if (httpMessageConverterIterator.next().getClass().isAssignableFrom(MappingJackson2HttpMessageConverter.class)) {
                    httpMessageConverterIterator.remove();
                    break;
                }
            }
            // ??????????????? MappingJackson2HttpMessageConverter ?????????
            restTemplate.getMessageConverters().add(messageConverter);
        }
    }

}
