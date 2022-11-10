package info.spark.starter.websocket.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.websocket.ServerEndpointContext;
import info.spark.starter.websocket.annotation.EnableWebSocket;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:21
 * @since 2022.1.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EnableWebSocket.class)
@EnableConfigurationProperties(NettyWebSocketProperties.class)
public class NettyWebSocketAutoConfigure implements SparkAutoConfiguration {

    /**
     * Server endpoint context
     *
     * @return the server endpoint context
     * @since 2022.1.1
     */
    @Bean
    public ServerEndpointContext serverEndpointContext() {
        return new ServerEndpointContext();
    }
}
