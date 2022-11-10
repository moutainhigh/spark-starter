package info.spark.agent.adapter.client;

import info.spark.agent.adapter.registrar.AgentClientProxy;

/**
 * <p>Description: 使用代理动态创建实现类 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.21 18:17
 * @see AgentClientProxy#invoke
 * @since 1.6.0
 */
public interface AgentClient {

    /**
     * Get
     *
     * @param apiName api name
     * @return the agent request builder
     * @since 1.6.0
     * @deprecated 请使用 {@link AgentClient#api(java.lang.String)}
     */
    @Deprecated
    AgentRequestBuilder get(String apiName);

    /**
     * Post
     *
     * @param apiName api name
     * @return the agent request builder
     * @since 1.6.0
     * @deprecated 请使用 {@link AgentClient#api(java.lang.String)}
     */
    @Deprecated
    AgentRequestBuilder post(String apiName);

    /**
     * Delete
     *
     * @param apiName api name
     * @return the agent request builder
     * @since 1.6.0
     * @deprecated 请使用 {@link AgentClient#api(java.lang.String)}
     */
    @Deprecated
    AgentRequestBuilder delete(String apiName);

    /**
     * Put
     *
     * @param apiName api name
     * @return the agent request builder
     * @since 1.6.0
     * @deprecated 请使用 {@link AgentClient#api(java.lang.String)}
     */
    @Deprecated
    AgentRequestBuilder put(String apiName);

    /**
     * Patch
     *
     * @param apiName api name
     * @return the agent request builder
     * @since 1.6.0
     * @deprecated 请使用 {@link AgentClient#api(java.lang.String)}
     */
    @Deprecated
    AgentRequestBuilder patch(String apiName);

    /**
     * 使用方无需关注使用什么请求，直接指定 apiName, 组件根据入参情况自动选择 HttpMethod
     *
     * @param apiName api name
     * @return the agent request builder
     * @since 1.7.0
     */
    AgentRequestBuilder api(String apiName);

    /**
     * 是否需要签名
     *
     * @return the agent request builder
     * @since 1.8.0
     */
    AgentRequestBuilder needSignature();

    /**
     * 使用业务端已组装好的 request, 但是会覆盖 service name
     *
     * @param request request
     * @return the agent request builder
     * @since 1.7.0
     */
    AgentRequestBuilder request(AgentRequest request);

    /**
     * 手动设置 endpoint
     *
     * @param endpoint endpoint
     * @since 1.7.1
     */
    default void setEndpoint(String endpoint) {}

    /**
     * Gets endpoint *
     *
     * @return the endpoint
     * @since 1.7.1
     */
    default String getEndpoint() {
        return "";
    }

    /**
     * Gets service name *
     *
     * @return the service name
     * @since 1.7.0
     */
    default String serviceName() {
        return "";
    }

    /**
     * 设置 tenantId
     *
     * @param tenantId tenant id
     * @since 1.8.0
     */
    default void setTenantId(Long tenantId) { }

}
