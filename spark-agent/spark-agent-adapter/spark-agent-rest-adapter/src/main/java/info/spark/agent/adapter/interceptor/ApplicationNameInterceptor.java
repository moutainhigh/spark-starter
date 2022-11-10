package info.spark.agent.adapter.interceptor;

import info.spark.agent.constant.AgentConstant;
import info.spark.starter.basic.util.StringPool;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.08 17:55
 * @since 1.8.0
 */
public class ApplicationNameInterceptor implements ClientHttpRequestInterceptor, Ordered {

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.8.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 600;
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
        String applicationName = headers.getFirst(AgentConstant.X_AGENT_APPNAME);
        // todo-dong4j : (2021.02.8 18:11) [获取 v4 和 v5 的应用名]
        applicationName = StringUtils.isEmpty(applicationName) ? "" : applicationName;
        if (StringUtils.isEmpty(applicationName)) {
            applicationName = StringPool.NULL_STRING;
        }
        headers.set(AgentConstant.X_AGENT_APPNAME, applicationName);
        return execution.execute(request, body);
    }
}
