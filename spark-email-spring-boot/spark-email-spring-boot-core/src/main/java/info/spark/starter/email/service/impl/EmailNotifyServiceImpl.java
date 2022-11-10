package info.spark.starter.email.service.impl;

import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.email.entity.EmailMessage;
import info.spark.starter.email.service.EmailNotifyService;
import info.spark.starter.util.Tools;
import info.spark.starter.email.config.EmailConfig;
import info.spark.starter.notify.exception.NotifyException;

import org.jetbrains.annotations.Contract;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.07 22:34
 * @since 1.0.0
 */
@Slf4j
public class EmailNotifyServiceImpl implements EmailNotifyService<EmailMessage> {

    /** Email properties */
    private final EmailConfig config;
    /** Java mail sender */
    private final JavaMailSender javaMailSender;

    /**
     * Email notify service
     *
     * @param config         email properties
     * @param javaMailSender java mail sender
     * @since 1.0.0
     */
    @Contract(pure = true)
    public EmailNotifyServiceImpl(EmailConfig config, JavaMailSender javaMailSender) {
        this.config = config;
        this.javaMailSender = javaMailSender;
    }

    /**
     * 同步通知
     *
     * @param message message
     * @return the string
     * @since 1.0.0
     */
    @Override
    public EmailMessage notify(EmailMessage message) {
        log.info("start send mail!");
        Assertions.notNull(message, () -> new NotifyException("message 不允许为空"));
        Assertions.notBlank(message.getSubject(), () -> new NotifyException("message 主题不能为空"));
        Assertions.notBlank(message.getContent(), () -> new NotifyException("message 内容不能为空"));
        this.compareMessage(message);
        switch (message.getEmailType()) {
            case SIMPLE_EMAIL:
                // 发送简单邮件
                this.sendSimpleMail(message);
                break;
            case HTML_COMTENT_EMAIL:
                this.sendHtmlMail(message);
                break;
            case ATTACH_EMAIL:
                // 发送带附件邮件
                this.sendAttachmentsMail(message);
                break;
            default:
                log.error("email type not exists!");
                break;
        }

        return message;
    }

    /**
     * 检查发送配置信息, 如果没有则使用默认配置
     *
     * @param message message
     * @since 1.4.0
     */
    public void compareMessage(EmailMessage message) {
        if (Tools.isBlank(message.getSendFrom())) {
            message.setSendFrom(this.config.getSendFrom());
        }
        if (Tools.isEmpty(message.getSendTo())) {
            message.setSendTo(this.config.getSendTo());
        }
        if (Tools.isEmpty(message.getSendCc())) {
            message.setSendCc(this.config.getSendCc());
        }
    }

    /**
     * 发送简单文本邮件
     *
     * @param emailMessage emailMessage
     * @since 1.0.0
     */
    private void sendSimpleMail(EmailMessage emailMessage) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailMessage.getSendFrom());
        mailMessage.setTo(emailMessage.getSendTo());
        mailMessage.setCc(emailMessage.getSendCc());
        mailMessage.setSubject(emailMessage.getSubject());
        mailMessage.setText(emailMessage.getContent());
        // 异步发送邮件
        this.javaMailSender.send(mailMessage);
        log.info("send email success!");
    }

    /**
     * 发送HTML正文的简单邮件
     *
     * @param emailMessage emailMessage
     * @since 1.0.0
     */
    private void sendHtmlMail(EmailMessage emailMessage) {
        MimeMessage message = this.javaMailSender.createMimeMessage();
        try {
            // true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailMessage.getSendFrom());
            helper.setTo(emailMessage.getSendTo());
            helper.setCc(emailMessage.getSendCc());
            helper.setSubject(emailMessage.getSubject());
            helper.setText(emailMessage.getContent(), true);
            this.javaMailSender.send(message);
            log.info("send html email success!");
        } catch (MessagingException e) {
            log.error("send html email occurs error!", e);
        }
    }

    /**
     * 发送带附件邮件
     *
     * @param emailMessage emailMessage
     * @since 1.0.0
     */
    private void sendAttachmentsMail(EmailMessage emailMessage) {
        Assertions.notEmpty(emailMessage.getFilePaths(), () -> new NotifyException("附件不允许为空"));
        MimeMessage message = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailMessage.getSendFrom());
            helper.setTo(emailMessage.getSendTo());
            helper.setSubject(emailMessage.getSubject());
            helper.setText(emailMessage.getContent(), true);

            for (String filePath : emailMessage.getFilePaths()) {
                FileSystemResource file = new FileSystemResource(new File(filePath));
                String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
                // 添加多个附件可以使用多条 helper.addAttachment(fileName, file)
                helper.addAttachment(fileName, file);
            }

            this.javaMailSender.send(message);
            log.info("send attachment email success!");
        } catch (MessagingException e) {
            log.error("send attachment email occur error!", e);
        }
    }
}
