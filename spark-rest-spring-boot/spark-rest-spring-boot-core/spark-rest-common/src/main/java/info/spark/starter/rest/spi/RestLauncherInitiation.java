package info.spark.starter.rest.spi;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.util.FileUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.logsystem.constant.LogSystem;
import info.spark.starter.processor.annotation.AutoService;
import java.io.File;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>Description: rest 加载默认配置 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:17
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class RestLauncherInitiation implements LauncherInitiation {
    // formatter:off
    /** NOT_LOCALLAUNCHER_LOG_DEFAULT_PATTERN @see io.undertow.server.handlers.accesslog.AccessLogHandler */
    @SuppressWarnings("checkstyle:LineLength")
    private static final String LOG_DEFAULT_PATTERN = "[%{time,yyyy-MM-dd HH:mm:ss.SSS}] \"%r\" %s (%D ms) (%b bytes) %{i,X-Trace-Id} %{i,X-Agent-Api}_%{i,X-Agent-Version},%{i,User-Agent} %l %u %v";
    // formatter:on

    /**
     * Launcher *
     * info.spark.starter.launcher.env.RangeRandomValuePropertySource
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("checkstyle:Regexp")
    public Map<String, Object> launcher(ConfigurableEnvironment env,
                                        String appName,
                                        boolean isLocalLaunch) {
        String undertowLogDir = FileUtils.toTempDirPath(ConfigDefaultValue.DEFAULE_ACCESS_LOG_DIR);
        if (!isLocalLaunch) {
            String logPath = System.getProperty(ConfigKey.LogSystemConfigKey.LOG_FILE_PATH, LogSystem.DEFAULT_LOGGING_LOCATION);
            undertowLogDir = FileUtils.appendPath(logPath, appName, ConfigDefaultValue.DEFAULE_ACCESS_LOG_DIR);
        }
        System.out.println("access log: " + undertowLogDir + File.separator + "access.log");

        // 如果存在 START_SPARK_APPLICATION 环境变量, 则表示使用了 spark-launcher 依赖
        Object port = 8080;
        if (StringUtils.isNotBlank(System.getProperty(App.START_SPARK_APPLICATION))) {
            port = "${range.random.int(18000, 18200)}";
        }

        return ChainMap.build(16)
            .put(ConfigKey.UndertowConfigKye.ENABLE_ACCESSLOG, ConfigDefaultValue.TRUE)
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_DIR, undertowLogDir)
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_PATTERN, LOG_DEFAULT_PATTERN)
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_PREFIX, "access")
            .put(ConfigKey.UndertowConfigKye.ACCESSLOG_SUFFIX, ".log")

            .put(ConfigKey.MvcConfigKey.NO_HANDLER_FOUND, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MvcConfigKey.ENCODING_ENABLED, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MvcConfigKey.ENCODING_FORCE, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MvcConfigKey.ENCODING_CHARSET, StringPool.UTF_8)
            .put(ConfigKey.SpringConfigKey.SERVER_PORT, port)
            // 序列化时只包含不为空的字段
            .put(ConfigKey.SpringConfigKey.JACKSON_DEFAULT_PROPERTY_INCLUSION, ConfigDefaultValue.DEFAULT_PROPERTY_INCLUSION_VALUE);
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
        return "spark-starter-rest";
    }
}
