package info.spark.agent.adapter.config;

import info.spark.starter.basic.constant.ConfigKey;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.5
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.10 12:44
 * @since 1.0.0
 */
@Data
@Slf4j
public class AgentRestProperties {
    /**
     * Agent rest properties
     *
     * @since 1.6.0
     */
    public AgentRestProperties() {
        log.debug("加载 agent 配置: {}", AgentRestProperties.class);
    }

    /** 服务地址 (enable-router 为 false 时, 将直接调用指定服务, 如果为 true, 请配置 gateway 地址, 会通过 serviceName 路由到指定服务) */
    @Value("#{'${" + ConfigKey.AgentConfigKey.GATEWAY_SERVICE_LIST
           + ":${" + ConfigKey.AgentConfigKey.RIBBON_SERVICE_LIST + ":}}'}")
    public String servers;

    /** get 请求 request 最大长度限制 */
    @Value("#{'${" + ConfigKey.AgentConfigKey.GATEWAY_REQUEST_MAX_LINE_LENGTH
           + ":${" + ConfigKey.AgentConfigKey.REQUEST_MAX_LINE_LENGTH + ":1024}}'}")
    public Long requestMaxLineLength;

    /** http 请求超时时间 (毫秒) 默认不限制, 此时服务端默认 15 秒, 如果配置将以应用端配置优先 */
    @Value("#{'${" + ConfigKey.AgentConfigKey.GATEWAY_READ_TIMEOUT
           + ":${" + ConfigKey.AgentConfigKey.READ_TIMEOUT + ":0}}'}")
    public Long readTimeout;

    /** Ribbon 连接超时时间 (毫秒) 默认 5 秒 */
    @Value("#{'${" + ConfigKey.AgentConfigKey.GATEWAY_CONNECT_TIMEOUT
           + ":${" + ConfigKey.AgentConfigKey.CONNECT_TIMEOUT + ":5000}}'}")
    public Long connectTimeout;

    /**
     * 是否使用网关路由请求: 此项配置主要用于开发时使用, 默认为 true,
     * 适用于本地开发, 将忽略 serviceName, 设置为 true 时将会把 serviceName 用于网关路由
     * false: 配置 spark.gateway.servers=127.0.0.1:18080, 处理后的 url = http://127.0.0.1:18080/agent
     * true: 配置 spark.gateway.servers=127.0.0.1:18080, 处理后的 url = http://127.0.0.1:18080/${serviceName}/agent
     */
    @Value("#{'${" + ConfigKey.AgentConfigKey.GATEWAY_REST_ENABLE_ROUTER
           + ":${" + ConfigKey.AgentConfigKey.REST_ENABLE_ROUTER + ":true}}'}")
    public boolean enableRouter;

    /** 分布式 id 生成器需要的 machineId */
    @Value("${spark.machine-id:0}")
    public Long machineId;

    /** 是否启用自定义 endpoint */
    @Value("${" + ConfigKey.AgentConfigKey.GATEWAY_REST_ENABLE_ENDPOINT + ":true}")
    public boolean enableEndpoint;

}
