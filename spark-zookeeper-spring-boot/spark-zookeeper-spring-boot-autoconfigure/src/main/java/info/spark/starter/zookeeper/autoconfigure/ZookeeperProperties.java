package info.spark.starter.zookeeper.autoconfigure;

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
 * @since 1.8.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = ZookeeperProperties.PREFIX, ignoreInvalidFields = true)
public class ZookeeperProperties {

    /** Prefix */
    static final String PREFIX = "spark.zookeeper";

    /** Enabled */
    private boolean enabled;

    /** zookeeper 节点列表 首先加载此配置 */
    private String connectString;
    /** 与connectString 二选一 */
    private String ensembleProviderRef;
    /** 权限控制 */
    private String aclProvider;
    /** 权限认证 */
    private String scheme;
    /** 权限验证字符串 */
    private String authBase64Str;
    /** Auth infos ref */
    private String authInfosRef;
    /** Can be read only */
    private Boolean canBeReadOnly;
    /** Use container parents if available */
    private Boolean useContainerParentsIfAvailable;
    /** Compression provider ref */
    private String compressionProviderRef;
    /** 默认数据, 用于 debug */
    private String defaultDataBase64Str;
    /** 命名空间设置后, 所有操作将在此节点下 不能加 / */
    private String namespace;
    /** session 超时时间 默认 60s */
    private Integer sessionTimeOutMs;
    /** 连接操作时间 默认 15s */
    private Integer connectionTimeoutMs;
    /** 关闭后的最大等待时间 默认 1s */
    private Integer maxCloseWaitMs;
    /** Thread factory ref */
    private String threadFactoryRef;
    /** Zookeeper factory ref */
    private String zookeeperFactoryRef;
    /** 初试时间为1s */
    private int baseSleepTimeMs = 1000;
    /** 重试5次 */
    private int maxRetries = 5;
}
