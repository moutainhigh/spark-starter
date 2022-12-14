package info.spark.starter.mybatis.spi;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.processor.annotation.AutoService;

import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * <p>Description: 通过 SPI 加载 mybatis 默认配置</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:19
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class MybatisLauncherInitiation implements LauncherInitiation {
    /**
     * Launcher *
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> launcher(ConfigurableEnvironment env,
                                        String appName,
                                        boolean isLocalLaunch) {
        return ChainMap.build(8)
            // mybatis-plus
            .put(ConfigKey.MybatisConfigKey.MAPPER_LOCATIONS, "classpath*:/mapper/*.xml")
            .put(ConfigKey.MybatisConfigKey.CONFIGURATION_CALL_SETTERS_ON_NULLS, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MybatisConfigKey.CONFIGURATION_LOG_IMPL, "logger.info.spark.starter.mybatis.NoLogOutImpl")
            .put(ConfigKey.MybatisConfigKey.CONFIGURATION_CACHE_ENABLED, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MybatisConfigKey.CONFIGURATION_MAP_UNDERSCORE_TO_CAMEL_CASE, ConfigDefaultValue.TRUE)
            // 逻辑删除配置, 逻辑已删除值, 逻辑未删除值(默认为 0)
            .put(ConfigKey.MybatisConfigKey.GLOBAL_LOGIC_DELETE_VALUE, 1)
            .put(ConfigKey.MybatisConfigKey.GLOBAL_LOGIC_NOT_DELETE_VALUE, 0)
            // 主键类型, 设置为自增, 要求 DDL 使用 auto_increment
            .put(ConfigKey.MybatisConfigKey.GLOBAL_ID_TYPE, "auto")
            .put(ConfigKey.MybatisConfigKey.GLOBAL_LOGIC_BANNER, "false");

    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return "spark-starter-mybatis";
    }
}
