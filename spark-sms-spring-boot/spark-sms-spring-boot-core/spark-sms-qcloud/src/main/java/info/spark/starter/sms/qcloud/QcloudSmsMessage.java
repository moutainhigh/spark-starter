package info.spark.starter.sms.qcloud;

import info.spark.starter.sms.SmsMessage;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 18:01
 * @since 1.0.0
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class QcloudSmsMessage extends SmsMessage<String> {
    /** serialVersionUID */
    private static final long serialVersionUID = 1643582039368581262L;

    /** 短信模板ID, 需要在短信应用中申请 */
    private Integer templateId;
    /** 占位符参数 */
    private ArrayList<String> params;
}
