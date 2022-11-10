package info.spark.starter.dingtalk.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.05 00:27
 * @since 2.1.0
 */
@Data
@ConfigurationProperties(prefix = DingtalkProperties.PREFIX)
public class DingtalkProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.notify.dingtalk";
    /** Webhook */
    private String webhook;
    /** Enabled */
    private boolean enabled = true;
}
