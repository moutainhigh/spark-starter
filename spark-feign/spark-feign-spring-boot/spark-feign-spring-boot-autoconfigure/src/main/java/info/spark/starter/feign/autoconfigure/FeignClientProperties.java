package info.spark.starter.feign.autoconfigure;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:26
 * @since 1.0.0
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = FeignClientProperties.PREFIX)
public class FeignClientProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.feign.client";

    /** 开发直连和联调地址, 为空则直连本机 */
    private String devAddr;
}
