package info.spark.starter.security.spi;

import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.processor.annotation.AutoService;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * <p>Description: 加载默认配置 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:21
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class SecurityLauncherInitiation implements LauncherInitiation {

    /**
     * Launcher *
     * todo-dong4j : (2020年03月20日 18:39) [不默认加载, 在启动时通过监听器去加载]
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> launcher(@NotNull ConfigurableEnvironment env,
                                        String appName,
                                        boolean isLocalLaunch) {

        return ChainMap.build(0);

    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 102;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return "spark-starter-security";
    }
}
