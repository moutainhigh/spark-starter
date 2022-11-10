package info.spark.starter.sms.qcloud;

import info.spark.starter.sms.SmsConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 10:35
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QcloudConfig extends SmsConfig {

    /** serialVersionUID */
    private static final long serialVersionUID = -9014137844494728983L;
    /** 签名: 签名需要在短信控制台中申请,另外签名参数使用的是`签名内容`,而不是`签名ID` */
    private String sign;
}
