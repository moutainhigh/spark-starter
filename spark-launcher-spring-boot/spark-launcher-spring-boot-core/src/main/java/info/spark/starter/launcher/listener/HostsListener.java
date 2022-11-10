package info.spark.starter.launcher.listener;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.HostUtils;
import info.spark.starter.common.SparkApplicationListener;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.launcher.exception.LauncherException;
import info.spark.starter.util.ThreadUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 本地开发时自动写入/更新开发相关的 hosts 配置 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.12 16:05
 * @since 1.7.0
 * @deprecated 从 2022.1.1 开始 不再更新开发者本地的 hosts 文件, 自己去配置吧
 */
@Slf4j
@Deprecated
public class HostsListener implements SparkApplicationListener {

    /**
     * 优先级最高
     *
     * @return the order
     * @since 1.5.0
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * On application environment prepared event
     *
     * @param event event
     * @since 1.5.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Runner.executeAtFirst(this.key(event, this.getClass()),
                              () -> this.load(environment.getProperty(ConfigKey.SpringConfigKey.PROFILE_ACTIVE)));
    }


    /**
     * Load
     *
     * @param profile profile
     * @since 1.7.0
     */
    private void load(String profile) {
        if (ConfigKit.isLocalLaunch()) {
            try {
                this.loadHosts("includes/hosts.properties");
            } catch (LauncherException e) {
                this.loadHosts("dns-default/hosts.properties");
            }
        }
    }

    /**
     * Load hosts
     *
     * @param propertiesFileName properties file name
     * @since 1.7.0
     */
    private void loadHosts(String propertiesFileName) {
        InputStream inputStream;
        try {
            // 先加载本地文件, 开发环境在 target 目录下, 服务器部署环境在 config 目录下
            Resource resource = ConfigKit.getResource(propertiesFileName);
            inputStream = resource.getInputStream();
        } catch (Exception e) {
            // 从 jar 中加载
            inputStream = HostsListener.class.getClassLoader().getResourceAsStream(propertiesFileName);
        }

        if (inputStream == null) {
            throw new LauncherException("Fail to find " + propertiesFileName + " on classpath!");
        }

        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            ThreadUtils.submit(() -> writeToHosts(properties));
        } catch (Exception e) {
            throw new LauncherException(String.format("Fail to loadDnsCacheConfig from %s, cause: %s", propertiesFileName, e), e);
        }
    }

    /**
     * 将 hosts.properties 内的配置写入到本地 hosts, 如果存在相同记录则更新
     *
     * @param cacheProperties cache properties
     * @since 1.7.0
     */
    private static void writeToHosts(Properties cacheProperties) {
        cacheProperties.forEach((k, v) -> HostUtils.updateHost(String.valueOf(v), String.valueOf(k)));
    }

}
