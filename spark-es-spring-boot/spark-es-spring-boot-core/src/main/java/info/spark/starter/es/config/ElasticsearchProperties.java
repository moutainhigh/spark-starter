package info.spark.starter.es.config;

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
@ConfigurationProperties(prefix = ElasticsearchProperties.PREFIX, ignoreInvalidFields = true)
public class ElasticsearchProperties {

    /** PREFIX */
    public static final String PREFIX = "spark.es";

    /** Mapper path */
    private String mapperPath = "es/mapper/";
    /** Host */
    private String host = "es.server:9200";
    /** Show log */
    private Boolean showLog = false;
    /** Date format */
    private String dateFormat = "yyyy.MM";
    /** es 如果启用了x-pack或者 searchGuard 安全认证 */
    private String user;
    /** es 如果启用了x-pack或者 searchGuard 安全认证 */
    private String password;
}
