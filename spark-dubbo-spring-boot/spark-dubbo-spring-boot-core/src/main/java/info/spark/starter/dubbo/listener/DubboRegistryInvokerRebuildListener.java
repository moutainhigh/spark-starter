package info.spark.starter.dubbo.listener;

import com.alibaba.cloud.dubbo.metadata.repository.DubboServiceMetadataRepository;
import com.alibaba.cloud.dubbo.registry.event.ServiceInstancesChangedEvent;
import com.alibaba.cloud.dubbo.service.DubboGenericServiceFactory;
import com.alibaba.cloud.dubbo.service.DubboMetadataServiceProxy;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 解决注册中心推送新IP的时候, dubbo 服务一直不替换老 IP 或端口的 BUG </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.5 22:44
 * @since 1.5.0
 */
@Slf4j
@SuppressWarnings("all")
@Order(Integer.MIN_VALUE + 100)
public class DubboRegistryInvokerRebuildListener implements ApplicationListener<ServiceInstancesChangedEvent> {

    /** Dubbo service metadata repository */
    @Resource
    private DubboServiceMetadataRepository dubboServiceMetadataRepository;
    /** Dubbo generic service factory */
    @Resource
    private DubboGenericServiceFactory dubboGenericServiceFactory;
    /** Dubbo metadata service proxy */
    @Resource
    private DubboMetadataServiceProxy dubboMetadataServiceProxy;

    /**
     * On application event
     *
     * @param event event
     * @since 1.5.0
     */
    @Override
    public void onApplicationEvent(@NotNull ServiceInstancesChangedEvent event) {
        String serviceName = event.getServiceName();
        log.trace("Service Instances Changed Event --> {}", serviceName);
        // if (serviceName == null) {
        //     return;
        // }

        // todo-dong4j : (2021.01.9 20:21) []
        // this.dubboServiceMetadataRepository.removeMetadataAndInitializedService(serviceName);
        // this.dubboGenericServiceFactory.destroy(serviceName);
        // this.dubboMetadataServiceProxy.removeProxy(serviceName);
    }
}
