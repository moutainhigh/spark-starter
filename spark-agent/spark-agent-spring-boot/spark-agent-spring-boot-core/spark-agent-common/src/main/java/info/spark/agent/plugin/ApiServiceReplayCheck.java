package info.spark.agent.plugin;

import info.spark.agent.entity.ApiServiceHeader;
import info.spark.agent.entity.ApiServiceRequest;
import info.spark.agent.exception.AgentServiceException;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 重放攻击检查 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.19 14:44
 * @since 1.6.0
 */
public interface ApiServiceReplayCheck extends Plugin {

    /**
     * Check
     *
     * @param apiServiceHeader  api service header
     * @param apiServiceRequest api service request
     * @throws AgentServiceException agent service exception
     * @since 1.6.0
     */
    void check(@NotNull ApiServiceHeader apiServiceHeader, @NotNull ApiServiceRequest apiServiceRequest) throws AgentServiceException;

}
