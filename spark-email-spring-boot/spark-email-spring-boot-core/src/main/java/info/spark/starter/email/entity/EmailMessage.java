package info.spark.starter.email.entity;

import info.spark.starter.email.enums.EmailMessageType;
import info.spark.starter.notify.AbstractMessage;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.07 21:31
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmailMessage extends AbstractMessage<String> {
    /** serialVersionUID */
    private static final long serialVersionUID = 6297518434607557350L;
    /** 邮件发送方: 非必填, 默认系统配置的 */
    private String sendFrom;
    /** 邮件接收方: 非必填, 默认系统配置的 */
    private String[] sendTo;
    /** 抄送: 非必填, 默认系统配置的 */
    private String[] sendCc;
    /** 邮件主题 */
    private String subject;
    /** 邮件类型 */
    @Builder.Default
    private EmailMessageType emailType = EmailMessageType.SIMPLE_EMAIL;
    /** 邮件正文内容 */
    private String content;
    /** 附件地址 */
    private List<String> filePaths;

    /**
     * Email message
     *
     * @param subject subject
     * @param content content
     * @since 1.0.0
     */
    public EmailMessage(String subject, String content) {
        this.subject = subject;
        this.content = content;
        this.emailType = EmailMessageType.SIMPLE_EMAIL;
    }

    /**
     * Email message
     *
     * @param emailType email type
     * @param subject   subject
     * @param content   content
     * @since 1.0.0
     */
    public EmailMessage(EmailMessageType emailType, String subject, String content) {
        this(subject, content);
        this.emailType = emailType;
    }

    /**
     * Email message
     *
     * @param subject   subject
     * @param content   content
     * @param filePaths file path
     * @since 1.0.0
     */
    public EmailMessage(String subject, String content, List<String> filePaths) {
        this(content, subject);
        this.filePaths = filePaths;
        this.emailType = EmailMessageType.ATTACH_EMAIL;
    }
}
