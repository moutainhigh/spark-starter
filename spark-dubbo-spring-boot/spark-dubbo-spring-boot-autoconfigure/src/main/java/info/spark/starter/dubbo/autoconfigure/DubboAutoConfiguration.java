package info.spark.starter.dubbo.autoconfigure;

import com.google.common.collect.Lists;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.basic.util.StringUtils;
import info.spark.starter.common.context.EarlySpringContext;
import info.spark.starter.common.enums.LibraryEnum;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.common.util.StartUtils;
import info.spark.starter.dubbo.check.RpcCheck;
import info.spark.starter.dubbo.check.RpcCheckImpl;
import info.spark.starter.dubbo.listener.DubboRegistryInvokerRebuildListener;
import info.spark.starter.dubbo.spi.DubboLauncherInitiation;

import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 使用此组件, 依赖了 feign 后可直接通过 dubbo rest 方式调用</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 01:25
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = {ApplicationConfig.class, DubboLauncherInitiation.class})
@EnableConfigurationProperties(DubboProperties.class)
public class DubboAutoConfiguration implements SparkAutoConfiguration {

    /**
     * 获取 dubbo 的 port
     *
     * @see NetUtils#getAvailablePort() 获取系统可用端口
     * @since 1.4.0
     */
    @Override
    public void execute() {
        Map<String, ProtocolConfig> beansOfType = EarlySpringContext.getApplicationContext().getBeansOfType(ProtocolConfig.class);
        List<StartUtils.CustomInfo> dubboInfos = Lists.newArrayList();
        beansOfType.forEach((k, v) -> {
            String ip = v.getHost();
            if (StringUtils.isBlank(ip)) {
                ip = NetUtils.getLocalHost();
            }
            String finalIp = ip;
            //noinspection HttpUrlsUsage
            dubboInfos.add(() -> StartUtils.padding(LibraryEnum.DUBBO.getName())
                                 + "http://"
                                 + finalIp
                                 + StringPool.COLON
                                 + v.getPort());
            // 如果是随机端口， 为避免使用 ConfigKit.getPort() 获取的端口还是随机的， 这里重写端口号
            ConfigKit.setSystemProperties(ConfigKey.DubboConfigKey.PROTOCOL_PORT, String.valueOf(v.getPort()));
        });

        StartUtils.addCustomInfo(dubboInfos);
    }

    /**
     * Dubbo registry invoker rebuild listener
     *
     * @return the dubbo registry invoker rebuild listener
     * @since 1.5.0
     */
    @Bean
    public DubboRegistryInvokerRebuildListener dubboRegistryInvokerRebuildListener() {
        return new DubboRegistryInvokerRebuildListener();
    }

    /**
     * Rpc check
     *
     * @return the rpc check
     * @since 1.5.0
     */
    @Bean
    public RpcCheck rpcCheck() {
        return new RpcCheckImpl();
    }
}
