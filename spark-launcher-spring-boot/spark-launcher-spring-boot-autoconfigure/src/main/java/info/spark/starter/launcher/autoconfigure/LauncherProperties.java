package info.spark.starter.launcher.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

import java.util.Map;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:54
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = LauncherProperties.PREFIX)
public class LauncherProperties {

    /** PREFIX */
    public static final String PREFIX = "spark.app";
    /** 装载自定义配置 spark.app.custom.xxx */
    private Map<String, Object> custom;
    /** App */
    @Deprecated
    private Map<String, Object> app;
    /** 是否关闭 banner */
    private boolean enableBanner = Boolean.FALSE;
    /** 应用分组 */
    private String group;
    /** Config group */
    private String configGroup;
    /** Discovery group */
    private String discoveryGroup;

    /**
     * Sets app *
     *
     * @param app app
     * @since 1.0.0
     */
    @DeprecatedConfigurationProperty(replacement = "spark.app.custom", reason = "添加 app 配置层级, 与其他项目区分")
    public void setApp(Map<String, Object> app) {
        this.app = app;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.19 16:14
     * @since 1.0.0
     */
    public enum Type {
        /** Config type */
        config,
        /** Discovery type */
        discovery
    }
}
