package info.spark.starter.launcher.listener;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.SparkApplicationListener;
import info.spark.starter.common.dns.internal.InetAddressCacheUtils;
import info.spark.starter.processor.annotation.AutoListener;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>Description: 在配置初始化完成后加载 dns 配置, 优先级必须设置为最高, 否则连接不了 Nacos </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.09 18:14
 * @since 1.5.0
 */
@AutoListener
public class DnsCacheListener implements SparkApplicationListener {
    /**
     * 优先级最高
     *
     * @return the order
     * @since 1.5.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * On application environment prepared event
     *
     * @param event event
     * @since 1.5.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        SparkApplicationListener.Runner.executeAtFirst(
            this.key(event, this.getClass()),
            () -> InetAddressCacheUtils.loadDnsProperties(environment.getProperty(ConfigKey.SpringConfigKey.PROFILE_ACTIVE)));
    }

}
