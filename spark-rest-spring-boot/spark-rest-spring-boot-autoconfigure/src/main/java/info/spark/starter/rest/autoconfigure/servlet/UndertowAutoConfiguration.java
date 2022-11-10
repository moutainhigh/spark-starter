package info.spark.starter.rest.autoconfigure.servlet;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.undertow.ShowUndertowLog;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RequestDumpingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:47
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RestProperties.class)
@ConditionalOnClass(Undertow.class)
public class UndertowAutoConfiguration implements SparkAutoConfiguration, WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    /** Rest properties */
    private final RestProperties properties;

    /**
     * Undertow configuration
     *
     * @param properties properties
     * @since 1.0.0
     */
    @Contract(pure = true)
    public UndertowAutoConfiguration(RestProperties properties) {
        this.properties = properties;
    }

    /**
     * 容器的临时目录
     *
     * @return the multipart config element
     * @since 1.8.0
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(this.properties.getMultipart().getLocation());
        return factory.createMultipartConfig();
    }

    /**
     * 处理 UT026010: Buffer pool was not set on WebSocketDeploymentInfo, the default pool will be used 警告
     * 开启 undertow 的请求日志功能, 开启 HTTP2 支持
     * https://frandorado.github.io/spring/2018/11/04/log-request-response-with-undertow-spring.html
     * https://undertow.io/undertow-docs/undertow-docs-2.1.0/index.html
     *
     * @param factory the factory
     * @see AccessLogHandler
     * @see RequestDumpingHandler
     * @since 1.0.0
     */
    @Override
    public void customize(@NotNull UndertowServletWebServerFactory factory) {
        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, 1024));
            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
        });

        // 开启 http2 支持
        if (this.properties.isEnableHttp2()) {
            factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
        }

        // 开启 http 请求日志
        factory.addDeploymentInfoCustomizers(
            info -> info.addInitialHandlerChainWrapper(
                next -> new CustomRequestDumpingHandler(next, this.properties)));

        // %D 需要开启 undertow 记时 (access.log)
        factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true));
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.04.25 19:59
     * @since 1.7.3
     */
    static class CustomRequestDumpingHandler implements HttpHandler {
        /** Properties */
        private final RestProperties properties;
        /** Next */
        private final HttpHandler next;

        /**
         * Custom request dumping handler
         *
         * @param next       next
         * @param properties properties
         * @since 1.7.3
         */
        CustomRequestDumpingHandler(HttpHandler next, RestProperties properties) {
            this.next = next;
            this.properties = properties;
        }

        /**
         * 每次判断是否需要输入日志, 实现动态开关
         * 1, 日志等级必须设置为 project: trace
         * 2. 必须配置 enable-container-log: true
         *
         * @param exchange exchange
         * @throws Exception exception
         * @since 1.7.3
         */
        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            ShowUndertowLog.showLog(exchange, this.properties.isEnableContainerLog());
            // Perform the exchange
            this.next.handleRequest(exchange);
        }
    }

}
