package info.spark.starter.dingtalk.spi;

import info.spark.starter.processor.annotation.AutoService;

import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.dingtalk.DingtalkConstant;
import info.spark.starter.util.core.support.ChainMap;

import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.05 00:27
 * @since 2.1.0
 */
@AutoService(LauncherInitiation.class)
public class DingtalkLauncherInitiation implements LauncherInitiation {

    /**
     * Launcher
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 2.1.0
     */
    @Override
    public Map<String, Object> launcher(ConfigurableEnvironment env,
                                        String appName,
                                        boolean isLocalLaunch) {
        return ChainMap.build(2);
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 2.1.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 2.1.0
     */
    @Override
    public String getName() {
        return DingtalkConstant.MODULE_NAME;
    }
}
