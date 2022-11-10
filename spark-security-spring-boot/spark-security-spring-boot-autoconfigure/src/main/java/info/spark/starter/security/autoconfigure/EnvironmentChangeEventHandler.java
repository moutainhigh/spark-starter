package info.spark.starter.security.autoconfigure;

import info.spark.starter.common.event.BaseEventHandler;

import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 监听配置刷新事件 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:12
 * @since 1.0.0
 */
@Slf4j
public class EnvironmentChangeEventHandler extends BaseEventHandler<EnvironmentChangeEvent> {

    /**
     * Handler *
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    @EventListener
    public void handler(@NotNull EnvironmentChangeEvent event) {
        log.info("配置刷新, 捕获 EnvironmentChange Event {}", event.getSource());
    }
}
