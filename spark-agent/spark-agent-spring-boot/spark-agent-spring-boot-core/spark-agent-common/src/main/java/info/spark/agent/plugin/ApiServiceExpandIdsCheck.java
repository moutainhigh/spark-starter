package info.spark.agent.plugin;

import info.spark.agent.entity.ApiServiceHeader;
import info.spark.agent.entity.ApiServiceRequest;
import info.spark.agent.exception.AgentServiceException;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 如果开启了 spark.agent.endpoint.enableExpandIdsCheck; 参数必传 </p>
 *
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.04.14 10:37
 * @since 1.8.0
 */
public interface ApiServiceExpandIdsCheck extends Plugin {

    /**
     * Check
     *
     * @param apiServiceHeader  api service header
     * @param apiServiceRequest api service request
     * @throws AgentServiceException agent service exception
     * @since 1.8.0
     */
    void check(@NotNull ApiServiceHeader apiServiceHeader, @NotNull ApiServiceRequest apiServiceRequest) throws AgentServiceException;

}
