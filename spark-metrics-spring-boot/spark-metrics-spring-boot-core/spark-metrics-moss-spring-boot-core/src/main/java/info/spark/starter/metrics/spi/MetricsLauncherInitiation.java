package info.spark.starter.metrics.spi;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.util.FileUtils;
import info.spark.starter.processor.annotation.AutoService;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.xujin.moss.client.config.ManagementEnvironmentCustomizer;


/**
 * <p>Description: </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.11.04 15:20
 * @since 2.0.0
 */
@AutoService(LauncherInitiation.class)
public class MetricsLauncherInitiation implements LauncherInitiation {

    /** SYSTEM_LOGS_PATH */
    private static final String SYSTEM_LOGS_PATH = ConfigDefaultValue.DEFAULT_LOGGING_LOCATION + StringPool.SLASH;
    /** ALL_LOG */
    public static final String ALL_LOG = "all.log";
    /** DEBUG_LOG */
    public static final String DEBUG_LOG = "debug.log";
    /** ERROR_LOG */
    public static final String ERROR_LOG = "error.log";
    /** TRACE_LOG */
    public static final String TRACE_LOG = "trace.log";
    /** CONTAINER_LOG */
    public static final String CONTAINER_LOG = "access.log";
    /** GC_LOG */
    public static final String GC_LOG = "gc.log";
    /** AGENT_LOG */
    public static final String AGENT_LOG = "agent.service.log";

    /**
     * Launcher map
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 2.0.0
     */
    @Override
    public Map<String, Object> launcher(ConfigurableEnvironment env, String appName, boolean isLocalLaunch) {
        String allLogPath, debugLogPath, errorLogPath, gcLogPath, traceLogPath, containerLogPath, agentLogPath;

        String basePath = !isLocalLaunch
            ? System.getProperty(ConfigKey.LogSystemConfigKey.LOG_FILE_PATH,
            SYSTEM_LOGS_PATH + ConfigKit.getEnv().getName())
            : FileUtils.getTempDirPath();

        allLogPath = FileUtils.appendPath(basePath, appName, ALL_LOG);
        debugLogPath = FileUtils.appendPath(basePath, appName, DEBUG_LOG);
        errorLogPath = FileUtils.appendPath(basePath, appName, ERROR_LOG);
        gcLogPath = FileUtils.appendPath(basePath, appName, GC_LOG);
        traceLogPath = FileUtils.appendPath(basePath, appName, TRACE_LOG);
        agentLogPath = FileUtils.appendPath(basePath, appName, AGENT_LOG);
        containerLogPath = FileUtils.appendPath(basePath, appName, ConfigDefaultValue.DEFAULE_ACCESS_LOG_DIR, CONTAINER_LOG);

        ManagementEnvironmentCustomizer managementEnvironmentCustomizer = new ManagementEnvironmentCustomizer();
        return ChainMap.build(16)
            .put("info.artifactId", env.getProperty("project.artifactId", appName))
            .put("info.groupId", env.getProperty("project.groupId", "info.spark"))
            .put("info.version", env.getProperty("project.version", ConfigKit.getAppVersion()))

            .put("logging.registry.files[0].name", ALL_LOG)
            .put("logging.registry.files[0].description", "全部日志")
            .put("logging.registry.files[0].path", allLogPath)

            .put("logging.registry.files[1].name", GC_LOG)
            .put("logging.registry.files[1].description", "GC 日志")
            .put("logging.registry.files[1].path", gcLogPath)

            .put("logging.registry.files[2].name", DEBUG_LOG)
            .put("logging.registry.files[2].description", "DEBUG 日志")
            .put("logging.registry.files[2].path", debugLogPath)

            .put("logging.registry.files[3].name", ERROR_LOG)
            .put("logging.registry.files[3].description", "ERROR 日志")
            .put("logging.registry.files[3].path", errorLogPath)

            .put("logging.registry.files[4].name", TRACE_LOG)
            .put("logging.registry.files[4].description", "TRACE 日志")
            .put("logging.registry.files[4].path", traceLogPath)

            .put("logging.registry.files[5].name", CONTAINER_LOG)
            .put("logging.registry.files[5].description", "容器日志")
            .put("logging.registry.files[5].path", containerLogPath)

            .put("logging.registry.files[6].name", AGENT_LOG)
            .put("logging.registry.files[6].description", "Agent 日志")
            .put("logging.registry.files[6].path", agentLogPath)

            // todo-dong4j 添加安全认证
            .put(ConfigKey.ManagementConfigKey.EXPOSURE_INCLUDE, "*");
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 2.0.0
     */
    @Override
    public String getName() {
        return "spark-starter-metrics-moss";
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 2.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
