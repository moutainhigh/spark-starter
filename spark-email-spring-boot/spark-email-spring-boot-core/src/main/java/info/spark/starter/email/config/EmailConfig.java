package info.spark.starter.email.config;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.05.07 22:45
 * @since 1.0.0
 */
@Data
public class EmailConfig {
    /** 邮件发送方 */
    private String sendFrom;
    /** 邮件接收方 */
    private String[] sendTo;
    /** 抄送 */
    private String[] sendCc;
}
