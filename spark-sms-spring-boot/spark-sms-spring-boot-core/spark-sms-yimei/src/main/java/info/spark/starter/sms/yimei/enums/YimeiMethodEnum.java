package info.spark.starter.sms.yimei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.03 11:11
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum YimeiMethodEnum  {
    /** SEND_SMS */
    SEND_SMS("/sdkproxy/sendsms.action"),
    /** GET_MO */
    GET_MO("/sdkproxy/getmo.action");
    /** method */
    private final String method;
}
