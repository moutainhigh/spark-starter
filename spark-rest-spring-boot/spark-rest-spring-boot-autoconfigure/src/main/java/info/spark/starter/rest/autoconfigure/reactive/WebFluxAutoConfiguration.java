package info.spark.starter.rest.autoconfigure.reactive;

import info.spark.starter.common.start.SparkAutoConfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:47
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@RestController
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFluxConfigurer.class)
public class WebFluxAutoConfiguration implements SparkAutoConfiguration {

    /** Application context */
    @Resource
    private ReactiveWebApplicationContext applicationContext;

}
