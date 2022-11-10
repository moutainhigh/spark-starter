package info.spark.agent.adapter;

import info.spark.agent.constant.AgentConstant;

/**
 * <p>Description:  </p>获取 tenantId 的接口, 由业务端实现, 会在调用 agent 服务时写入到 {@link AgentConstant#X_AGENT_TENANTID} </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 17:12
 * @since 1.8.0
 */
public interface TenantService {

    /**
     * Gets tenant id *
     *
     * @return the tenant id
     * @since 1.8.0
     */
    default Long getTenantId() {
        return null;
    }

}
