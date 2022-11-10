package info.spark.starter.logsystem.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.logsystem.LogPrintStream;
import info.spark.starter.logsystem.handler.AutoChangeLogLevelEventHandler;
import info.spark.starter.logsystem.handler.ManualChangeLogLevelEventHandler;
import info.spark.starter.logsystem.factory.LogStorageFactory;
import info.spark.starter.logsystem.factory.LogStorageFactoryAdapter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:47
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@ConditionalOnClass(LogPrintStream.class)
@EnableConfigurationProperties(LogSystemProperties.class)
public class LogSystemAutoConfiguration implements SparkAutoConfiguration {

    /**
     * 当环境配置改变时 自动检查是否需要修改日志等级
     *
     * @return the logging level rebinder
     * @since 1.6.0
     */
    @Contract(value = " -> new", pure = true)
    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.context.environment.EnvironmentChangeEvent")
    public static @NotNull AutoChangeLogLevelEventHandler autoChangeLogLevelEventHandler() {
        return new AutoChangeLogLevelEventHandler();
    }

    /**
     * 监听 ChangeLogLevelEvent 以动态修改日志等级 (手动修改事件)
     *
     * @return the logging level refresh event handler
     * @since 1.6.0
     */
    @Bean
    public ManualChangeLogLevelEventHandler manualChangeLogLevelEventHandler() {
        return new ManualChangeLogLevelEventHandler();
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.31 11:40
     * @since 1.0.0
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "info.spark.starter.logsystem.factory.LogStorageFactory")
    static class LogSystemRecordAutoConfiguration implements SparkAutoConfiguration {
        /**
         * Log storage factory log storage factory
         *
         * @return the log storage factory
         * @since 1.0.0
         */
        @Bean
        @ConditionalOnMissingBean
        public LogStorageFactory logStorageFactory() {
            log.warn("未配置任何日志服务, 将不会发送日志, 请在业务端配置.");
            return new LogStorageFactoryAdapter() {
            };
        }
    }
}
