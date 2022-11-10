package info.spark.starter.websocket.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:21
 * @since 2022.1.1
 */
@Data
@ConfigurationProperties(prefix = NettyWebSocketProperties.PREFIX)
public class NettyWebSocketProperties {
    /** Prefix */
    static final String PREFIX = "spark.websocket";

    /** Port */
    private int port;
}
