package info.spark.starter.sms.aliyun.service;

import info.spark.starter.sms.SmsService;
import info.spark.starter.sms.aliyun.AliyunSmsClient;
import info.spark.starter.sms.aliyun.AliyunSmsMessage;

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
public class AliyunSmsService implements SmsService<AliyunSmsMessage> {
    /** Aliyun sms client */
    @Getter
    private final AliyunSmsClient aliyunSmsClient;

    /**
     * Send message string
     *
     * @param message message
     * @since 1.0.0
     */
    @Override
    public void sendMessage(AliyunSmsMessage message) {
        this.aliyunSmsClient.sendMessage(message);
    }
}
