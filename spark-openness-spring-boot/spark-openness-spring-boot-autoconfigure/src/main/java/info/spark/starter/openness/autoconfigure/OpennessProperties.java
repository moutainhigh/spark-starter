package info.spark.starter.openness.autoconfigure;

import info.spark.starter.basic.AbstractSkipFilter;

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
@ConfigurationProperties(prefix = OpennessProperties.PREFIX)
public class OpennessProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.openness";

    /** 相邻请求的时间间隔: 默认为60 * 1000 毫秒 */
    private Long timeInterval = 60 * 1000L;
    /** nonce字符串长度: 默认为6 */
    private Integer nonceLength = 6;
    /** 需要被过滤器拦截的url pattern，默认 null 则为 /** {@link AbstractSkipFilter} */
    private String[] includePatterns;
    /** 不需要被过滤器拦截的url pattern 默认 null 则为 {@link AbstractSkipFilter} */
    private String[] excludePatterns;
    /** 是否启用 */
    private boolean enabled = true;

}
