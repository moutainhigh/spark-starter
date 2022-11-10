package info.spark.starter.sms.yimei;

import info.spark.starter.sms.SmsConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 10:35
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class YimeiConfig extends SmsConfig {
    /** serialVersionUID */
    private static final long serialVersionUID = 5876357203418539643L;

    /** 亿美短信密码 */
    private String password;
    /** 亿美短信URL */
    private String url;
}
