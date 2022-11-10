package info.spark.feign.adapter.config;

import info.spark.feign.adapter.interceptor.RequestHeaderInterceptor;
import info.spark.feign.adapter.interceptor.RequestParamterInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 将本模块的 bean 注入到 IoC, 为了兼容非 Spring Boot 项目, 这里使用 @ComponentScan 注解, 将 FeignClientBuilder package 下的所有 bean 注入到 IoC </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 18:20
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan("info.spark.feign.adapter")
public class FeignClientAdapterConfiguration {

    /**
     * Instantiates a new Feign auto configuration.
     *
     * @since 1.0.0
     */
    public FeignClientAdapterConfiguration() {
        log.info("加载 Feign Client Adapter 注入自动装配器: {}", FeignClientAdapterConfiguration.class);
    }

    /**
     * Request header interceptor
     *
     * @return the request header interceptor
     * @since 1.5.0
     */
    @Bean
    public RequestHeaderInterceptor requestHeaderInterceptor() {
        return new RequestHeaderInterceptor();
    }

    /**
     * Request paramter interceptor
     *
     * @return the request paramter interceptor
     * @since 1.5.0
     */
    @Bean
    public RequestParamterInterceptor requestParamterInterceptor() {
        return new RequestParamterInterceptor();
    }
}
