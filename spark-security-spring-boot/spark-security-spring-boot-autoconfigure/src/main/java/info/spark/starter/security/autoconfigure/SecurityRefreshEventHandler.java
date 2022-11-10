package info.spark.starter.security.autoconfigure;

import info.spark.starter.common.event.BaseEventHandler;

import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 监听配置刷新事件 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:05
 * @since 1.6.0
 */
@Slf4j
public class SecurityRefreshEventHandler extends BaseEventHandler<RefreshEvent> {

    /**
     * 处理 {@link DynamicSecurityUrl#dynamicIgnoreUrlByNacosListener()} 发送的事件, 添加一个标识,以便
     * {@link SecurityRefreshScopeRefreshedEventHandler#handler} 处理
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    @EventListener
    public void handler(@NotNull RefreshEvent event) {
        log.info("{}", event.getSource());
        if (event.getEvent() != null
            && String.valueOf(event.getEvent()).equals(SecurityRefreshScopeRefreshedEventHandler.REFRESH_IGNORE_URL_CONFIG)) {
            SecurityRefreshScopeRefreshedEventHandler.DATA_ID.set(SecurityRefreshScopeRefreshedEventHandler.REFRESH_IGNORE_URL_CONFIG);
        }
    }
}
