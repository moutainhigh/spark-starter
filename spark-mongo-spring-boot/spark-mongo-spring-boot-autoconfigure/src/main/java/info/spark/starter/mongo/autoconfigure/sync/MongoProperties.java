package info.spark.starter.mongo.autoconfigure.sync;

import com.google.common.collect.Maps;

import info.spark.starter.mongo.constant.MongoConstant;
import com.mongodb.ConnectionString;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * <p>Description: mongo 配置类 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.03 12:07
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = MongoProperties.PREFIX)
public class MongoProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.mongo";
    /** DEFAULT_DATASOURCE */
    public static final String DEFAULT_DATASOURCE = MongoConstant.DEFAULT_DATASOURCE;

    /** mongo 实体类扫描路径 */
    private List<String> scanPath;
    /** mongo 多数据源配置 */
    private Map<String, String> datasource = Maps.newHashMapWithExpectedSize(4);
    /** 字段转换策略 */
    private Class<?> fieldNamingStrategy;
    /** 是否自动创建索引 */
    private boolean enableAutoCreateIndex = false;
    /** 是否开启事务 (只有副本集才支持) */
    private boolean enableTransaction = false;
    /** 是否开启 id 自增 */
    private boolean enableAutoIncrementKey = false;
    /** 是否自动生成 id */
    private boolean enableAutoCreateKey = true;
    /** 是否自动生成创建和更新时间字段 */
    private boolean enableAutoCreateTime = false;
    /** 是否保存 class name 到 _class 字段中 */
    private boolean enableSaveClassName = false;
    /** Pool */
    @NestedConfigurationProperty
    private Pool pool = new Pool();

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.16 18:29
     * @since 1.0.0
     * @deprecated 使用 {@link ConnectionString} 代替
     */
    @Data
    @Deprecated
    static class Pool {
        /** mongodb 更多配置, 以下全部为默认配置 */
        private Integer minConnectionPerHost = 0;
        /** Max connection per host */
        private Integer maxConnectionPerHost = 100;
        /** Threads allowed to block for connection multiplier */
        private Integer threadsAllowedToBlockForConnectionMultiplier = 5;
        /** Server selection timeout */
        private Integer serverSelectionTimeout = 30000;
        /** Max wait time */
        private Integer maxWaitTime = 120000;
        /** Max connection idle time */
        private Integer maxConnectionIdleTime = 0;
        /** Max connection life time */
        private Integer maxConnectionLifeTime = 0;
        /** Connect timeout */
        private Integer connectTimeout = 10000;
        /** Socket timeout */
        private Integer socketTimeout = 0;
        /** Ssl enabled */
        private Boolean sslEnabled = false;
        /** Ssl invalid host name allowed */
        private Boolean sslInvalidHostNameAllowed = false;
        /** Heartbeat frequency */
        private Integer heartbeatFrequency = 10000;
        /** Min heartbeat frequency */
        private Integer minHeartbeatFrequency = 500;
        /** Heartbeat connect timeout */
        private Integer heartbeatConnectTimeout = 20000;
        /** Heartbeat socket timeout */
        private Integer heartbeatSocketTimeout = 20000;
        /** Local threshold */
        private Integer localThreshold = 15;
    }
}
