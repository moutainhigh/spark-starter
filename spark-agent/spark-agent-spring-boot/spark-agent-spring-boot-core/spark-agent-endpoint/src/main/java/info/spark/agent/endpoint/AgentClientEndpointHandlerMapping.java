package info.spark.agent.endpoint;

import info.spark.starter.util.CollectionUtils;
import info.spark.starter.endpoint.EndpointHandlerMapping;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 自动将被 @AgentClientEndpoint 标识的类注册为一个 controller  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 03:15
 * @since 1.0.0
 */
@Slf4j
public class AgentClientEndpointHandlerMapping extends EndpointHandlerMapping {

    /** Interceptors */
    private final List<Object> interceptors = new ArrayList<>();

    /**
     * Agent client endpoint handler mapping
     *
     * @param clientHttpRequestInterceptors client http request interceptors
     * @since 2022.1.1
     */
    public AgentClientEndpointHandlerMapping(List<HandlerInterceptor> clientHttpRequestInterceptors) {
        super();
        if (CollectionUtils.isNotEmpty(clientHttpRequestInterceptors)) {
            this.interceptors.addAll(clientHttpRequestInterceptors);
        }
    }

    /**
     * Extend interceptors
     *
     * @param interceptors interceptors
     * @since 2022.1.1
     */
    @Override
    protected void extendInterceptors(@NotNull List<Object> interceptors) {
        interceptors.addAll(this.interceptors);
    }

}
