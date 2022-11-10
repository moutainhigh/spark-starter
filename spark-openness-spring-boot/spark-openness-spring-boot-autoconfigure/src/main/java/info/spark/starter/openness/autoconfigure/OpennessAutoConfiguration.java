package info.spark.starter.openness.autoconfigure;

import info.spark.agent.adapter.client.AgentTemplate;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.openness.filter.OpennessFilter;
import info.spark.starter.openness.handler.IBucketListHandler;
import info.spark.starter.openness.handler.IResourceAclHandler;
import info.spark.starter.openness.handler.ISecretAuthHandler;
import info.spark.starter.openness.service.impl.OpennessAlgorithmServiceImpl;
import info.spark.starter.rest.filter.ExceptionFilter;
import info.spark.starter.util.StringUtils;
import info.spark.starter.openness.handler.impl.DefaultSecretAuthHandler;
import info.spark.starter.openness.service.AbstractOpennessAlgorithm;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:
 * todo-dong4j : (2021-10-7 20:39) [
 * 按照这个装配类意思就是必须依赖 spark-agent-rest-adapter, spark-cache-spring-boot 和 spark-rest-servlet-spring-boot-starter
 * 1. 将 DefaultSecretAuthHandler 里面的 AgentTemplate 抽象一个接口出来, 如果用户依赖了 spark-agent-rest-adapter 就用 AgentTemplate, 没有就自己实现;
 * 2. CacheService 同理 (如果用户依赖了 cache 组件但是没有配置 redis 的 url, 则注入的 CacheService 默认是 CaffeineCacheServiceImpl, 不支持 hset);
 * 3. RequestMappingHandlerMapping 的实现类有 2 个,
 * 且 OpennessFilter 的优先级比 {@link ExceptionFilter} 高, 因此不能直接在 doFilterInternal 抛出异常;
 * ]</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.04 22:32
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OpennessProperties.class)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class})
public class OpennessAutoConfiguration implements SparkAutoConfiguration {

    /**
     * 如果没有其他实现，默认使用 调用 v5-user 验证，得到 secretKey
     *
     * @param agentTemplate agent template
     * @return the secret auth handler
     * @since 1.9.0
     */
    @Bean
    @ConditionalOnMissingBean
    public ISecretAuthHandler secretAuthHandler(AgentTemplate agentTemplate) {
        return new DefaultSecretAuthHandler(agentTemplate);
    }

    /**
     * Openness service
     *
     * @param secretAuthHandler  secret auth handler
     * @param opennessProperties openness properties
     * @return the openness client
     * @since 1.6.0
     */
    @Bean
    @ConditionalOnMissingBean(OpennessAlgorithmServiceImpl.class)
    public AbstractOpennessAlgorithm opennessAesAlgorithmService(ISecretAuthHandler secretAuthHandler,
                                                                 OpennessProperties opennessProperties) {
        return new OpennessAlgorithmServiceImpl(secretAuthHandler,
                                                opennessProperties.getTimeInterval(),
                                                opennessProperties.getNonceLength());
    }

    /**
     * Openness filter
     *
     * @param requestMappingHandlerMapping request mapping handler mapping
     * @param opennessClientAuthService    openness client auth service
     * @param opennessProperties           openness properties
     * @param resourceAclHandlers          resource acl handlers
     * @param bucketListHandlers           bucket list handlers
     * @return the openness filter
     * @since 1.6.0
     */
    @Bean
    @ConditionalOnProperty(name = "spark.openness.enabled", havingValue = "true")
    public FilterRegistrationBean<Filter> opennessFilterProxy(@NotNull RequestMappingHandlerMapping requestMappingHandlerMapping,
                                                              @NotNull AbstractOpennessAlgorithm opennessClientAuthService,
                                                              @NotNull OpennessProperties opennessProperties,
                                                              ObjectProvider<IResourceAclHandler> resourceAclHandlers,
                                                              ObjectProvider<IBucketListHandler> bucketListHandlers) {

        log.info("加载 Filter 第三方请求接口鉴权、统计处理, callback 鉴权处理 [{}]", OpennessFilter.class);

        OpennessFilter opennessFilter = new OpennessFilter(requestMappingHandlerMapping,
                                                           opennessClientAuthService,
                                                           resourceAclHandlers.getIfAvailable(() -> new IResourceAclHandler() {}),
                                                           bucketListHandlers.getIfAvailable(() -> new IBucketListHandler() {}));
        opennessFilter.setIncludePatterns(opennessProperties.getIncludePatterns());
        opennessFilter.setExcludePatterns(opennessProperties.getExcludePatterns());

        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(opennessFilter);
        String contextPath = ConfigKit.getProperty(ConfigKey.SpringConfigKey.SERVER_CONTEXT_PATH, StringPool.SLASH);
        contextPath = StringUtils.removeSuffix(contextPath, StringPool.SLASH);
        List<String> list = Collections.singletonList(contextPath + StringPool.ANY_URL_PATTERNS);
        bean.setUrlPatterns(list);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return bean;
    }


}
