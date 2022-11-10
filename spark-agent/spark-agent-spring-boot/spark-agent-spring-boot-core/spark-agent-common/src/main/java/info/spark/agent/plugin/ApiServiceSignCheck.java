package info.spark.agent.plugin;

import info.spark.agent.entity.ApiServiceHeader;
import info.spark.agent.entity.ApiServiceRequest;
import info.spark.agent.exception.AgentServiceException;

/**
 * <p>Description: 接口参数签名检查 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.18 20:18
 * @since 1.6.0
 */
public interface ApiServiceSignCheck extends Plugin {
    /**
     * Check
     *
     * @param apiServiceHeader  api service header
     * @param apiServiceRequest api service request
     * @throws AgentServiceException agent service exception
     * @since 1.6.0
     */
    void check(ApiServiceHeader apiServiceHeader, ApiServiceRequest apiServiceRequest) throws AgentServiceException;
}
