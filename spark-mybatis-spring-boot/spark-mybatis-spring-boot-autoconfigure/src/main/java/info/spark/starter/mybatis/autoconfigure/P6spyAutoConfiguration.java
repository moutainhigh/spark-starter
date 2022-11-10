package info.spark.starter.mybatis.autoconfigure;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.exception.StarterException;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.mybatis.plugins.PerformanceInterceptor;

import com.p6spy.engine.spy.P6ModuleManager;
import com.p6spy.engine.spy.P6SpyDriver;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.option.SystemProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author zhubo
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.15 10:44
 * @since 1.7.0
 */
@Slf4j
@SuppressWarnings("all")
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(P6SpyDriver.class)
@EnableConfigurationProperties(P6spyProperties.class)
@ConditionalOnProperty(name = ConfigKey.DruidConfigKey.DRIVER_CLASS, havingValue = "com.p6spy.engine.spy.P6SpyDriver")
@ConditionalOnMissingBean(PerformanceInterceptor.class)
public class P6spyAutoConfiguration implements SparkAutoConfiguration {

    /**
     * p6spy auto configuration
     *
     * @param environment environment
     * @param properties  properties
     * @since 1.7.1
     */
    public P6spyAutoConfiguration(Environment environment, P6spyProperties properties) {
        final String property = environment.getProperty(ConfigKey.SpringConfigKey.DATASOURCE_URL);
        if (!property.contains("jdbc:p6spy:")) {
            throw new StarterException("[{}] 配置错误, 正确配置: [jdbc:p6spy:db-type]", ConfigKey.SpringConfigKey.DATASOURCE_URL);
        }

        Map<String, String> defaults = P6SpyOptions.getActiveInstance().getDefaults();
        Field[] fields = P6spyProperties.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                Object value = field.get(properties);
                String propertiesName = SystemProperties.P6SPY_PREFIX.concat(fieldName);
                // 优先使用自定义配置的信息
                if (environment.containsProperty(propertiesName)) {
                    String systemPropertyValue = environment.getProperty(propertiesName, defaults.get(fieldName));
                    defaults.put(fieldName, systemPropertyValue);
                    continue;
                }
                // 若配置文件中没有自定义配置，则使用组件 P6spyProperties 中的默认值
                if (value != null) {
                    defaults.put(fieldName, String.valueOf(value));
                }

            } catch (IllegalAccessException e) {
                // 不影响启动
                log.warn("初始化 p6spy 参数异常: [{}]", e.getMessage());
                return;
            }
        }
        P6SpyOptions.getActiveInstance().load(defaults);
        P6ModuleManager.getInstance().reload();
    }

}

