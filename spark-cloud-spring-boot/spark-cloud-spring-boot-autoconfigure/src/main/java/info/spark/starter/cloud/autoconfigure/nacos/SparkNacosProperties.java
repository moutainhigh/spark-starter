package info.spark.starter.cloud.autoconfigure.nacos;

import com.google.common.collect.Lists;

import com.alibaba.nacos.api.config.listener.Listener;
import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.common.constant.App;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.annotation.Resource;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.alibaba.nacos.api.PropertyKeyConst.CLUSTER_NAME;
import static com.alibaba.nacos.api.PropertyKeyConst.NAMESPACE;
import static com.alibaba.nacos.api.PropertyKeyConst.NAMING_LOAD_CACHE_AT_START;
import static com.alibaba.nacos.api.PropertyKeyConst.SERVER_ADDR;

/**
 * <p>Description: 直接使用 Nacos API 的配置类  </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.22 19:36
 * @since 1.0.0
 */
@Data
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = SparkNacosProperties.PREFIX)
public class SparkNacosProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.nacos";
    /** DEFAULT_GROUP */
    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    /** DEFAULT_INTERVAL */
    public static final Long DEFAULT_INTERVAL = 120 * 1000L;

    /** Config */
    @Resource
    public Config config;
    /** Discovery */
    @Resource
    public Discovery discovery;
    /** 监听器 */
    private List<Listener> listener = Lists.newArrayList();
    /** 是否自动向 nacos 创建配置 (非本地开发环境默认为 false) */
    private Boolean enableAutoCreateConfig = false;
    /** 是否优先从 Nacos 中读取配置 (非本地开发环境默认为 true) */
    private Boolean enableNacosConfig;
    /** 是否更新元数据 */
    private Boolean enableUpdateMetadata = true;

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.20 18:12
     * @since 1.0.0
     */
    @Data
    @Validated
    @Component
    @EqualsAndHashCode(callSuper = true)
    @ConfigurationProperties(prefix = Config.PREFIX)
    @RefreshScope
    public static class Config extends CommonNacos {
        /** PREFIX */
        public static final String PREFIX = "spark.nacos.config";

        /** Data id */
        private String dataId;
        /** 是否定时更新内存使用信息 */
        private boolean enableUpdateMemory = true;
        /** 跟新内存使用信息间隔 */
        private Long updateMemoryInterval = DEFAULT_INTERVAL;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.20 18:12
     * @since 1.0.0
     */
    @Data
    @Validated
    @Component
    @EqualsAndHashCode(callSuper = true)
    @ConfigurationProperties(prefix = Discovery.PREFIX)
    public static class Discovery extends CommonNacos {
        /** PREFIX */
        public static final String PREFIX = "spark.nacos.discovery";
        /** Cluster name */
        private String clusterName = "DEFAULT";
        /** Naming load cache at start */
        private String namingLoadCacheAtStart = "false";
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.01.19 23:17
     * @since 1.7.1
     */
    @Data
    static class CommonNacos {
        /** 服务发现地址 */
        protected String serverAddr = ConfigDefaultValue.NACOS_SERVER;
        /** Namespace */
        protected String namespace = App.SPARK_NAME_SPACE;
        /** Group */
        protected String group = DEFAULT_GROUP;
    }

    /**
     * Assemble config service properties properties
     *
     * @return the properties
     * @since 1.0.0
     */
    public Properties assembleConfigServiceProperties() {
        Properties properties = new Properties();
        properties.put(SERVER_ADDR, Objects.toString(this.config.getServerAddr(), ""));
        properties.put(NAMESPACE, Objects.toString(this.config.getNamespace(), ""));
        return properties;
    }

    /**
     * Assemble discovery service properties properties
     *
     * @return the properties
     * @since 1.0.0
     */
    public Properties assembleDiscoveryServiceProperties() {
        Properties properties = new Properties();
        properties.put(SERVER_ADDR, Objects.toString(this.discovery.getServerAddr(), ""));
        properties.put(NAMESPACE, Objects.toString(this.discovery.getNamespace(), ""));
        properties.put(CLUSTER_NAME, this.discovery.getClusterName());
        properties.put(NAMING_LOAD_CACHE_AT_START, this.discovery.getNamingLoadCacheAtStart());
        return properties;
    }
}
