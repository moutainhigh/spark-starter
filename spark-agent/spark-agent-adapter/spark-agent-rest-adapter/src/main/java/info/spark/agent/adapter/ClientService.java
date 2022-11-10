package info.spark.agent.adapter;

import info.spark.agent.constant.AgentConstant;

/**
 * <p>Description: 获取 clientId 的接口, 由业务端实现, 会在调用 agent 服务时写入到 {@link AgentConstant#X_AGENT_APPID}  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.08 17:37
 * @since 1.8.0
 */
public interface ClientService {

    /**
     * Gets client id *
     *
     * @return the client id
     * @since 1.8.0
     */
    default String getClientId() {
        return "";
    }

    /**
     * 业务端 agent 层实现此接口根据 clientId 获取对应的 secret
     *
     * @param clientId client id
     * @return the string
     * @since 1.0.0
     */
    default String getSecret(String clientId) {
        return "";
    }
}
