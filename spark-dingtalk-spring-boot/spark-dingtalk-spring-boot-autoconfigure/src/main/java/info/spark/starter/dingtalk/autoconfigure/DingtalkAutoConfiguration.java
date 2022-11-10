package info.spark.starter.dingtalk.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.util.BeanUtils;
import info.spark.starter.dingtalk.config.DingtalkConfig;
import info.spark.starter.dingtalk.entity.DingtalkMessage;
import info.spark.starter.dingtalk.service.DingtalkNotifyService;
import info.spark.starter.dingtalk.service.impl.DingtalkNotifyServiceImpl;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.05 00:27
 * @since 2.1.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = {DingtalkProperties.class})
public class DingtalkAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Dingtalk notify service
     *
     * @param properties properties
     * @return the dingtalk notify service
     * @since 2.1.0
     */
    @Bean
    @ConditionalOnProperty(prefix = DingtalkProperties.PREFIX, value = "webhook")
    public DingtalkNotifyService<DingtalkMessage<?>> dingtalkNotifyService(@NotNull DingtalkProperties properties) {
        if (!properties.isEnabled()) {
            return this.defaultDingtalkService();
        }
        return new DingtalkNotifyServiceImpl(BeanUtils.copy(properties, DingtalkConfig.class));
    }

    /**
     * No dingtalk service
     *
     * @return the dingtalk notify service
     * @since 2.1.0
     */
    @Bean
    @ConditionalOnMissingBean(DingtalkNotifyService.class)
    public DingtalkNotifyService<DingtalkMessage<?>> noDingtalkService() {
        return this.defaultDingtalkService();
    }

    /**
     * Default dingtalk service
     *
     * @return the dingtalk notify service
     * @since 2.1.0
     */
    @Contract(value = " -> new", pure = true)
    @NotNull
    private DingtalkNotifyService<DingtalkMessage<?>> defaultDingtalkService() {
        return new DingtalkNotifyService<DingtalkMessage<?>>() {

            /**
             * 同步通知
             *
             * @param content 发送内容
             * @return the string
             * @since 1.4.0
             */
            @Override
            public DingtalkMessage<?> notify(DingtalkMessage<?> content) {
                log.warn("未开启 Dingtalk 服务, 请删除 spark.notify.dingtalk.enabled=false 或设置为 true. content: [{}]", content);
                return content;
            }
        };
    }


}
