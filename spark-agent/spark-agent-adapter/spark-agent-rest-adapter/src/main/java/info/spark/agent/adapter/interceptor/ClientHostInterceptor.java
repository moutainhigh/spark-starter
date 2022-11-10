package info.spark.agent.adapter.interceptor;

import info.spark.agent.constant.AgentConstant;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.core.util.NetUtils;

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
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.8 18:08
 * @since 1.8.0
 */
@Slf4j
public class ClientHostInterceptor implements ClientHttpRequestInterceptor, Ordered {

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.8.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 500;
    }

    /**
     * Intercept
     *
     * @param request   request
     * @param body      body
     * @param execution execution
     * @return the client http response
     * @throws IOException io exception
     * @since 1.8.0
     */
    @Override
    @SuppressWarnings("all")
    public @NotNull ClientHttpResponse intercept(@NotNull HttpRequest request,
                                                 byte[] body,
                                                 @NotNull ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders headers = request.getHeaders();
        String host = headers.getFirst(AgentConstant.X_AGENT_HOST);
        host = StringUtils.isEmpty(host) ? NetUtils.getLocalHost() : host;
        if (StringUtils.isEmpty(host)) {
            host = StringPool.NULL_STRING;
        }
        headers.set(AgentConstant.X_AGENT_HOST, host);
        return execution.execute(request, body);
    }
}
