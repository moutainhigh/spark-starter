package info.spark.starter.cloud.autoconfigure.nacos.discovery;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClientConfiguration;
import info.spark.starter.basic.util.BasicUtils;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.ConditionalOnBlockingDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * <p>Description: 自定义 Nacos 元数据 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.02 23:36
 * @since 1.5.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnBlockingDiscoveryEnabled
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore(value = {SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class})
@AutoConfigureAfter(NacosDiscoveryClientConfiguration.class)
public class SparkNacosDiscoveryClientAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Spark nacos discovery client auto configuration
     *
     * @since 1.5.0
     */
    @Contract(pure = true)
    public SparkNacosDiscoveryClientAutoConfiguration() {
    }

    /**
     * Nacos watch
     *
     * @param nacosServiceManager      nacos service manager
     * @param nacosDiscoveryProperties nacos discovery properties
     * @return the nacos watch
     * @since 1.7.1
     */
    @Bean
    @Primary
    @ConditionalOnProperty(value = "spring.cloud.nacos.discovery.watch.enabled", matchIfMissing = true)
    public CustomNacosWatch nacosWatch(NacosServiceManager nacosServiceManager,
                                       @NotNull NacosDiscoveryProperties nacosDiscoveryProperties) {

        // 写入应用版本号
        nacosDiscoveryProperties.getMetadata().put("framework.version", BasicUtils.getFrameworkVersion());
        // 如果不是脚本启动, 则写入启动类型
        if (ConfigKit.isLocalLaunch()) {
            nacosDiscoveryProperties.getMetadata().put(App.START_TYPE, System.getProperty(App.START_TYPE));
        }
        // todo-dong4j : (2021.01.20 01:10) [添加启动 shell 启动参数]
        return new CustomNacosWatch(nacosServiceManager, nacosDiscoveryProperties);
    }
}
