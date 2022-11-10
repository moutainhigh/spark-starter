package info.spark.starter.cloud.autoconfigure.nacos;

import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import info.spark.starter.basic.exception.ServiceInternalException;
import info.spark.starter.cloud.autoconfigure.nacos.discovery.SparkNacosDiscoveryWatch;
import info.spark.starter.cloud.nacos.spi.CloudNacosLauncherInitiation;

import info.spark.starter.common.start.SparkAutoConfiguration;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.22 19:26
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import(SparkNacosProperties.class)
@SuppressWarnings("all")
@ConditionalOnClass(CloudNacosLauncherInitiation.class)
public class SparkNacosAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Spark nacos config manager spark nacos config manager
     *
     * @param sparkNacosProperties spark nacos properties
     * @return the spark nacos config manager
     * @since 1.0.0
     */
    // @Bean
    public SparkConfigManager sparkNacosConfigManager(@NotNull SparkNacosProperties sparkNacosProperties) {
        log.info("加载 Nacos 自定义配置: {}", sparkNacosProperties);

        List<Listener> listeners = sparkNacosProperties.getListener();

        SparkConfigManager manager = new SparkConfigManager(sparkNacosProperties);

        listeners.forEach(listener -> {
            try {
                manager.getService().addListener(sparkNacosProperties.getConfig().getDataId(),
                                                 sparkNacosProperties.getConfig().getGroup(),
                                                 listener);
            } catch (NacosException e) {
                throw new ServiceInternalException("添加 Nacos Config 监听器失败", e);
            }
        });

        return manager;
    }

    /**
     * Spark naming maintain manager
     *
     * @param sparkNacosProperties spark nacos properties
     * @return the spark naming maintain manager
     * @since 1.7.1
     */
    // @Bean
    public SparkNamingMaintainManager sparkNamingMaintainManager(@NotNull SparkNacosProperties sparkNacosProperties) {
        return new SparkNamingMaintainManager(sparkNacosProperties);
    }

    /**
     * Spark naming manager
     *
     * @param sparkNacosProperties spark nacos properties
     * @return the spark naming manager
     * @since 1.7.1
     */
    // @Bean
    public SparkNamingManager sparkNamingManager(@NotNull SparkNacosProperties sparkNacosProperties) {
        return new SparkNamingManager(sparkNacosProperties);
    }

    /**
     * 应用停止之前自动删除实例
     *
     * @param sparkNamingMaintainManager spark naming maintain manager
     * @return the spark nacos discovery watch
     * @since 1.7.1
     */
    // @Bean
    // @ConditionalOnNacosDiscoveryEnabled
    public SparkNacosDiscoveryWatch sparkNacosDiscoveryWatch(SparkNamingMaintainManager sparkNamingMaintainManager) {
        return new SparkNacosDiscoveryWatch(sparkNamingMaintainManager);
    }

    /**
     * Empty service cleaner handler
     * todo-dong4j : (2021.01.19 22:16) [暂时注释 bean, 因为可能会删除错误的实例]
     *
     * @param discoveryClient          discovery client
     * @param sparkNamingMaintainManager spark naming maintain manager
     * @param sparkNamingManager         spark naming manager
     * @return the empty service cleaner handler
     * @since 1.6.0
     */
    public EmptyServiceCleanerHandler emptyServiceCleanerHandler(DiscoveryClient discoveryClient,
                                                                 SparkNamingMaintainManager sparkNamingMaintainManager,
                                                                 SparkNamingManager sparkNamingManager) {
        return new EmptyServiceCleanerHandler(discoveryClient,
                                              sparkNamingMaintainManager,
                                              sparkNamingManager);
    }


}
