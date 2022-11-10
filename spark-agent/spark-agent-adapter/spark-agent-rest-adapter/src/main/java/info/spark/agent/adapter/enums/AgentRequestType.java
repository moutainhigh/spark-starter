package info.spark.agent.adapter.enums;

import info.spark.agent.adapter.client.AgentClient;

/**
 * <p>Description: AgentClient 接口调用类型 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.14 20:09
 * @since 1.7.0
 */
public enum AgentRequestType {

    /**
     * 直接调用如下接口:
     * {@link AgentClient#get(java.lang.String)}
     * {@link AgentClient#post(java.lang.String)}
     * {@link AgentClient#delete(java.lang.String)}
     * {@link AgentClient#put(java.lang.String)}
     * {@link AgentClient#patch(java.lang.String)}
     */
    HTTP_METHOD,
    /** 直接调用 {@link AgentClient#request} */
    REQUEST_MENTOD,
    /** 直接调用 {@link AgentClient#api} */
    API_METHOD,
}
