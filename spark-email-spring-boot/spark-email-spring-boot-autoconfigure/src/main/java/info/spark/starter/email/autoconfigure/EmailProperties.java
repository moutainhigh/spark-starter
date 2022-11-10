package info.spark.starter.email.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <p>Description: 邮件发送配置信息</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.07 22:43
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = EmailProperties.PREFIX)
public class EmailProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.notify.email";
    /** 等同于 spring.mail.host */
    private String host;
    /** 等同于 spring.mail.username */
    private String username;
    /** 等同于 spring.mail.password */
    private String password;
    /** 邮件发送方 */
    private String sendFrom;
    /** 邮件接收方 */
    private String[] sendTo;
    /** 抄送 */
    private String[] sendCc = new String[] {};
    /** 默认发送邮件 */
    private boolean enabled = true;
}
