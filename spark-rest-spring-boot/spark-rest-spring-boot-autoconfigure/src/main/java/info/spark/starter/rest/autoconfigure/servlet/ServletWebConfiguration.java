package info.spark.starter.rest.autoconfigure.servlet;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.basic.util.SecurityUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.enums.SerializeEnum;
import info.spark.starter.common.enums.serialize.EntityEnumDeserializer;
import info.spark.starter.common.enums.serialize.EntityEnumSerializer;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.rest.autoconfigure.JacksonConfiguration;
import info.spark.starter.rest.autoconfigure.XssProperties;
import info.spark.starter.rest.converter.GlobalEnumConverterFactory;
import info.spark.starter.rest.converter.StringToDateConverter;
import info.spark.starter.rest.filter.GlobalParameterFilter;
import info.spark.starter.rest.interceptor.AuthenticationInterceptor;
import info.spark.starter.rest.interceptor.CurrentUserInterceptor;
import info.spark.starter.rest.support.CurrentUserArgumentResolver;
import info.spark.starter.rest.xss.XssFilter;
import info.spark.starter.util.core.jackson.MappingApiJackson2HttpMessageConverter;
import info.spark.starter.rest.filter.ExceptionFilter;
import info.spark.starter.rest.filter.ServletGlobalCacheFilter;
import info.spark.starter.rest.handler.ServletErrorController;
import info.spark.starter.rest.mapping.ApiVersionRequestMappingHandlerMapping;
import info.spark.starter.rest.support.FormdataBodyArgumentResolver;
import info.spark.starter.rest.support.RequestAbstractFormMethodArgumentResolver;
import info.spark.starter.rest.support.RequestSingleParamHandlerMethodArgumentResolver;
import info.spark.starter.rest.util.InnerWebUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.Servlet;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: WEB ?????????????????????</p>
 * 1. ?????? MappingJackson2HttpMessageConverter ?????????
 * 2. ??????????????????
 * 3. ???????????????
 * 4. ????????????
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:47
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(JacksonConfiguration.class)
@EnableConfigurationProperties(value = {ServerProperties.class, XssProperties.class, WebProperties.class})
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletWebConfiguration implements WebMvcConfigurer, SparkAutoConfiguration {

    /** Object mapper */
    @Resource
    private ObjectMapper objectMapper;
    /** Current user argument resolver */
    @Resource
    private CurrentUserArgumentResolver currentUserArgumentResolver;
    /** Current user interceptor */
    @Resource
    private CurrentUserInterceptor currentUserInterceptor;
    /** Authentication interceptor */
    @Resource
    private AuthenticationInterceptor authenticationInterceptor;
    /** GLOBAL_ENUM_CONVERTER_FACTORY */
    private static final ConverterFactory<String, SerializeEnum<?>> GLOBAL_ENUM_CONVERTER_FACTORY = new GlobalEnumConverterFactory();
    /** MAX_AGE */
    private static final Long MAX_AGE = 18000L;

    /**
     * Filter registration bean filter registration bean.
     * ?????????????????????
     *
     * @return the filter registration bean
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(CharacterEncodingFilter.class)
    public FilterRegistrationBean<CharacterEncodingFilter> filterRegistrationBean() {
        FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding(StringPool.UTF_8);
        characterEncodingFilter.setForceEncoding(true);
        registrationBean.setFilter(characterEncodingFilter);
        return registrationBean;
    }

    /**
     * request ??? response ??????
     *
     * @param properties properties
     * @return the filter registration bean
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(ServletGlobalCacheFilter.class)
    @ConditionalOnProperty(value = ConfigKey.WEB_ENABLE_GLOBAL_CACHE_FILTER,
                           havingValue = ConfigDefaultValue.TRUE_STRING,
                           matchIfMissing = true)
    public FilterRegistrationBean<ServletGlobalCacheFilter> servletRequestCacheFilter(@NotNull WebProperties properties) {
        log.debug("?????? Request & Response Cahce ????????? [{}]", ServletGlobalCacheFilter.class);
        ServletGlobalCacheFilter servletGlobalCacheFilter = new ServletGlobalCacheFilter(properties.getIgnoreCacheRequestUrl());
        FilterRegistrationBean<ServletGlobalCacheFilter> bean = new FilterRegistrationBean<>(servletGlobalCacheFilter);
        InnerWebUtils.setUrlPatterns(bean, Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    /**
     * Add cors mapping.
     * ?????? CORS ??????, ?????? prod ?????????????????????
     *
     * @return the cors filter
     * @since 1.0.0
     */
    @Bean
    @Profile(value = {App.ENV_LOCAL, App.ENV_DEV, App.ENV_TEST, App.ENV_PREV})
    @ConditionalOnMissingBean(CorsFilter.class)
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        log.debug("?????????????????????????????????: {}", CorsFilter.class);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(StringPool.ANY_PATH, this.buildConfig());
        CorsFilter corsFilter = new CorsFilter(source);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(corsFilter);
        InnerWebUtils.setUrlPatterns(bean, Ordered.HIGHEST_PRECEDENCE + 40);
        return bean;
    }

    /**
     * Filter ????????????
     *
     * @param serverProperties server properties
     * @return the filter registration bean
     * @see ExceptionFilter
     * @see ServletErrorController
     * @see RestProperties#isEnableExceptionFilter
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(ExceptionFilter.class)
    @ConditionalOnProperty(value = ConfigKey.WEB_ENABLE_EXCEPTION_FILTER,
                           havingValue = ConfigDefaultValue.TRUE_STRING,
                           matchIfMissing = true)
    public FilterRegistrationBean<ExceptionFilter> errorFilterProxy(ServerProperties serverProperties) {
        log.debug("?????? Filter ??????????????? [{}]", ExceptionFilter.class);
        FilterRegistrationBean<ExceptionFilter> bean = new FilterRegistrationBean<>(new ExceptionFilter(serverProperties));
        InnerWebUtils.setUrlPatterns(bean, Ordered.HIGHEST_PRECEDENCE + 50);
        return bean;
    }

    /**
     * ?????????????????????
     *
     * @return the filter registration bean
     * @see RestProperties#isEnableGlobalParameterFilter RestProperties#isEnableGlobalParameterFilter
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(GlobalParameterFilter.class)
    @ConditionalOnProperty(value = ConfigKey.REST_ENABLE_GLOBAL_PARAMETER_FILTER, havingValue = ConfigDefaultValue.TRUE_STRING)
    public FilterRegistrationBean<GlobalParameterFilter> parameterFilterRegistrationBean() {
        log.info("?????????????????????????????????: {}", GlobalParameterFilter.class);
        FilterRegistrationBean<GlobalParameterFilter> bean = new FilterRegistrationBean<>(new GlobalParameterFilter());
        InnerWebUtils.setUrlPatterns(bean, Ordered.LOWEST_PRECEDENCE - 1000);
        return bean;
    }

    /**
     * ??? XSS ?????? Filter
     *
     * @param xssProperties xss properties
     * @return FilterRegistrationBean filter registration bean
     * @see XssProperties#isEnableXssFilter XssProperties#isEnableXssFilterXssProperties#isEnableXssFilter
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(XssFilter.class)
    @ConditionalOnProperty(value = ConfigKey.XSS_ENABLE_XSS_FILTER, havingValue = ConfigDefaultValue.TRUE_STRING)
    public FilterRegistrationBean<XssFilter> xssFilterRegistration(XssProperties xssProperties) {
        log.debug("????????? XSS ?????? Filter: {}", XssFilter.class);
        FilterRegistrationBean<XssFilter> bean = new FilterRegistrationBean<>(new XssFilter(xssProperties.getExcludePatterns()));
        InnerWebUtils.setUrlPatterns(bean, Ordered.LOWEST_PRECEDENCE - 900);
        return bean;
    }

    /**
     * ??????????????????????????????, ??????????????? Date ??????, ????????????????????????,
     * ????????? @RequestBody ????????????, ????????? {@link MappingApiJackson2HttpMessageConverter} ?????? jackson ????????????
     *
     * @param registry the registry
     * @since 1.0.0
     */
    @Override
    public void addFormatters(@NotNull FormatterRegistry registry) {
        log.debug("?????? String -> Date ????????? :[{}] ??????: [{}]", StringToDateConverter.class, StringToDateConverter.PATTERN);
        registry.addConverter(new StringToDateConverter());
        log.debug("???????????????????????????: [{}]", GlobalEnumConverterFactory.class);
        registry.addConverterFactory(GLOBAL_ENUM_CONVERTER_FACTORY);
    }

    /**
     * Add argument resolvers *
     *
     * @param argumentResolvers argument resolvers
     * @since 1.0.0
     */
    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> argumentResolvers) {
        log.debug("?????? @RequestSingleParam ???????????????: [{}]", RequestSingleParamHandlerMethodArgumentResolver.class);
        argumentResolvers.add(new RequestSingleParamHandlerMethodArgumentResolver(this.objectMapper, GLOBAL_ENUM_CONVERTER_FACTORY));
        argumentResolvers.add(new RequestAbstractFormMethodArgumentResolver(this.objectMapper, GLOBAL_ENUM_CONVERTER_FACTORY));
        argumentResolvers.add(new FormdataBodyArgumentResolver(this.objectMapper, GLOBAL_ENUM_CONVERTER_FACTORY));
        argumentResolvers.add(this.currentUserArgumentResolver);
    }

    /**
     * ?????? JACKSON ??????JSON MessageConverter
     *
     * @param converters converters
     * @since 1.0.0
     */
    @Override
    public void configureMessageConverters(@NotNull List<HttpMessageConverter<?>> converters) {
        log.debug("???????????????????????????????????? [{}]", MappingApiJackson2HttpMessageConverter.class);
        converters.removeIf(x -> x instanceof StringHttpMessageConverter || x instanceof AbstractJackson2HttpMessageConverter);
        // Content-Type = text/plain ???????????????, ???????????? UTF-8
        converters.add(new StringHttpMessageConverter(Charsets.UTF_8));
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new BufferedImageHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new ResourceRegionHttpMessageConverter());

        this.config();

        converters.add(new MappingApiJackson2HttpMessageConverter(this.objectMapper));
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @since 1.0.0
     */
    private void config() {
        log.debug("??????????????????????????????/?????????????????????: [{}] [{}]", EntityEnumSerializer.class, EntityEnumDeserializer.class);
        SimpleModule simpleModule = new SimpleModule("EntityEnum-Converter", PackageVersion.VERSION);
        simpleModule.addDeserializer(SerializeEnum.class, new EntityEnumDeserializer<>());
        simpleModule.addSerializer(SerializeEnum.class, new EntityEnumSerializer<>());
        this.objectMapper.registerModule(simpleModule);
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * ???????????????
     *
     * @param registry registry
     * @since 1.0.0
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        log.debug("?????????????????????????????????: [{}]", CurrentUserInterceptor.class);

        registry.addInterceptor(this.currentUserInterceptor)
            .addPathPatterns(StringPool.ANY_PATH)
            .excludePathPatterns(new ArrayList<>(SecurityUtils.mergeSkipPatterns("")));

        log.debug("?????????????????????????????????: [{}]", AuthenticationInterceptor.class);
        registry.addInterceptor(this.authenticationInterceptor)
            .addPathPatterns(StringPool.ANY_PATH)
            .excludePathPatterns(new ArrayList<>(SecurityUtils.mergeSkipPatterns("")));

    }

    /**
     * ?????????????????? {@code @MatrixVariable}??????
     *
     * @param configurer configurer
     * @since 1.0.0
     */
    @Override
    public void configurePathMatch(@NotNull PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    /**
     * Registrations
     *
     * @return the web mvc registrations
     * @since 2.0.0
     */
    @Bean
    @ConditionalOnMissingBean(WebMvcRegistrations.class)
    public WebMvcRegistrations registrations() {
        return new WebMvcRegistrations() {
            /**
             * ?????? RequestMappingHandlerMapping,
             * ????????? ?????? WebMvcConfigurationSupport, ??????????????????????????????????????????????????????????????????
             * ????????????????????????????????????RequestMappingHandlerAdapter???????????????????????????ExceptionHandlerExceptionResolver??????
             *
             * @return the request mapping handler mapping
             * @since 2.0.0
             */
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new ApiVersionRequestMappingHandlerMapping();
            }
        };
    }


    /**
     * Build config cors configuration
     *
     * @return the cors configuration
     * @since 1.0.0
     */
    private @NotNull CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1. ????????????????????????
        corsConfiguration.addAllowedOrigin(StringPool.ASTERISK);
        // 2. ???????????????
        corsConfiguration.addAllowedHeader(StringPool.ASTERISK);
        // 3. ?????????????????? (post???get???)
        corsConfiguration.addAllowedMethod(StringPool.ASTERISK);
        corsConfiguration.setMaxAge(MAX_AGE);
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }

}
