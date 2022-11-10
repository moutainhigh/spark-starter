package info.spark.starter.idempotent.autoconfigure;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.cache.autoconfigure.CacheAutoConfiguration;
import info.spark.starter.cache.service.CacheService;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.idempotent.filter.ApiIdempotentFilter;
import info.spark.starter.idempotent.service.TokenService;
import info.spark.starter.idempotent.service.impl.TokenServiceImpl;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.04 22:32
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class})
@EnableConfigurationProperties(IdempotentProperties.class)
@Import(CacheAutoConfiguration.class)
public class IdempotentAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Token service
     *
     * @param cacheService         cache service
     * @param idempotentProperties idempotent properties
     * @return the token service
     * @since 1.0.0
     */
    @Bean
    public TokenService tokenService(@NotNull CacheService cacheService,
                                     @NotNull IdempotentProperties idempotentProperties) {
        return new TokenServiceImpl(cacheService, idempotentProperties.getExpire());
    }

    /**
     * Api idempotent filter proxy
     *
     * @param apiIdempotentFilter  filter
     * @return the filter registration bean
     * @since 1.0.0
     */
    @Bean
    public FilterRegistrationBean<Filter> apiIdempotentFilterProxy(@NotNull ApiIdempotentFilter apiIdempotentFilter) {
        log.info("加载 Filter 接口幂等处理器 [{}]", ApiIdempotentFilter.class);
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(apiIdempotentFilter);
        String contextPath = ConfigKit.getProperty(ConfigKey.SpringConfigKey.SERVER_CONTEXT_PATH, StringPool.SLASH);
        contextPath = StringUtils.removeSuffix(contextPath, StringPool.SLASH);
        List<String> list = Collections.singletonList(contextPath + StringPool.ANY_URL_PATTERNS);
        bean.setUrlPatterns(list);
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bean;
    }

    /**
     * Api idempotent filter
     *
     * @param requestMappingHandlerMapping request mapping handler mapping
     * @param tokenService                 token service
     * @return the api idempotent filter
     * @since 1.0.0
     */
    @Bean
    @SuppressWarnings("all")
    public ApiIdempotentFilter apiIdempotentFilter(@NotNull RequestMappingHandlerMapping requestMappingHandlerMapping,
                                                   @NotNull TokenService tokenService) {
        return new ApiIdempotentFilter(requestMappingHandlerMapping,
                                       tokenService);
    }


}
