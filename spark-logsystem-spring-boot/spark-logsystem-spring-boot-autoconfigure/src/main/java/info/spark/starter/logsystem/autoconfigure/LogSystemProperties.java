package info.spark.starter.logsystem.autoconfigure;

import info.spark.starter.logsystem.Constants;
import info.spark.starter.logsystem.constant.LogSystem;
import info.spark.starter.logsystem.entity.LogFile;
import info.spark.starter.logsystem.entity.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.logging.LogLevel;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * <p>Description: 日志配置, 此类并没有被使用到, 这里只是为了生成配置的元数据, 方便在配置时进行提示 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:47
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = LogSystemProperties.PREFIX)
public class LogSystemProperties {

    /** PREFIX */
    public static final String PREFIX = "spark.logging";

    /**
     * 使用此配置来区分不同应用的日志目录, 默认为应用名, 此配置只对 log4j2-flie.xml 有效.
     * 最终保存的日志位置为: ${path}/${appName}/${name.log}
     */
    private String appName;

    /**
     * 指定日志配置文件, 本地开发环境为 log4j2-console.xml, 日志只会输出到控制台, 非本地开发环境为 log4j2-file.xml, 日志只会输出到文件,
     * 如果使用 log4j2-file.xml 配置, 默认会将日志输出到 {@link LogSystem#DEFAULT_LOGGING_LOCATION},
     * 默认的日志文件名为 {@link Constants#DEFAULT_FILE_NAME}, 分别使用 spark.logging.file.path 和 spark.logging.file.name 修改.
     * 使用 IDE 启动的应用(包括单元测试)都为本地开发环境, 如果需要切换日志配置, 可通过 spark.logging.config = log-config-file-name.xml,
     * 如果默认的日志配置无法满足业务, 也可通过上述配置进行覆盖.
     */
    private String config;

    /**
     * 是否输出 location (此配置只对 log4j2-console.xml 有效, 因为会消耗性能. 用于快速跳转到日志输出语句, 本地开发时能更友好的进行调试).
     *
     * @since 1.0.0
     */
    private boolean enableShowLocation;

    /** File */
    @NestedConfigurationProperty
    private LogFile file = new LogFile();
    /** Pattern */
    @NestedConfigurationProperty
    private Pattern pattern = new Pattern();

    /**
     * formatter:off
     * 日志等级分组.
     * {@code
     * spark:
     *  logging:
     *    group:
     *      test: sample.logsystem, sample.launcher
     *    level:
     *      test: info
     *      starter: info
     *      rest: info
     * }
     */
    private Map<String, List<String>> group;

    /**
     * 日志等级配置.
     * {@code
     * spark:
     *   logging:
     *     level:
     *       sample.logsystem: info
     * }
     */
    private Map<String, LogLevel> level;
}
