package info.spark.starter.security.autoconfigure;

import info.spark.starter.basic.util.StringPool;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:55
 * @since 1.0.0
 */
@Slf4j
@Data
@RefreshScope
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = SecurityProperties.PREFIX)
public class SecurityProperties {
    /** Prefix */
    public static final String PREFIX = "spark.security";
    /** DEFAULT_DATA_ID */
    public static final String DEFAULT_DATA_ID = "security-ignore-url.yml";
    /** DEFAULT_GROUP */
    public static final String DEFAULT_GROUP = "SECURITY";
    /** Signing key */
    static final String SIGNING_KEY = PREFIX + StringPool.DOT + "signing-key";

    /** JWT 对称加密密钥, 用于签名验证, 解密 */
    private String signingKey;
    /** Data id */
    private String dataId = DEFAULT_DATA_ID;
    /** Group */
    private String group = DEFAULT_GROUP;
    /** [注意: 此配置项由 security-ignore-url.yml 单独配置] key = 服务名, value = 被忽略的 url */
    private Map<String, Set<String>> ignoreUrl;
    /** 是否使用本地配置 (只会在本地开发环境生效) */
    private boolean enableLocalIgnoreUrl = false;

}
