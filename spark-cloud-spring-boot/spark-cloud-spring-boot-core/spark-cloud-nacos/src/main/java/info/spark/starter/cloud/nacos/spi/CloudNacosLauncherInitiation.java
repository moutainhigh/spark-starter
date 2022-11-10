package info.spark.starter.cloud.nacos.spi;

import info.spark.starter.autoconfigure.util.SkipAutoCinfiguration;
import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.util.DateUtils;
import info.spark.starter.util.ObjectUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.processor.annotation.AutoService;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: cloud 加载默认配置 </p>
 * todo-dong4j : (2019年10月22日 8:47 下午) [使用 {@link org.springframework.boot.env.YamlPropertySourceLoader} 重构]
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 02:23
 * @since 1.0.0
 */
@Slf4j
@AutoService(LauncherInitiation.class)
public class CloudNacosLauncherInitiation implements LauncherInitiation {

    /** GROUP */
    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    /** NACOS_SERVICE_REGISTRY_AUTOCONFIGURATION */
    private static final String NACOS_SERVICE_REGISTRY_AUTOCONFIGURATION
        = "com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration";

    /**
     * Launcher *
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> launcher(@NotNull ConfigurableEnvironment env,
                                        String appName,
                                        boolean isLocalLaunch) {
        PropertySource<?> propertySource = ConfigKit.getPropertySource(ConfigKit.CLOUD_CONFIG_FILE_NAME);

        ChainMap chainMap = ChainMap.build(8)
            .put(ConfigKey.CloudConfigKey.CONFIG_FILE_EXTENSION, ConfigKit.YAML_FILE_EXTENSION)
            .put(ConfigKey.CloudConfigKey.CONFIG_ENCODE, StringPool.UTF_8)
            .put(ConfigKey.CloudConfigKey.CONFIG_NAMESPACE, App.SPARK_NAME_SPACE)
            .put(ConfigKey.CloudConfigKey.CONFIG_SERVER_ADDRESS, ConfigDefaultValue.NACOS_SERVER)
            .put(ConfigKey.CloudConfigKey.CONFIG_SERVER_ADDRESS, ConfigDefaultValue.NACOS_SERVER)
            .put(ConfigKey.CloudConfigKey.DISCOVERY_SERVER_ADDRESS, ConfigDefaultValue.NACOS_SERVER)
            .put(ConfigKey.CloudConfigKey.DISCOVERY_NAMESPACE, App.SPARK_NAME_SPACE)
            // 元数据(应用启动时间)
            .put(ConfigKey.CloudConfigKey.DISCOVERY_METADATA + ".started.time", DateUtils.formatDateTime(new Date()));

        this.processGroup(propertySource, chainMap);
        return chainMap;
    }

    /**
     * Advance
     *
     * @param appName app name
     * @since 2022.1.1
     */
    @Override
    public void advance(String appName) {
        SkipAutoCinfiguration.skip(CloudNacosLauncherInitiation.class.getName(),
                                   NACOS_SERVICE_REGISTRY_AUTOCONFIGURATION);
    }

    /**
     * 处理 group
     * 1. 优先使用 spring.cloud.nacos.config.group 和 spring.cloud.nacos.discovery.group;
     * 2. 相同的 discovery.group 下的服务才能通信, 如果未设置则默认为 DEFAULT_GROUP;
     * 3. 如果只配置了 spark.app.group, 则 config 和 discovery 都设置为此配置;
     * 4. 如果配置了 spark.app.config-group, 则将此配置设置为 spring.cloud.nacos.config.group;
     * 5. 如果配置了 spark.app.discovery-group, 则将此配置设置为 spring.cloud.nacos.discovery.group;
     *
     * @param propertySource property source
     * @param chainMap       chain map
     * @since 1.0.0
     */
    public void processGroup(@NotNull PropertySource<?> propertySource, ChainMap chainMap) {
        Object configGroup = propertySource.getProperty(ConfigKey.CloudConfigKey.CONFIG_GROUP);
        if (this.notBlank(configGroup)) {
            chainMap.put(ConfigKey.CloudConfigKey.CONFIG_GROUP, String.valueOf(configGroup));
            log.warn("可使用 [spark.app.config-group] 代替 [spring.cloud.nacos.config.group] 配置.");
        }

        Object discoveryGroup = propertySource.getProperty(ConfigKey.CloudConfigKey.DISCOVERY_GROUP);
        if (this.notBlank(discoveryGroup)) {
            chainMap.put(ConfigKey.CloudConfigKey.DISCOVERY_GROUP, String.valueOf(discoveryGroup));
            log.warn("可使用 [spark.app.discovery-group] 代替 [spring.cloud.nacos.discovery.group] 配置.");
        } else {
            chainMap.put(ConfigKey.CloudConfigKey.DISCOVERY_GROUP, DEFAULT_GROUP);
        }

        Object customGroup = propertySource.getProperty(ConfigKey.SPARK_APP_GROUP);
        if (this.notBlank(customGroup)) {
            chainMap.put(ConfigKey.CloudConfigKey.CONFIG_GROUP, String.valueOf(customGroup));
            chainMap.put(ConfigKey.CloudConfigKey.DISCOVERY_GROUP, String.valueOf(customGroup));
        } else {
            Object customConfigGroup = propertySource.getProperty(ConfigKey.SPARK_APP_CONFIG_GROUP);
            if (this.notBlank(customConfigGroup)) {
                chainMap.put(ConfigKey.CloudConfigKey.CONFIG_GROUP, String.valueOf(customConfigGroup));
            }

            Object customDiscoveryGroup = propertySource.getProperty(ConfigKey.SPARK_APP_DISCOVERY_GROUP);
            if (this.notBlank(customDiscoveryGroup)) {
                chainMap.put(ConfigKey.CloudConfigKey.DISCOVERY_GROUP, String.valueOf(customDiscoveryGroup));
            }
        }
    }

    /**
     * Not blank
     *
     * @param object object
     * @return the boolean
     * @since 1.0.0
     */
    private boolean notBlank(Object object) {
        return ObjectUtils.isNotNull(object)
               && StringUtils.isNotBlank(String.valueOf(object));
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 102;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return "spark-starter-cloud-nacos";
    }
}
