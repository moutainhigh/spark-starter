package info.spark.starter.sms.qcloud.service;

import info.spark.starter.sms.SmsService;
import info.spark.starter.sms.qcloud.QcloudSmsClient;
import info.spark.starter.sms.qcloud.QcloudSmsMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 17:48
 * @since 1.0.0
 */
@AllArgsConstructor
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
public class QcloudSmsService implements SmsService<QcloudSmsMessage> {
    /** QCloud sms client */
    @Getter
    private final QcloudSmsClient qcloudSmsClient;

    /**
     * Send message string
     *
     * @param message message
     * @since 1.0.0
     */
    @Override
    public void sendMessage(QcloudSmsMessage message) {
        this.qcloudSmsClient.sendMessage(message);
    }
}
