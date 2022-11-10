package info.spark.agent.plugin.impl;

import info.spark.agent.constant.AgentConstant;
import info.spark.agent.entity.ApiServiceHeader;
import info.spark.agent.entity.ApiServiceRequest;
import info.spark.agent.exception.AgentCodes;
import info.spark.agent.exception.AgentServiceException;
import info.spark.agent.plugin.ApiServiceExpandIdsCheck;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * <p>Description: 如果开启了 spark.agent.endpoint.enableExpandIdsCheck; 参数必传  </p>
 *
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.04.14 10:48
 * @since 1.8.0
 */
public class DefaultApiServiceExpandIdsCheck implements ApiServiceExpandIdsCheck {

    /** Enable expand ids check */
    private final boolean enableExpandIdsCheck;

    /**
     * Default api service expand ids check
     *
     * @param enableExpandIdsCheck enable expand ids check
     * @since 1.8.0
     */
    @Contract(pure = true)
    public DefaultApiServiceExpandIdsCheck(boolean enableExpandIdsCheck) {
        this.enableExpandIdsCheck = enableExpandIdsCheck;
    }

    /**
     * Check
     *
     * @param apiServiceHeader  api service header
     * @param apiServiceRequest api service request
     * @throws AgentServiceException agent service exception
     * @since 1.8.0
     */
    @Override
    public void check(@NotNull ApiServiceHeader apiServiceHeader,
                      @NotNull ApiServiceRequest apiServiceRequest) throws AgentServiceException {
        if (this.enableExpandIdsCheck) {
            Map<String, String> headers = apiServiceHeader.getHeaders();

            AgentCodes.EXPAND_IDS_ABSENT.notBlank(headers.get(AgentConstant.X_AGENT_TENANTID), AgentConstant.X_AGENT_TENANTID);
            AgentCodes.EXPAND_IDS_ABSENT.notBlank(headers.get(AgentConstant.X_AGENT_APPID), AgentConstant.X_AGENT_APPID);
        }
    }
}
