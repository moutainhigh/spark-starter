package info.spark.starter.logsystem.handler;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.logsystem.AbstractPropertiesProcessor;
import info.spark.starter.logsystem.Constants;
import info.spark.starter.logsystem.entity.Pattern;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>Description:  spark.logging.pattern 配置处理</p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.20 10:47
 * @since 1.4.0
 */
public class PatternProcessor extends AbstractPropertiesProcessor {
    /**
     * Pattern processor
     *
     * @param environment environment
     * @since 1.4.0
     */
    public PatternProcessor(ConfigurableEnvironment environment) {
        super(environment);
    }

    /**
     * Apply
     *
     * @since 1.4.0
     */
    @Override
    public void apply() {
        // 提前将配置绑定到配置类
        Binder binder = Binder.get(this.environment);
        Pattern pattern = binder.bind(ConfigKey.LogSystemConfigKey.LOG_PATTERN, Pattern.class).orElse(new Pattern());

        // spark.logging.pattern.console
        this.setSystemProperty(pattern.getConsole(), Constants.CONSOLE_LOG_PATTERN,
                               ConfigKey.LogSystemConfigKey.LOG_PATTERN_CONSOLE);
        // spark.logging.pattern.file
        this.setSystemProperty(pattern.getFile(), Constants.FILE_LOG_PATTERN, ConfigKey.LogSystemConfigKey.LOG_PATTERN_FILE);
        // spark.logging.pattern.level
        this.setSystemProperty(pattern.getLevel(), Constants.LOG_LEVEL_PATTERN, ConfigKey.LogSystemConfigKey.LOG_PATTERN_LEVEL);
        // spark.logging.pattern.dateformat
        this.setSystemProperty(pattern.getDateformat(), Constants.LOG_DATEFORMAT_PATTERN,
                               ConfigKey.LogSystemConfigKey.LOG_PATTERN_DATEFORMAT);
        // spark.logging.pattern.rolling-file-name
        this.setSystemProperty(pattern.getRollingFileName(), Constants.ROLLING_FILE_NAME_PATTERN,
                               ConfigKey.LogSystemConfigKey.ROLLING_FILE_NAME);
        //  spark.logging.pattern.marker
        this.setSystemProperty(pattern.getMarker(), Constants.MARKER_PATTERN, ConfigKey.LogSystemConfigKey.MARKER_PATTERN);

    }
}
