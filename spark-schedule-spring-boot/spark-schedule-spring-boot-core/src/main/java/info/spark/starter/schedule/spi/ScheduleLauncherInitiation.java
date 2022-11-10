package info.spark.starter.schedule.spi;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.util.FileUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.processor.annotation.AutoService;

import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * <p>Description: Schedule 加载默认配置 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:17
 * @since 1.0.0
 */
@SuppressWarnings("all")
@AutoService(LauncherInitiation.class)
public class ScheduleLauncherInitiation implements LauncherInitiation {
    /** DEFAULT_LOG_PATH */
    public static String DEFAULT_LOG_PATH = FileUtils.getTempDirPath() + "schedule";

    /**
     * Launcher *
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @see info.spark.starter.launcher.env.RangeRandomValuePropertySource
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> launcher(ConfigurableEnvironment env,
                                        String appName,
                                        boolean isLocalLaunch) {

        if (!isLocalLaunch) {
            DEFAULT_LOG_PATH = ConfigDefaultValue.DEFAULT_LOGGING_LOCATION + "/schedule";
        }

        return ChainMap.build(4)
            .put(ConfigKey.ScheduleConfigKey.EXECUTOR_APP_NAME,
                 StringUtils.format("${{}}", ConfigKey.SpringConfigKey.APPLICATION_NAME))
            .put(ConfigKey.ScheduleConfigKey.EXECUTOR_APP_LOG_PATH, DEFAULT_LOG_PATH);
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 101;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return "spark-starter-schedule";
    }
}
