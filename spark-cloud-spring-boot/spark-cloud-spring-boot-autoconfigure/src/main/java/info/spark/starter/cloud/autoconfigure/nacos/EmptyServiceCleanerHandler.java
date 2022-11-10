package info.spark.starter.cloud.autoconfigure.nacos;

import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import info.spark.starter.basic.util.TimeoutUtils;
import info.spark.starter.common.event.BaseEventHandler;
import info.spark.starter.common.util.ConfigKit;

import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 通过心跳删除空实例 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.23 14:44
 * @since 1.5.0
 */
@Slf4j
public class EmptyServiceCleanerHandler extends BaseEventHandler<HeartbeatEvent> {
    /** Discovery properties */
    private final SparkNacosProperties.Discovery discoveryProperties;
    /** Discovery client */
    private final DiscoveryClient discoveryClient;
    /** Nacos discovery properties */
    private final SparkNamingMaintainManager sparkNamingMaintainManager;
    /** Spark naming manager */
    private final SparkNamingManager sparkNamingManager;

    /**
     * Empty service cleaner handler
     *
     * @param discoveryClient          discovery client
     * @param sparkNamingMaintainManager spark naming maintain manager
     * @param sparkNamingManager         spark naming manager
     * @since 1.6.0
     */
    public EmptyServiceCleanerHandler(DiscoveryClient discoveryClient,
                                      SparkNamingMaintainManager sparkNamingMaintainManager,
                                      SparkNamingManager sparkNamingManager) {
        this.discoveryClient = discoveryClient;
        this.sparkNamingMaintainManager = sparkNamingMaintainManager;
        this.discoveryProperties = sparkNamingMaintainManager.getProperties().getDiscovery();
        this.sparkNamingManager = sparkNamingManager;
    }

    /**
     * Handler.
     *
     * @param event the event
     * @since 1.0.0
     */
    @Override
    @EventListener(HeartbeatEvent.class)
    public void handler(@NotNull HeartbeatEvent event) {
        try {
            List<ServiceInfo> subscribeServices = this.sparkNamingManager.getService().getSubscribeServices();
            List<String> services = this.discoveryClient.getServices();
            log.debug("[{}]", services);
            for (String service : services) {
                if (this.discoveryClient.getInstances(service).size() == 0) {
                    TimeoutUtils.process(() -> {
                        boolean result = false;
                        NamingMaintainService namingMaintainService = this.sparkNamingMaintainManager.getService();
                        if (namingMaintainService != null) {
                            try {
                                result = namingMaintainService.deleteService(service, this.discoveryProperties.getGroup());
                                log.info("删除 Nacos 实例: [{}]:[{}]", ConfigKit.getAppName(), this.discoveryProperties.getGroup());
                            } catch (Exception e) {
                                log.warn("[{}]", e.getMessage());
                            }
                        }
                        return result;
                    }, 2);
                }
            }
        } catch (Exception ignored) {
        }
    }

}


