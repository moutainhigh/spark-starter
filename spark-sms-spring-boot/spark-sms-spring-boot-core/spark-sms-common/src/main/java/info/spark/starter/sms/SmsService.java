package info.spark.starter.sms;

import info.spark.starter.notify.Message;
import info.spark.starter.notify.Notify;

/**
 * <p>Description:  </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 17:47
 * @since 1.0.0
 */
public interface SmsService<T extends Message<?>> extends Notify<T> {

    /**
     * Send message string
     *
     * @param sms sms
     * @since 1.0.0
     */
    void sendMessage(T sms);

    /**
     * Notify
     *
     * @param content content
     * @return the t
     * @since 1.0.0
     */
    @Override
    default T notify(T content) {
        this.sendMessage(content);
        return content;
    }
}
