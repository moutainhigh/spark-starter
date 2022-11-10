package info.spark.starter.sms.yimei.service;

import info.spark.starter.sms.SmsService;
import info.spark.starter.sms.yimei.YimeiSmsClient;
import info.spark.starter.sms.yimei.YimeiSmsMessage;
import info.spark.starter.sms.yimei.entity.MoDTO;

import java.util.List;

import lombok.AllArgsConstructor;

/**
 * <p>Description: 亿美短信API</p>
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.03 11:22
 * @since 1.0.0
 */
@AllArgsConstructor
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
public class YimeiSmsService implements SmsService<YimeiSmsMessage> {

    /** yimeiSmsClient */
    private final YimeiSmsClient yimeiSmsClient;

    /**
     * 发送短信
     *
     * @param message 下发短信参数实体
     * @since 1.0.0
     */
    @Override
    public void sendMessage(YimeiSmsMessage message) {
        this.yimeiSmsClient.sendMessage(message);
    }

    /**
     * Gets mo *
     *
     * @return the mo
     * @since 1.0.0
     */
    public List<MoDTO> getMo() {
        return this.yimeiSmsClient.getMo();
    }
}
