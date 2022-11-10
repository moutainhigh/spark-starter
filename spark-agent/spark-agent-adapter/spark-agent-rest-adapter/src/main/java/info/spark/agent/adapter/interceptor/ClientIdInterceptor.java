package info.spark.agent.adapter.interceptor;

import info.spark.agent.adapter.ClientService;
import info.spark.agent.constant.AgentConstant;
import info.spark.starter.common.util.JustOnceLogger;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 使用拦截器将 clientId 添加到 header </p>
 * 使用 {@link AgentConstant#X_AGENT_APPID} 透传 clientId 数据,
 * 业务端可实现 {@link ClientService} 接口, 用于获取 clientId, 分 2 种情况:
 * 1. 如果是共享应用, clientId 会配置到当前应用的应用配置中, 可选择不实现此接口, 将由框架自动获取;
 * 2. 如果是独立应用, clientId
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.12 13:40
 * @since 1.6.0
 */
@Slf4j
public class ClientIdInterceptor implements ClientHttpRequestInterceptor, Ordered {

    /** Client service */
    private final ClientService clientService;

    /**
     * Context interceptor
     *
     * @param clientService client service
     * @since 1.6.0
     */
    public ClientIdInterceptor(ClientService clientService) {
        log.info("Loading ClientId Plugin: [{}]", ClientIdInterceptor.class);
        this.clientService = clientService;
    }

    /**
     * 此拦截器优先级必须最高
     *
     * @return the order
     * @since 1.6.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 400;
    }

    /**
     * Intercept
     *
     * @param request   request
     * @param body      body
     * @param execution execution
     * @return the client http response
     * @throws IOException io exception
     * @since 1.6.0
     */
    @Override
    @SuppressWarnings("all")
    public @NotNull ClientHttpResponse intercept(@NotNull HttpRequest request,
                                                 byte[] body,
                                                 @NotNull ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders headers = request.getHeaders();

        String clientId = headers.getFirst(AgentConstant.X_AGENT_APPID);
        clientId = StringUtils.isEmpty(clientId) ? this.clientService.getClientId() : clientId;

        if (StringUtils.isEmpty(clientId)) {
            JustOnceLogger.warnOnce(this.getClass().getName(),
                                    "无法获取 clientId, 默认使用 [spark_client], 请实现 info.spark.agent.adapter.ClientService");
            clientId = "spark_client";
        }
        headers.set(AgentConstant.X_AGENT_APPID, clientId);
        return execution.execute(request, body);
    }
}
