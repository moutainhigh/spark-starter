package info.spark.starter.dubbo.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.18 23:44
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = DubboProperties.PREFIX)
public class DubboProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.dubbo";
}
