package info.spark.starter.security.autoconfigure;

import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.event.BaseEventHandler;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 监听 security-ignore-url.yml 配置刷新事件 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:21
 * @see DynamicSecurityUrl#dynamicIgnoreUrlByNacosListener()
 * @since 1.0.0
 */
@Slf4j
public class SecurityRefreshScopeRefreshedEventHandler extends BaseEventHandler<RefreshScopeRefreshedEvent> implements DisposableBean {

    /** Dynamic security url */
    private final DynamicSecurityUrl dynamicSecurityUrl;
    /** DATA_ID */
    static final ThreadLocal<String> DATA_ID = new ThreadLocal<>();
    /** REFRESH_IGNORE_URL_CONFIG */
    static final String REFRESH_IGNORE_URL_CONFIG = "REFRESH_IGNORE_URL_CONFIG";

    /**
     * Refresh scope refreshed event handler
     *
     * @param dynamicSecurityUrl dynamic security url
     * @since 1.0.0
     */
    SecurityRefreshScopeRefreshedEventHandler(DynamicSecurityUrl dynamicSecurityUrl) {
        this.dynamicSecurityUrl = dynamicSecurityUrl;
    }

    /**
     * 只处理 security-ignore-url.yml 被刷新的事件
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    @EventListener
    public void handler(@NotNull RefreshScopeRefreshedEvent event) {

        if (DATA_ID.get() != null
            && String.valueOf(DATA_ID.get()).equals(REFRESH_IGNORE_URL_CONFIG)) {
            log.debug("配置刷新完成, 更新之前的配置 {}", JsonUtils.toJson(this.dynamicSecurityUrl.getAllIgnoreUrlMap(), true));
            this.dynamicSecurityUrl.updateIgnoreUrl();
            log.debug("配置刷新完成, 更新之后的配置 {}", JsonUtils.toJson(this.dynamicSecurityUrl.getAllIgnoreUrlMap(), true));
            DATA_ID.remove();
        }

    }

    /**
     * Destroy
     *
     * @since 1.0.0
     */
    @Override
    public void destroy() {
        DATA_ID.remove();
    }
}
