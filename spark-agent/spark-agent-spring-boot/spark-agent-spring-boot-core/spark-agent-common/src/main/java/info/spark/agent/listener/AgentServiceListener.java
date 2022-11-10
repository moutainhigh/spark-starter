package info.spark.agent.listener;

import info.spark.starter.processor.annotation.AutoListener;

import info.spark.starter.basic.context.ComponentThreadLocal;
import info.spark.starter.common.SparkApplicationListener;
import info.spark.starter.util.CollectionUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.core.Ordered;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 优先级要在 {info.spark.starter.cloud.autoconfigure.nacos.listener.SparkCloudAppStartedListener} 之后,
 * 以便在没有集成 cloud 组件时主动删除 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 16:37
 * @since 1.7.1
 */
@Slf4j
@AutoListener
public class AgentServiceListener implements SparkApplicationListener {

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.7.1
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 900;
    }

    /**
     * 清理存储线程变量中的 agent service 列表
     *
     * @param event event
     * @since 1.7.1
     */
    @Override
    public void onApplicationStartedEvent(@NotNull ApplicationStartedEvent event) {
        SparkApplicationListener.Runner.executeAtLast(this.key(event, this.getClass()), () -> {
            Map<String, Object> agentServices = ComponentThreadLocal.context().get();
            if (CollectionUtils.isNotEmpty(agentServices)) {
                agentServices.clear();
            }
        });
    }
}
