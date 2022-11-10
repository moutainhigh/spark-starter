package info.spark.starter.endpoint.spi;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.enums.SparkEnv;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.endpoint.constant.Endpoint;
import info.spark.starter.processor.annotation.AutoService;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * <p>Description: 通过 SPI 加载 endpoint 默认配置</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:12
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class EndpointLauncherInitiation implements LauncherInitiation {

    /**
     * Launcher chain map
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the chain map
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> launcher(ConfigurableEnvironment env,
                                        String appName,
                                        boolean isLocalLaunch) {
        ChainMap map = ChainMap.build(4)
            .put(ConfigKey.ManagementConfigKey.ENABLED, ConfigDefaultValue.TRUE)
            // 输出更多的 health 信息
            .put(ConfigKey.ManagementConfigKey.HEALTH_DETAILS, "always")
            .put(ConfigKey.ManagementConfigKey.BASE_URL, "/actuator")
            // 输出更多的 git 信息
            .put(ConfigKey.ManagementConfigKey.GIT_MODE, "full");

        if (!SparkEnv.PROD.getName().equals(ConfigKit.getProfile(env))) {
            map.put(ConfigKey.ManagementConfigKey.EXPOSURE_INCLUDE, "*");
        }

        return map;
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return Endpoint.MODULE_NAME;
    }
}
