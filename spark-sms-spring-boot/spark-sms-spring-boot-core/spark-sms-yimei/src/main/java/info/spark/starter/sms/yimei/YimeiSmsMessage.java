package info.spark.starter.sms.yimei;

import info.spark.starter.sms.SmsMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: 亿美短信请求参数实体</p>
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.08 20:18
 * @since 1.0.0
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class YimeiSmsMessage extends SmsMessage<Long> {
    /** serialVersionUID */
    private static final long serialVersionUID = -6536350927874308862L;

    /**
     * 短信内容的前缀，默认为 Spark，可以自己传入，例如：xxx ，最后你收到的短信模板内容如：
     * 【xxx】您于2020年12月07日在 yyy 电子商务平台对授信账户成功还款1元，请前往 yyy 电子商务平台查看详情。
     */
    private String company;
}
