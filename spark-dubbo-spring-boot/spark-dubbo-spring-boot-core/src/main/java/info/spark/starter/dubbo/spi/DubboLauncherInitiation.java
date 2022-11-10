package info.spark.starter.dubbo.spi;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.StringUtils;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.core.util.NetUtils;
import info.spark.starter.processor.annotation.AutoService;

import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 通过 SPI 加载 dubbo 默认配置</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 09:29
 * @since 1.0.0
 */
@Slf4j
@AutoService(LauncherInitiation.class)
public class DubboLauncherInitiation implements LauncherInitiation {

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
    @SuppressWarnings("PMD.RemoveCommentedCodeRule")
    public Map<String, Object> launcher(ConfigurableEnvironment env,
                                        String appName,
                                        boolean isLocalLaunch) {

        // see: org.apache.dubbo.common.logger.LoggerFactory
        System.setProperty(ConfigKey.DubboConfigKey.APPLICATION_LOGGER, "slf4j");

        ChainMap chainMap = ChainMap.build(16);

        String localIp = NetUtils.getLocalHost();
        log.info("自动设置 [dubbo.protocol.host = {}] (默认使用优先级最高的网卡), "
                 + "避免注册成本地虚拟机网卡地址导致 dubbo 服务调用失败", localIp);
        chainMap.put(ConfigKey.DubboConfigKey.DUBBO_HOST, localIp);

        // dubbo 服务全部注册到 nacos
        chainMap.put(ConfigKey.DubboConfigKey.REGISTRY_ADDRESS, "spring-cloud://localhost");
        // 通信协议默认 dubbo
        chainMap.put(ConfigKey.DubboConfigKey.PROTOCOL_NAME, "dubbo");
        // 默认不检查 consumer
        chainMap.put(ConfigKey.DubboConfigKey.CONSUMER_CHECK, "false");
        // 设置默认的超时时间
        chainMap.put(ConfigKey.DubboConfigKey.PROVIDER_TIMEOUT, 10000);
        // 开启Consumer参数校验
        chainMap.put(ConfigKey.DubboConfigKey.CONSUMER_VALIDATION, "true");

        // todo-dong4j : (2021.01.31 18:56) [元数据写入 zookeeper (需要添加 zk 依赖)]
        chainMap.put(ConfigKey.DubboConfigKey.METADATA_REPORT_ADDRESS,
                     "nacos://" + ConfigDefaultValue.NACOS_SERVER + "?namespace=" + App.SPARK_NAME_SPACE);
        // 将 dubbo 的 metadata 数据写入到对应的 namespace, 而不是全部写入到 public 中
        chainMap.put("dubbo.metadata-report.group", "dubbo");

        chainMap.put(ConfigKey.DubboConfigKey.DUBBO_CONSUMER_FILTER,
                     "crossJvmParameterPassingFilter");
        chainMap.put(ConfigKey.DubboConfigKey.DUBBO_PROVIDER_FILTER,
                     "-exception,dubboExceptionFilter,crossJvmParameterPassingFilter");
        // 如果存在 START_SPARK_APPLICATION 环境变量, 则表示使用了 spark-launcher 依赖
        Object port = 20880;
        if (StringUtils.isNotBlank(System.getProperty(App.START_SPARK_APPLICATION)) && !ConfigKit.isStartedByJunit()) {
            port = "${range.random.int(28000, 28200)}";
        }
        chainMap.put(ConfigKey.DubboConfigKey.PROTOCOL_PORT, port);
        // 设置随机端口
        return chainMap;
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
        return "spark-starter-dubbo";
    }
}
