package info.spark.starter.agent.autoconfigure;

import info.spark.starter.endpoint.EndpointHandlerMapping;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.30 19:37
 * @since 2022.1.1
 */
public interface EndpointHandlerMappingCustomizer {

    /**
     * 用于自定义 {@link EndpointHandlerMapping} 实例的回调
     *
     * @param endpointHandlerMapping endpoint handler mapping
     * @since 2022.1.1
     */
    void customize(EndpointHandlerMapping endpointHandlerMapping);

}
