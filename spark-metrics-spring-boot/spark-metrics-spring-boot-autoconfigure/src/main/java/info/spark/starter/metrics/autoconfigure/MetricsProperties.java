package info.spark.starter.metrics.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.04 22:34
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = MetricsProperties.PREFIX, ignoreInvalidFields = true)
public class MetricsProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.metrics";

}
