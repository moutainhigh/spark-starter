package info.spark.starter.schedule.autoconfigure;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.core.util.NetUtils;
import info.spark.starter.schedule.spi.ScheduleLauncherInitiation;
import info.spark.starter.util.StringUtils;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.03 10:59
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("all")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ScheduleProperties.class)
@ConditionalOnClass(ScheduleLauncherInitiation.class)
public class ScheduleAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Xxl job executor xxl job spring executor
     *
     * @param scheduleProperties schedule properties
     * @return the xxl job spring executor
     * @since 1.0.0
     */
    @Bean(destroyMethod = "destroy")
    @ConditionalOnProperty(value = ConfigKey.ScheduleConfigKey.ENABLE, havingValue = "true", matchIfMissing = true)
    public XxlJobSpringExecutor xxlJobExecutor(@NotNull ScheduleProperties scheduleProperties) {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(scheduleProperties.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(scheduleProperties.getExecutor().getAppName());
        String ip = scheduleProperties.getExecutor().getIp();
        if (StringUtils.isBlank(ip)) {
            ip = NetUtils.getLocalHost();
        }
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(scheduleProperties.getExecutor().getPort());
        xxlJobSpringExecutor.setAccessToken(scheduleProperties.getAccessToken());

        String logPath = scheduleProperties.getExecutor().getLogPath();
        logPath = StringUtils.isBlank(logPath) ? ScheduleLauncherInitiation.DEFAULT_LOG_PATH : logPath;
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(scheduleProperties.getExecutor().getLogRetentionDays());
        return xxlJobSpringExecutor;
    }

}
