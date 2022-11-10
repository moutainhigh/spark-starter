package info.spark.starter.mybatis.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;


/**
 * <p>Description: p6spy 默认配置 </p>
 *
 * @author zhubo
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.15 10:03
 * @since 1.7.0
 */
@Data
@ConfigurationProperties(prefix = P6spyProperties.P6SPY_CONFIG_PREFIX)
public class P6spyProperties {

    /** P6SPY_CONFIG_PREFIX */
    public static final String P6SPY_CONFIG_PREFIX = "spring.datasource.p6spy";
    /** Enabled */
    private boolean enabled = false;
    /** Modulelist */
    private String modulelist = "com.baomidou.mybatisplus.extension.p6spy.MybatisPlusLogFactory,com.p6spy.engine.outage.P6OutageFactory";
    /** Log message format */
    private String logMessageFormat = "com.baomidou.mybatisplus.extension.p6spy.P6SpyLogger";
    /** Appender */
    private String appender = "logger.info.spark.starter.mybatis.SparkP6spySlf4jLogger";
    /** Excludecategories */
    private String excludecategories = "info, warn, error, result, batc, resultset, statement";
    /** 设置 p6spy driver 代理 */
    private String deregisterdrivers = "true";
    /** Autoflush */
    private String autoflush = "false";
    /** Driverlist */
    private String driverlist = "com.mysql.cj.jdbc.Driver";
    /** Dateformat */
    private String dateformat = "yyyy-MM-dd HH:mm:ss";
    /** Database dialect date format */
    private String databaseDialectDateFormat = "yyyy-MM-dd HH:mm:ss";
    /** Database dialect timestamp format */
    private String databaseDialectTimestampFormat = "yyyy-MM-dd HH:mm:ss";
    /** 是否开启慢查询 */
    private String outagedetection = "true";
    /** 慢查询时间, 单位秒 */
    private String outagedetectioninterval = "1";
}
