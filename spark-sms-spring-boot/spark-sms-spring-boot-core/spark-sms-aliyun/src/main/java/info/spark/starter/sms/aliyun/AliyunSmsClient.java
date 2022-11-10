package info.spark.starter.sms.aliyun;

import info.spark.starter.sms.SmsClient;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 17:48
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
public class AliyunSmsClient implements SmsClient<AliyunSmsMessage> {

    /** Sms properties */
    private final AliyunConfig config;

    /**
     * Send msg string
     *
     * @param message message
     * @since 1.0.0
     */
    @Override
    @Contract(pure = true)
    public void sendMessage(@NotNull AliyunSmsMessage message) {
        if (this.unavailable(this.config, message)) {
            return;
        }
        log.info("{}", message);
    }
}
