package info.spark.starter.logsystem.spi;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.logsystem.constant.LogSystem;
import info.spark.starter.util.core.support.ChainMap;

import info.spark.starter.processor.annotation.AutoService;

import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * <p>Description: 日志系统默认配置 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.27 12:23
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class LogSystemRecordInitiation implements LauncherInitiation {

    /**
     * 日志系统配置需要在配置文件被读取后才能设置, 默认配置已通过 SparkLoggingListener 进行设置, 此处不再配置.
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> launcher(ConfigurableEnvironment env, String appName, boolean isLocalLaunch) {
        return ChainMap.build(0)
            .put(ConfigKey.SpringConfigKey.MAIN_ALLOW_BEAN_DEFINITION_OVERRIDING, ConfigDefaultValue.TRUE);
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return LogSystem.MODULE_NAME;
    }
}
