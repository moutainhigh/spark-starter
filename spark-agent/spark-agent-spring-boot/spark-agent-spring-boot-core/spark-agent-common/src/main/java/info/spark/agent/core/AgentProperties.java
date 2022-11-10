package info.spark.agent.core;

import com.google.common.collect.Maps;

import info.spark.starter.basic.constant.ConfigDefaultValue;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;
import java.util.Map;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.5
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.05 09:53
 * @since 1.0.0
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = AgentProperties.PREFIX)
public class AgentProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.agent";

    /** Endpoint */
    private EndpointConfig endpoint = new EndpointConfig();
    /** 是否开启 undertow 容器的请求日志(输出详细的请求和响应到日志,不是 undertow_log/access.log) */
    private boolean enableContainerLog = Boolean.FALSE;
    /** 是否开启 http2 支持 */
    private boolean enableHttp2 = Boolean.FALSE;
    /** 文件上传配置 */
    private Multipart multipart = new Multipart();

    /**
         * <p>Description: agent 全局配置 </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.24 15:00
     * @since 1.0.0
     */
    @Data
    public static class EndpointConfig {

        /** 客户端密钥, clientId:secret */
        private Map<String, String> appSecretKeys = Maps.newHashMap();
        /** 全局重复提交检查 (如果为 false, 则不再检查每个 agent 服务是否需要检查) */
        private boolean enableReplyCheck = Boolean.FALSE;
        /** 全局签名检查 (如果为 false, 则不再检查每个 agent 服务是否需要检查) */
        private boolean enableSignCheck = Boolean.FALSE;
        /** Nonce expired time */
        private Duration nonceExpiredTime = Duration.ofMillis(15 * 1000L);
        /** 开启快速失败将在启动时检查 agent service 写法, 错误将抛出异常导致启动失败 */
        private boolean enableFailFast = Boolean.TRUE;
        /** 开启业务端是否需要开启 ApiExtend 对象中 ExpandIds tenantId、clientId 必传校验 */
        private boolean enableExpandIdsCheck = Boolean.FALSE;
        /**
         * 默认 15 秒.
         * 注意: agent client 的请求超时时间必须大于 agent service 的处理超时, 即 spark.gateway.read-timeout > spark.agent.endpoint.request-timeout,
         * 否则会进行请求重试
         */
        private Duration requestTimeout = Duration.ofMillis(15 * 1000L);
    }

    /**
         * <p>Description: 文件上传配置 </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.07.13 17:32
     * @since 1.8.0
     */
    @Data
    public static class Multipart {
        /** Location */
        private String location = ConfigDefaultValue.CONTAINER_LOCATION;
    }
}
