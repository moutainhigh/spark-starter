package info.spark.starter.endpoint.autoconfigure.reactive;

import info.spark.starter.basic.Result;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.enums.LibraryEnum;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.endpoint.initialization.InitializationService;
import info.spark.starter.endpoint.reactive.ReactiveInitializationService;
import info.spark.starter.util.core.api.R;
import info.spark.starter.core.util.NetUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 11:20
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(WebFluxConfigurer.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveStartInfoAutoConfiguration implements SparkAutoConfiguration {

    /**
     * 如果在 classpath 下存在 git.properties, 则重定向到 /git
     *
     * @return the router function
     * @since 1.0.0
     */
    @NotNull
    @Bean
    static RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route(
            RequestPredicates.GET(LibraryEnum.START_URL),
            request -> ServerResponse.temporaryRedirect(URI.create("http://"
                                                                   + NetUtils.getLocalHost()
                                                                   + ":"
                                                                   + ConfigKit.getPort()
                                                                   + ConfigKit.getContextPath()
                                                                   + "/git")).build());
    }

    /**
     * Git result.
     *
     * @return the result
     * @since 1.0.0
     */
    @ReadOperation
    public Result<? extends Serializable> git() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(App.GIT_CONFIG_FILE_NAME);
        if (inputStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
            } catch (IOException ignored) {
            }
            return R.succeed(properties);
        }
        return R.succeed("up");
    }

    /**
     * Reactive initialization service
     *
     * @param webClientBuilder web client builder
     * @return the initialization service
     * @since 2022.1.1
     */
    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public InitializationService reactiveInitializationService(WebClient.Builder webClientBuilder) {
        return new ReactiveInitializationService(webClientBuilder);
    }
}
