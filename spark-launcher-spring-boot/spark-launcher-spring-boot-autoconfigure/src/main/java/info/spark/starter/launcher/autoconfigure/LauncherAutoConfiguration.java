package info.spark.starter.launcher.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.launcher.SparkStarter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;

/**
 * <p>Description: 全局启动组件, 封装启动相关公共逻辑, 配置优先级最高</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:55
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@ConditionalOnClass(SparkStarter.class)
@EnableConfigurationProperties(LauncherProperties.class)
public class LauncherAutoConfiguration implements SparkAutoConfiguration {
}
