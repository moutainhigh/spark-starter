package info.spark.starter.cloud.autoconfigure.nacos.config;

import com.alibaba.cloud.nacos.NacosConfigBootstrapConfiguration;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.client.NacosPropertySourceLocator;
import com.alibaba.nacos.api.config.ConfigService;
import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.basic.util.HostUtils;
import info.spark.starter.basic.util.IoUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.cloud.autoconfigure.nacos.SparkNacosProperties;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.dns.DnsCacheEntry;
import info.spark.starter.common.dns.DnsCacheManipulator;
import info.spark.starter.common.util.ConfigKit;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 在 NacosConfigBootstrapConfiguration 之前执行 </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.21 16:38
 * @see NacosConfigBootstrapConfiguration
 * @see PropertySourceBootstrapConfiguration#initialize(org.springframework.context.ConfigurableApplicationContext)
 * @since 1.4.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(NacosConfigBootstrapConfiguration.class)
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)
public class SparkNacosBootstrapConfiguration {

    /**
     * Spark nacos bootstrap configuration
     *
     * @since 1.5.0
     */
    public SparkNacosBootstrapConfiguration() {
        log.debug("加载 Nacos 自定义 Bootstrap 装配类: {}", SparkNacosBootstrapConfiguration.class.getName());
    }

    /**
     * Nacos config properties
     *
     * @return the nacos config properties
     * @since 1.5.0
     */
    @Bean
    @Primary
    public NacosConfigProperties nacosConfigProperties() {
        return new NacosConfigProperties();
    }

    /**
     * Nacos config manager
     *
     * @param nacosConfigProperties nacos config properties
     * @return the nacos config manager
     * @since 1.7.1
     */
    @Bean
    @Primary
    public NacosConfigManager nacosConfigManager(NacosConfigProperties nacosConfigProperties) {
        return new NacosConfigManager(nacosConfigProperties);
    }

    /**
     * Nacos property source locator
     *
     * @param nacosConfigManager nacos config manager
     * @return the nacos property source locator
     * @since 1.4.0
     */
    @Bean
    @Primary
    @SneakyThrows
    public NacosPropertySourceLocator nacosPropertySourceLocator(NacosConfigManager nacosConfigManager) {
        DnsCacheEntry dnsCache = DnsCacheManipulator.getDnsCache(ConfigDefaultValue.NACOS_HOST);
        log.info("Nacos DNS Cache: {}", dnsCache == null
                                        ? "优先使用本地 hosts 配置: " + HostUtils.getIps(HostUtils.read(), ConfigDefaultValue.NACOS_HOST)
                                        : dnsCache + ". 注意: 如果 DNS Cache 未生效, 请检查本地是否启用了代理服务(GWF)");
        return new SparkNacosPropertySourceLocator(nacosConfigManager);
    }

    /**
         * <p>Description: 在父类之前执行 </p>
     *
     * @author dong4j
     * @version 1.4.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.05.21 16:56
     * @since 1.4.0
     */
    @Order(-1)
    static class SparkNacosPropertySourceLocator extends NacosPropertySourceLocator {
        /** Nacos config properties */
        private final NacosConfigProperties nacosConfigProperties;
        /** Config service */
        private final ConfigService configService;
        /** formatter:off 写入到 nacos 主配置, 本地开发时的配置优先于 nacos 配置 */
        private static final String CONFIG_OVERRIDE = "";

        /** DEFAULT_LOGGING */
        private static final String DEFAULT_LOGGING = "# 自动生成的日志配置, 请根据业务修改或删除 \n"
                                                      + "spark:\n"
                                                      + "  logging:\n"
                                                      + "    level: \n"
                                                      + "      root: info\n\n";

        /** formatter:on 本地配置的默认名 */
        private static final String LOCAL_CONFIG_NAME = "application";

        /**
         * Spark nacos property source locator
         *
         * @param nacosConfigManager nacos config manager
         * @since 1.4.0
         */
        SparkNacosPropertySourceLocator(NacosConfigManager nacosConfigManager) {
            super(nacosConfigManager);
            this.configService = nacosConfigManager.getConfigService();
            this.nacosConfigProperties = nacosConfigManager.getNacosConfigProperties();
        }

        /**
         * 在获取 nacos 配置之前, 向 nacos 写入当前应用的配置, 避免每次手动去 naocs 创建应用配置
         *
         * @param environment environment
         * @return the property source
         * @see PropertySourceBootstrapConfiguration#initialize PropertySourceBootstrapConfiguration#initialize
         * @since 1.4.0
         */
        @Override
        public PropertySource<?> locate(@NotNull Environment environment) {
            if (null == this.configService) {
                log.error("no instance of config service found, can't load config from nacos");
                return null;
            }

            SparkNacosProperties sparkNacosProperties = new SparkNacosProperties();
            Binder.get(environment).bind(SparkNacosProperties.PREFIX, Bindable.ofInstance(sparkNacosProperties));

            if (ConfigKit.isLocalLaunch()) {
                // 本地开发时, 未配置时设置为 false
                if (sparkNacosProperties.getEnableNacosConfig() == null) {
                    log.warn("本地开发且未显式配置 [{}], 将默认为 false", ConfigKey.NacosConfigKey.ENABLE_NACOS_CONFIG);
                    sparkNacosProperties.setEnableNacosConfig(false);
                }
            } else {
                log.warn("非本地开发环境, 默认读取 Nacos 配置");
                sparkNacosProperties.setEnableAutoCreateConfig(false);
                sparkNacosProperties.setEnableNacosConfig(true);
            }

            if (!sparkNacosProperties.getEnableAutoCreateConfig()) {
                log.info("[{}=false], 关闭自动创建 Nacos 配置", ConfigKey.NacosConfigKey.ENABLE_AUTO_CREATE_CONFIG);
            } else {
                // 自动创建配置
                this.autoCreateNacosConfig(environment);
            }

            if (!sparkNacosProperties.getEnableNacosConfig()) {
                log.warn("[{}=false], 优先使用本地配置, 不加载 Nacos 配置 (设置为 true 以启用 Nacos 配置). ", ConfigKey.NacosConfigKey.ENABLE_NACOS_CONFIG);
                return null;
            }

            // 加载 Nacos 配置
            PropertySource<?> propertySource = super.locate(environment);
            Environment newEnvironment = this.environment(propertySource);

            this.checkNacosConfig(newEnvironment);
            return propertySource;
        }

        /**
         * 检查当前应用的 Nacos 是否配置了 spring.cloud.config.override-none, 如果为 true 将优先使用 本地配置
         *
         * @param newEnvironment new environment
         * @since 1.5.0
         */
        private void checkNacosConfig(Environment newEnvironment) {
            PropertySourceBootstrapProperties remoteProperties = new PropertySourceBootstrapProperties();
            Binder.get(newEnvironment).bind("spring.cloud.config",
                                            Bindable.ofInstance(remoteProperties));

            log.warn(remoteProperties.isOverrideNone()
                     ? "Nacos: [{}={}], 优先使用本地配置"
                     : "Nacos: [{}={}], 优先使用 Nacos 配置",
                     ConfigKey.CloudConfigKey.CONFIG_OVERRIDE_NONE,
                     remoteProperties.isOverrideNone());
        }

        /**
         * Environment
         *
         * @param composite composite
         * @return the environment
         * @since 1.5.0
         */
        private @NotNull Environment environment(PropertySource<?> composite) {
            MutablePropertySources incoming = new MutablePropertySources();
            incoming.addFirst(composite);

            StandardEnvironment environment = new StandardEnvironment();
            for (PropertySource<?> source : environment.getPropertySources()) {
                environment.getPropertySources().remove(source.getName());
            }
            for (PropertySource<?> source : incoming) {
                environment.getPropertySources().addLast(source);
            }
            return environment;
        }

        /**
         * 当 Nacos 没有当前应用配置时自动创建.
         *
         * @param environment environment
         * @since 1.5.0
         */
        private void autoCreateNacosConfig(@NotNull Environment environment) {
            if (ConfigKit.isLocalLaunch()) {
                String fileExtension = this.nacosConfigProperties.getFileExtension();
                String nacosGroup = this.nacosConfigProperties.getGroup();

                String name = this.getName(environment);
                String dataId = name + StringPool.DOT + fileExtension;
                String primaryConfig = LOCAL_CONFIG_NAME + StringPool.DOT + fileExtension;
                // 主配置
                this.publishConfig(nacosGroup, dataId, true, primaryConfig);

                for (String profile : environment.getActiveProfiles()) {
                    dataId = name + StringPool.DASH + profile + StringPool.DOT + fileExtension;
                    primaryConfig = LOCAL_CONFIG_NAME + StringPool.DASH + profile + StringPool.DOT + fileExtension;
                    // 环境配置
                    this.publishConfig(nacosGroup, dataId, false, primaryConfig);
                }
            }
        }

        /**
         * 直接通过 nacos API 推送应用配置, 如果存在则不创建.
         * 注意: 创建的配置可能存在相同的配置, 需要开发者手动去修改.
         *
         * @param nacosGroup      nacos group
         * @param dataId          data id
         * @param primary         primary
         * @param localConfigName local config name
         * @since 1.5.0
         */
        private void publishConfig(String nacosGroup, String dataId, boolean primary, String localConfigName) {
            try {
                long timeout = this.nacosConfigProperties.getTimeout();
                String data = this.configService.getConfig(dataId, nacosGroup, timeout);
                if (StringUtils.isEmpty(data)) {
                    String finalContent;

                    Resource resource = ConfigKit.getResource(localConfigName);
                    String originalConfig = IoUtils.copyToString(resource.getInputStream(), Charsets.UTF_8);

                    if (primary) {
                        finalContent = CONFIG_OVERRIDE;
                        PropertySource<?> propertySource = ConfigKit.getPropertySource(localConfigName);
                        // 如果本地主配置没有配置 spring.cloud.config.override-none=true 则添加到本地配置然后写入到 Nacos 配置
                        if (propertySource.getProperty(ConfigKey.CloudConfigKey.CONFIG_OVERRIDE_NONE) == null) {
                            log.info("{}", propertySource);
                            finalContent += originalConfig;
                        } else {
                            finalContent = originalConfig;
                        }
                    } else {
                        finalContent = StringUtils.isEmpty(originalConfig) ? DEFAULT_LOGGING : originalConfig;
                    }

                    this.configService.publishConfig(dataId, nacosGroup, finalContent);
                    log.info("创建 {}:{}:{} 初始化配置: \n{}", App.SPARK_NAME_SPACE, dataId, nacosGroup, finalContent);
                }
            } catch (Exception e) {
                log.warn("自动创建 {} 配置失败: [{}]", dataId, e.getMessage());
            }
        }

        /**
         * 应用名
         *
         * @param environment environment
         * @return the name
         * @since 1.5.0
         */
        private String getName(Environment environment) {
            String name = this.nacosConfigProperties.getName();

            String dataIdPrefix = this.nacosConfigProperties.getPrefix();
            if (StringUtils.isEmpty(dataIdPrefix)) {
                dataIdPrefix = name;
            }

            if (StringUtils.isEmpty(dataIdPrefix)) {
                dataIdPrefix = environment.getProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME);
            }

            return dataIdPrefix;
        }
    }

}
