package info.spark.starter.schedule.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.03 10:59
 * @since 1.0.0
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = ScheduleProperties.PREFIX)
public class ScheduleProperties {

    /** Prefix */
    static final String PREFIX = "spark.schedule";
    /** 提供定时任务开关 */
    private boolean enable;
    /** 调度中心部署跟地址 [选填]: 如调度中心集群部署存在多个地址则用逗号分隔. 执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"; 为空则关闭自动注册; */
    private String adminAddresses = "http://localhost:17070";
    /** 执行器通讯TOKEN [选填]: 非空时启用; */
    private String accessToken;
    /** Executor */
    private Executor executor = new Executor();

    /**
         * <p>Description: </p>
     *
     * @author dong4jXxlJobConfig
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.03 11:07
     * @since 1.0.0
     */
    @Data
    public static class Executor {
        /** 执行器AppName [选填]: 执行器心跳注册分组依据; 为空则关闭自动注册 */
        private String appName;
        /** 执行器IP [选填]: 默认为空表示自动获取IP, 多网卡时可手动设置指定IP, 该IP不会绑定Host仅作为通讯实用; 地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"; */
        private String ip;
        /** 执行器端口号 [选填]: 小于等于0则自动获取; 默认端口为9999, 单机部署多个执行器时, 注意要配置不同执行器端口; */
        private int port;
        /** 执行器运行日志文件存储磁盘路径 [选填]: 需要对该路径拥有读写权限; 为空则使用默认路径; */
        private String logPath;
        /** 执行器日志文件保存天数 [选填]:  过期日志自动清理, 限制值大于等于3时生效; 否则, 如-1, 关闭自动清理功能; */
        private int logRetentionDays = 7;
    }
}
