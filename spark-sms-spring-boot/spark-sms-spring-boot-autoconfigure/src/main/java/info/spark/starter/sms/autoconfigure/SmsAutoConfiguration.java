package info.spark.starter.sms.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.util.BeanUtils;
import info.spark.starter.sms.SmsConfig;
import info.spark.starter.sms.SmsService;
import info.spark.starter.sms.aliyun.AliyunConfig;
import info.spark.starter.sms.aliyun.AliyunSmsClient;
import info.spark.starter.sms.aliyun.AliyunSmsMessage;
import info.spark.starter.sms.aliyun.service.AliyunSmsService;
import info.spark.starter.sms.qcloud.QcloudConfig;
import info.spark.starter.sms.qcloud.QcloudSmsClient;
import info.spark.starter.sms.qcloud.QcloudSmsMessage;
import info.spark.starter.sms.qcloud.service.QcloudSmsService;
import info.spark.starter.sms.yimei.YimeiConfig;
import info.spark.starter.sms.yimei.YimeiSmsClient;
import info.spark.starter.sms.yimei.YimeiSmsMessage;
import info.spark.starter.sms.yimei.service.YimeiSmsService;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 10:24
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SmsConfig.class)
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Aliyun sms service sms service
     *
     * @param properties properties
     * @return the sms service
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnClass(name = "info.spark.starter.sms.aliyun.AliyunConfig")
    public SmsService<AliyunSmsMessage> aliyunSmsService(@NotNull SmsProperties properties) {
        log.debug("classpath 存在 [aliyun.info.spark.starter.sms.AliyunConfig], 注入 AliyunSmsService");
        AliyunSmsClient client = new AliyunSmsClient(BeanUtils.copy(properties.getAliyun(), AliyunConfig.class));
        return new AliyunSmsService(client);
    }

    /**
     * Qcloud sms service sms service
     *
     * @param properties properties
     * @return the sms service
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnClass(name = "info.spark.starter.sms.qcloud.QcloudConfig")
    public SmsService<QcloudSmsMessage> qcloudSmsService(@NotNull SmsProperties properties) {
        log.debug("classpath 存在 [qcloud.info.spark.starter.sms.QcloudConfig], 注入 QcloudSmsService");
        QcloudSmsClient client = new QcloudSmsClient(BeanUtils.copy(properties.getQcloud(), QcloudConfig.class));
        return new QcloudSmsService(client);
    }

    /**
     * Yimei sms service sms service
     *
     * @param properties properties
     * @return the sms service
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnClass(name = "info.spark.starter.sms.yimei.YimeiConfig")
    public SmsService<YimeiSmsMessage> yimeiSmsService(@NotNull SmsProperties properties) {
        log.debug("classpath 存在 [info.spark.starter.sms.yimei.YimeiConfig], 注入 YimeiSmsService");
        YimeiSmsClient client = new YimeiSmsClient(BeanUtils.copy(properties.getYimei(), YimeiConfig.class));
        return new YimeiSmsService(client);
    }

    /**
     * 如果未添加其他短信服务，则使用默认短信服务, 避免注入 @SmsService 时出现异常情况.
     *
     * @return the sms service
     * @since 2022.1.1
     */
    @Bean
    @ConditionalOnMissingBean(SmsService.class)
    public SmsService<?> defalutSmsService() {
        return sms -> log.info("短信服务默认实现: {}", sms);
    }
}
