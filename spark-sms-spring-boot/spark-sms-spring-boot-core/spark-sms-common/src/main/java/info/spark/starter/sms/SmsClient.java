package info.spark.starter.sms;

import info.spark.starter.basic.util.JsonUtils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Description:  </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 17:48
 * @since 1.0.0
 */
public interface SmsClient<T extends SmsMessage<?>> {
    /** log */
    Logger LOG = LoggerFactory.getLogger(SmsClient.class);

    /**
     * unavailable
     *
     * @param config  config
     * @param message message
     * @return the boolean
     * @since 1.6.0
     */
    default boolean unavailable(@NotNull SmsConfig config, T message) {
        if (!config.isEnable()) {
            LOG.error("{}.enable=false, 关闭短信发送功能, 待发送短信内容 = {}", config.getClass().getSimpleName(), JsonUtils.toJson(message));
            return true;
        }
        return false;
    }

    /**
     * Send message
     *
     * @param content content
     * @since 1.0.0
     */
    void sendMessage(@NotNull T content);
}
