package info.spark.starter.dubbo.listener;

import info.spark.starter.common.SparkApplicationListener;
import info.spark.starter.common.context.SpringContext;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.dubbo.DubboConstant;
import info.spark.starter.dubbo.check.RpcCheck;

import info.spark.starter.processor.annotation.AutoListener;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.07 22:16
 * @since 1.5.0
 */
@Slf4j
@AutoListener
public class DubboRegistryCheckListener implements SparkApplicationListener {

    /**
     * On application ready event
     *
     * @param event event
     * @since 1.5.0
     */
    @Override
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {

        SparkApplicationListener.Runner.executeAtLast(this.getClass().getName(), () -> {
            RpcCheck genericService = SpringContext.getInstance(RpcCheck.class);
            ServiceConfig<RpcCheck> service = new ServiceConfig<>();
            ApplicationModel.getConfigManager().setApplication(SpringContext.getInstance(ApplicationConfig.class));
            service.setInterface(RpcCheck.class);
            service.setRef(genericService);
            service.setVersion(ConfigKit.getFrameworkVersion());
            service.setGroup(DubboConstant.RPC_CHECK_GROUP);
            service.export();
            log.info("发布 RPC Check 服务: {}", service.getExportedUrls());
        });
    }

}
