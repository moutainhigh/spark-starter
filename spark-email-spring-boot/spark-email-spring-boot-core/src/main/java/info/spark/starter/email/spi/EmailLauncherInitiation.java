package info.spark.starter.email.spi;

import info.spark.starter.basic.util.Charsets;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.email.EmailConstant;
import info.spark.starter.processor.annotation.AutoService;

import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * <p>Description: 通过 SPI 加载 mongo 默认配置</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.03 11:41
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class EmailLauncherInitiation implements LauncherInitiation {

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
        return ChainMap.build(2)
            .put("spring.mail.default-encoding", Charsets.UTF_8_NAME)
            .put("spring.mail.port", 465)
            .put("spring.mail.properties.mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            .put("spring.mail.properties.mail.auth", "true");
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return EmailConstant.MODULE_NAME;
    }
}
