package info.spark.starter.email.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.email.entity.EmailMessage;
import info.spark.starter.email.service.EmailNotifyService;
import info.spark.starter.email.service.impl.EmailNotifyServiceImpl;
import info.spark.starter.util.BeanUtils;
import info.spark.starter.email.config.EmailConfig;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

import javax.activation.MimeType;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.07 22:39
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import(EmailAutoConfiguration.MailSenderPropertiesConfiguration.class)
@ConditionalOnClass(value = {MimeMessage.class, MimeType.class, MailSender.class})
@EnableConfigurationProperties(value = {EmailProperties.class})
public class EmailAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Email notify service
     *
     * @param properties     properties
     * @param javaMailSender java mail sender
     * @return the email notify service
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(name = "spark.notify.email.enabled", matchIfMissing = true)
    public EmailNotifyService<EmailMessage> emailNotifyService(@NotNull EmailProperties properties, JavaMailSender javaMailSender) {
        if (!properties.isEnabled()) {
            return this.defaultEmailService();
        }
        return new EmailNotifyServiceImpl(BeanUtils.copy(properties, EmailConfig.class), javaMailSender);
    }

    /**
     * 避免未开启邮件服务时, 没有 service 导致启动失败的问题
     *
     * @return the email notify service
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(name = "spark.notify.email.enabled", havingValue = "false")
    public EmailNotifyService<EmailMessage> noEmailService() {
        return this.defaultEmailService();
    }

    /**
     * Default email service
     *
     * @return the email notify service
     * @since 1.0.0
     */
    @Contract(value = " -> new", pure = true)
    @NotNull
    private EmailNotifyService<EmailMessage> defaultEmailService() {
        return new EmailNotifyService<EmailMessage>() {

            /**
             * 同步通知
             *
             * @param content 发送内容
             * @return the string
             * @since 1.4.0
             */
            @Override
            public EmailMessage notify(EmailMessage content) {
                log.warn("未开启邮件服务, 请删除 spark.notify.email.enabled=false 或设置为 true. content: [{}]", content);
                return content;
            }
        };
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.4.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.05.12 21:47
     * @since 1.4.0
     */
    @EnableConfigurationProperties(MailProperties.class)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "spark.notify.email.enabled", matchIfMissing = true)
    static class MailSenderPropertiesConfiguration implements SparkAutoConfiguration {

        /**
         * Email auto configuration
         *
         * @param environment environment
         * @since 1.0.0
         */
        MailSenderPropertiesConfiguration(@NotNull ConfigurableEnvironment environment) {
            Properties properties = new Properties();
            properties.setProperty("spring.mail.host", environment.getProperty("spark.notify.email.host", ""));
            properties.setProperty("spring.mail.username", environment.getProperty("spark.notify.email.username", ""));
            properties.setProperty("spring.mail.password", environment.getProperty("spark.notify.email.password", ""));
            ConfigKit.setProperties(environment, properties);
        }

        /**
         * Mail sender
         *
         * @param properties properties
         * @return the java mail sender
         * @since 1.0.0
         */
        @Bean
        @Primary
        JavaMailSenderImpl mailSender(MailProperties properties) {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            this.applyProperties(properties, sender);
            return sender;
        }

        /**
         * Apply properties
         *
         * @param properties properties
         * @param sender     sender
         * @since 1.0.0
         */
        private void applyProperties(@NotNull MailProperties properties, @NotNull JavaMailSenderImpl sender) {
            sender.setHost(properties.getHost());
            if (properties.getPort() != null) {
                sender.setPort(properties.getPort());
            }
            sender.setUsername(properties.getUsername());
            sender.setPassword(properties.getPassword());
            sender.setProtocol(properties.getProtocol());
            if (properties.getDefaultEncoding() != null) {
                sender.setDefaultEncoding(properties.getDefaultEncoding().name());
            }
            if (!properties.getProperties().isEmpty()) {
                sender.setJavaMailProperties(this.asProperties(properties.getProperties()));
            }
        }

        /**
         * As properties
         *
         * @param source source
         * @return the properties
         * @since 1.0.0
         */
        private @NotNull Properties asProperties(Map<String, String> source) {
            Properties properties = new Properties();
            properties.putAll(source);
            return properties;
        }

    }
}
