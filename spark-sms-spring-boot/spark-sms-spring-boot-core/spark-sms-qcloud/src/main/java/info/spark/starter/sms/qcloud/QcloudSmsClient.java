package info.spark.starter.sms.qcloud;

import info.spark.starter.sms.SmsConfig;
import info.spark.starter.sms.exception.SmsException;
import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.core.util.EnumUtils;
import info.spark.starter.sms.SmsClient;
import info.spark.starter.validation.util.RegexUtils;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.httpclient.HTTPException;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 18:05
 * @since 1.0.0
 */
@Slf4j
public class QcloudSmsClient implements SmsClient<QcloudSmsMessage> {

    /** Sms properties */
    private final QcloudConfig config;
    /** Sms single sender */
    private final SmsSingleSender smsSingleSender;
    /** Sms multi sender */
    private final SmsMultiSender smsMultiSender;

    /**
     * Qcloud sms client
     *
     * @param config config
     * @since 1.0.0
     */
    @Contract(pure = true)
    public QcloudSmsClient(@NotNull QcloudConfig config) {
        this.config = config;
        this.smsMultiSender = new SmsMultiSender(Integer.parseInt(config.getAppId()), config.getAppKey());
        this.smsSingleSender = new SmsSingleSender(Integer.parseInt(config.getAppId()), config.getAppKey());
    }

    /**
     * Send msg string
     *
     * @param content content
     * @since 1.0.0
     */
    @Override
    @Contract(pure = true)
    @SuppressWarnings("checkstyle:Indentation")
    public void sendMessage(@NotNull QcloudSmsMessage content) {
        if (this.unavailable(this.config, content)) {
            return;
        }
        // 检查手机号数量
        BaseCodes.PARAM_VERIFY_ERROR.isFalse(CollectionUtils.isEmpty(content.getPhone()), "接收消息的手机号不能为空");
        // 检查每个号码
        boolean allMatch = content.getPhone().stream().allMatch(p -> RegexUtils.match(RegexUtils.PHONE, p));
        BaseCodes.PARAM_VERIFY_ERROR.isTrue(allMatch, "手机号不是正确的手机号");

        int resultCode;
        try {
            // 使用模板发送短信
            resultCode = this.smsSingleSender.sendWithParam(SmsConfig.MAINLAND_CHINA,
                                                            content.getPhone().get(0),
                                                            content.getTemplateId(),
                                                            content.getParams(),
                                                            this.config.getSign(),
                                                            "",
                                                            "").result;

            QcloudSmsCodes smsCodes = EnumUtils.of(QcloudSmsCodes.class,
                                                   q -> q.getCode() == resultCode).orElseThrow(QcloudSmsCodes.UNKNOWN::newException);

            if (smsCodes != QcloudSmsCodes.OK) {
                throw new SmsException(smsCodes);
            }

        } catch (HTTPException | IOException | JSONException e) {
            throw QcloudSmsCodes.UNKNOWN.newException(e.getMessage());
        }
    }


}
