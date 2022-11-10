package info.spark.starter.idempotent.autoconfigure;

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
@ConfigurationProperties(prefix = IdempotentProperties.PREFIX, ignoreInvalidFields = true)
public class IdempotentProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.idempotent";

    /** 过期时间 单位秒 */
    private Long expire = 7200L;

}
