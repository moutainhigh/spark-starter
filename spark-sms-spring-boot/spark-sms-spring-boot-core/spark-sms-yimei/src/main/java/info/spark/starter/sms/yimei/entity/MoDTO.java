package info.spark.starter.sms.yimei.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 亿美上行短信接口响应实体
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.03 11:27
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class MoDTO implements java.io.Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = -5499346892188466115L;
    /** addSerial */
    private String addSerial;
    /** addSerialRev */
    private String addSerialRev;
    /** channelnumber */
    private String channelnumber;
    /** 手机号 */
    private String mobileNumber;
    /** 响应时间 */
    private String sentTime;
    /** 上行内容 */
    private String smsContent;
}
