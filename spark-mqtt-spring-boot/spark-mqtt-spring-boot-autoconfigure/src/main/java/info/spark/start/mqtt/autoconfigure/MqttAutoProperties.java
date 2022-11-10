package info.spark.start.mqtt.autoconfigure;

import info.spark.start.mqtt.core.properties.MqttProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 10:32
 * @since 2.1.0
 */
@ConfigurationProperties(prefix = MqttAutoProperties.PREFIX)
@Data
public class MqttAutoProperties {
    /** PREFIX */
    static final String PREFIX = "spark.mqtt";
    /** 默认开启 */
    private boolean enable = true;

    /** Clients */
    private List<MqttProperties.MqttConnectorProperties> clients;

}
