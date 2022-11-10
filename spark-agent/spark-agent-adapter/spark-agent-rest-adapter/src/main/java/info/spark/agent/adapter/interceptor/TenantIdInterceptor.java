package info.spark.agent.adapter.interceptor;

import info.spark.agent.adapter.TenantService;
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
 * <p>Description: 使用拦截器将 tenantId 添加到 header </p>
 * 使用 {@link AgentConstant#X_AGENT_TENANTID} 透传 tenantId 数据,
 * 业务端可实现 {@link TenantService } 接口
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 17:24
 * @since 1.8.0
 */
@Slf4j
public class TenantIdInterceptor implements ClientHttpRequestInterceptor, Ordered {

    /** Tenant service */
    private final TenantService tenantService;

    /**
     * Context interceptor
     *
     * @param tenantService client service
     * @since 1.8.0
     */
    public TenantIdInterceptor(TenantService tenantService) {
        log.info("Loading TenantId Plugin: [{}]", TenantIdInterceptor.class);
        this.tenantService = tenantService;
    }

    /**
     * 此拦截器优先级必须最高
     *
     * @return the order
     * @since 1.8.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
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

        String tenantId = headers.getFirst(AgentConstant.X_AGENT_TENANTID);
        // 优先从 header 获取, 再通过接口获取
        Long tenantIdByService;
        tenantId = StringUtils.isEmpty(tenantId)
                   ? ((tenantIdByService = this.tenantService.getTenantId()) == null ? "" : String.valueOf(tenantIdByService))
                   : tenantId;

        if (StringUtils.isEmpty(tenantId)) {
            JustOnceLogger.warnOnce(this.getClass().getName(), "无法获取 tenantId, 请实现 info.spark.agent.adapter.TenantService");
        } else {
            headers.set(AgentConstant.X_AGENT_TENANTID, tenantId);
        }
        return execution.execute(request, body);
    }
}
