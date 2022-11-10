package info.spark.starter.agent.autoconfigure;

import info.spark.agent.filter.AgentRequestContextFilter;
import info.spark.agent.filter.ExpandIdsContextReleaseFilter;
import info.spark.agent.filter.OrderedAgentRequestContextFilter;
import info.spark.agent.filter.TraceContextReleaseFilter;
import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.rest.autoconfigure.servlet.WebProperties;
import info.spark.starter.rest.filter.ExceptionFilter;
import info.spark.starter.rest.filter.ServletGlobalCacheFilter;
import info.spark.starter.rest.handler.ServletErrorController;
import info.spark.starter.rest.util.InnerWebUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

import javax.servlet.Servlet;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021-04-29 11:04
 * @since 1.8.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class})
@EnableConfigurationProperties(value = {ServerProperties.class, WebProperties.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class AgentWebMvcAutoConfigurationAdapter implements WebMvcConfigurer {

    /**
     * Request context filter
     *
     * @return the request context filter
     * @since 1.8.0
     */
    @Contract(" -> new")
    @Bean
    public static @NotNull AgentRequestContextFilter requestContextFilter() {
        return new OrderedAgentRequestContextFilter();
    }

    /**
     * Filter 异常处理
     *
     * @param serverProperties server properties
     * @return the filter registration bean
     * @see ExceptionFilter
     * @see ServletErrorController
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(ExceptionFilter.class)
    @ConditionalOnProperty(value = ConfigKey.WEB_ENABLE_EXCEPTION_FILTER,
                           havingValue = ConfigDefaultValue.TRUE_STRING,
                           matchIfMissing = true)
    public FilterRegistrationBean<ExceptionFilter> errorFilterProxy(ServerProperties serverProperties) {
        log.debug("加载 Filter 异常处理器 [{}]", ExceptionFilter.class);
        FilterRegistrationBean<ExceptionFilter> bean = new FilterRegistrationBean<>(new ExceptionFilter(serverProperties));
        InnerWebUtils.setUrlPatterns(bean, Ordered.HIGHEST_PRECEDENCE + 50);
        return bean;
    }

    /**
     * request 和 response 缓存
     *
     * @param webProperties web properties
     * @return the filter registration bean
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(ServletGlobalCacheFilter.class)
    @ConditionalOnProperty(value = ConfigKey.WEB_ENABLE_GLOBAL_CACHE_FILTER,
                           havingValue = ConfigDefaultValue.TRUE_STRING,
                           matchIfMissing = true)
    public FilterRegistrationBean<ServletGlobalCacheFilter> servletRequestCacheFilter(@NotNull WebProperties webProperties) {
        log.debug("加载 Request & Response Cahce 过滤器 [{}]", ServletGlobalCacheFilter.class);
        ServletGlobalCacheFilter servletGlobalCacheFilter = new ServletGlobalCacheFilter(webProperties.getIgnoreCacheRequestUrl());
        FilterRegistrationBean<ServletGlobalCacheFilter> bean = new FilterRegistrationBean<>(servletGlobalCacheFilter);
        InnerWebUtils.setUrlPatterns(bean, Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    /**
     * 请求结束后释放线程中的 traceId
     *
     * @return the filter registration bean
     * @since 1.8.0
     */
    @Bean
    @ConditionalOnMissingBean(TraceContextReleaseFilter.class)
    public FilterRegistrationBean<TraceContextReleaseFilter> traceContextReleaseFilter() {
        log.debug("加载 TraceContextReleaseFilter 过滤器 [{}]", TraceContextReleaseFilter.class);
        FilterRegistrationBean<TraceContextReleaseFilter> filter = new FilterRegistrationBean<>(new TraceContextReleaseFilter());
        filter.setUrlPatterns(Collections.singletonList("/*"));
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filter;
    }

    /**
     * 请求结束后释放线程中的 clientId 和 tenantId
     *
     * @return the filter registration bean
     * @since 1.8.0
     */
    @Bean
    @ConditionalOnMissingBean(ExpandIdsContextReleaseFilter.class)
    public FilterRegistrationBean<ExpandIdsContextReleaseFilter> expandIdsContextReleaseFilter() {
        log.debug("加载 ExpandIdsContextReleaseFilter 过滤器 [{}]", ExpandIdsContextReleaseFilter.class);
        FilterRegistrationBean<ExpandIdsContextReleaseFilter> filter = new FilterRegistrationBean<>(new ExpandIdsContextReleaseFilter());
        filter.setUrlPatterns(Collections.singletonList("/*"));
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return filter;
    }

}
