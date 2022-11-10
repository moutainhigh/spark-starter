package info.spark.starter.mq.autoconfigure.common;

import info.spark.starter.mq.RoleType;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 18:51
 * @since 1.5.0
 */
@Data
@ConfigurationProperties(prefix = MessageProperties.PREFIX)
public class MessageProperties {

    /** PREFIX */
    public static final String PREFIX = "spark.mq";
    /** Role */
    private RoleType role;
}
