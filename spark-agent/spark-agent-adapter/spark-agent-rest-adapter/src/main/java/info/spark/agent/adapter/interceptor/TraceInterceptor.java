package info.spark.agent.adapter.interceptor;

import info.spark.starter.basic.constant.TraceConstant;
import info.spark.starter.basic.context.Trace;
import info.spark.starter.basic.util.StringUtils;
import info.spark.starter.id.service.IdService;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 使用拦截器将 token/path 添加到 header </p>
 * 使用 {@link TraceConstant#X_TRACE_ID} 透传 traceId 数据
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.12 13:40
 * @since 1.6.0
 */
@Slf4j
public class TraceInterceptor implements ClientHttpRequestInterceptor, Ordered {
    /** Id service */
    private final IdService idService;

    /**
     * Context interceptor
     *
     * @param idService id service
     * @since 1.6.0
     */
    public TraceInterceptor(IdService idService) {
        log.info("Loading Trace Plugin: [{}]", TraceInterceptor.class);
        this.idService = idService;
    }

    /**
     * 此拦截器优先级必须最高
     *
     * @return the order
     * @since 1.6.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 150;
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
        // 往 header 中设置自己需要的参数 (todo-dong4j : (2021.02.24 17:25) [实验性质, 待删除])
        headers.set("X-Trace-Plugin", "agent.plugin.enable");

        // 如果集成了 traceId 则会写入到 Trace, 这里优先获取
        String traceId = Trace.context().get();
        if (StringUtils.isBlank(traceId)) {
            traceId = headers.getFirst(TraceConstant.X_TRACE_ID);
        }
        // 如果还是为空则重新生成一个
        if (StringUtils.isBlank(traceId)) {
            traceId = this.idService == null ? StringUtils.getUid() : String.valueOf(this.idService.genId());
            log.debug("request: [{}]  new traceId: [{}]", request.getURI().toString(), traceId);
            Trace.context().set(traceId);
        }
        headers.set(TraceConstant.X_TRACE_ID, traceId);
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }
}
