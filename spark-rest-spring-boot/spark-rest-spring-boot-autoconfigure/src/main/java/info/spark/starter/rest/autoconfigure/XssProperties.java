package info.spark.starter.rest.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * <p>Description: Xss配置类</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:43
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(XssProperties.PREFIX)
public class XssProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.xss";

    /** xss 处理器 */
    private boolean enableXssFilter = Boolean.TRUE;
    /** 设置忽略的 url */
    private List<String> excludePatterns = new ArrayList<>();

}
