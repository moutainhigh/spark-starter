package info.spark.starter.cloud.autoconfigure.nacos.discovery;

import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import info.spark.starter.basic.util.TimeoutUtils;
import info.spark.starter.cloud.autoconfigure.nacos.SparkNacosProperties;
import info.spark.starter.cloud.autoconfigure.nacos.SparkNamingMaintainManager;
import info.spark.starter.common.util.ConfigKit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.02 23:45
 * @see NacosAutoServiceRegistration
 * @since 1.5.0
 */
@Slf4j
public class SparkNacosDiscoveryWatch implements ApplicationEventPublisherAware {
    /** Spark naming maintain manager */
    private final SparkNamingMaintainManager sparkNamingMaintainManager;
    /** Discovery properties */
    private final SparkNacosProperties.Discovery discoveryProperties;
    /** Running */
    private final AtomicBoolean running;
    /** Publisher */
    @SuppressWarnings("FieldCanBeLocal")
    private ApplicationEventPublisher publisher;

    /**
     * Spark nacos discovery watch
     *
     * @param sparkNamingMaintainManager spark naming maintain manager
     * @since 1.5.0
     */
    @Contract(pure = true)
    public SparkNacosDiscoveryWatch(SparkNamingMaintainManager sparkNamingMaintainManager) {
        this.sparkNamingMaintainManager = sparkNamingMaintainManager;
        this.discoveryProperties = sparkNamingMaintainManager.getProperties().getDiscovery();
        this.running = new AtomicBoolean(false);
        this.running.compareAndSet(false, true);
    }

    /**
     * Sets application event publisher *
     *
     * @param publisher publisher
     * @since 1.5.0
     */
    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 暂时注释 @PreDestroy
     *
     * @since 1.5.0
     */
    public void destroy() {
        this.stop(ConfigKit.getAppName());
    }

    /**
     * Stop
     * todo-dong4j : (2021.01.20 02:40) []
     * failed to req API:/nacos/v1/ns/service after all servers([nacos.server:8848]) tried: failed to req API:nacos
     * .server:8848/nacos/v1/ns/service. code:400 msg: specified service has instances, serviceName :
     * DEFAULT_GROUP@@spark-agent-spring-boot-sample-cloud-integration
     * 应用关闭时, nacos 上注册的当前应用还没有下线, Nacos 不允许删除未下线的应用
     *
     * @param appName app name
     * @since 1.5.0
     */
    public void stop(String appName) {
        if (this.running.compareAndSet(true, false)) {
            log.trace("应用关闭, 删除实例");
            try {
                TimeoutUtils.process(() -> {
                    try {
                        NamingMaintainService namingMaintainService = this.sparkNamingMaintainManager.getService();
                        namingMaintainService.deleteService(appName,
                                                            this.discoveryProperties.getGroup());
                        log.trace("应用关闭, 删除 Nacos 实例: {}:{}", ConfigKit.getAppName(), this.discoveryProperties.getGroup());
                    } catch (Exception ignored) {
                        // nothing to do
                    }
                    return null;
                }, 2);
            } catch (Throwable ignored) {
                // nothing to do
            }
        }
    }

}
