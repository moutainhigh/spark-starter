package info.spark.agent.endpoint;

import info.spark.agent.constant.AgentConstant;
import info.spark.agent.entity.ApiServiceHeader;
import info.spark.agent.entity.ApiServiceRequest;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.basic.constant.TraceConstant;
import info.spark.starter.basic.context.Trace;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.util.core.api.R;
import info.spark.starter.core.util.NetUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.core.util.WebUtils;
import info.spark.starter.endpoint.Endpoint;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 全局 controller, 所有请求都由此类分发 </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 15:33
 * @since 1.0.0
 */
@Slf4j
@Endpoint
@ResponseBody
@SuppressWarnings("checkstyle:ParameterNumber")
public class AgentEndpoint extends AbstractEndpoint implements AgentCommonEndpoint {

    /**
     * Handle response entity
     *
     * @param api             API名称
     * @param version         API版本
     * @param appid           产品标识
     * @param timestamp       时间戳 毫秒
     * @param sign            签名值
     * @param nonce           请求唯一标识
     * @param signatureHeader signature header
     * @param charset         请求的编码方式
     * @param method          method
     * @param data            请求包
     * @return the response entity
     * @since 1.0.0
     */
    @Override
    public byte[] handle(String api,
                         String version,
                         String appid,
                         Long timestamp,
                         String sign,
                         String nonce,
                         String signatureHeader,
                         String charset,
                         @NotNull HttpMethod method,
                         byte[] data) {

        String requestId = StringUtils.getUid();
        this.response.setHeader(AgentConstant.X_AGENT_REQUEST_ID, requestId);

        // 如果未通过 AgentTemplate 或 SDK 调用, 有可能为 null, 这里需要兼容处理
        if (data == null) {
            data = JsonUtils.toJsonAsBytes("");
        }

        ApiServiceRequest apiServiceRequest = new ApiServiceRequest(requestId,
                                                                    appid,
                                                                    api,
                                                                    version,
                                                                    nonce,
                                                                    NetUtils.ip(this.request),
                                                                    data);

        ApiServiceHeader apiServiceHeader = new ApiServiceHeader(timestamp,
                                                                 sign,
                                                                 this.request.getServletPath(),
                                                                 method.name(),
                                                                 WebUtils.getHeader(this.request));

        this.saveContexts(apiServiceHeader, this.response);
        apiServiceRequest.setRequest(this.request);
        apiServiceRequest.setResponse(this.response);
        return this.agentService.send(apiServiceRequest, apiServiceHeader);
    }

    /**
     * Save contexts
     *
     * @param apiServiceHeader api service header
     * @param response         response
     * @since 1.6.0
     */
    private void saveContexts(@NotNull ApiServiceHeader apiServiceHeader, @NotNull HttpServletResponse response) {
        // 如果集成了 tracer 插件则会存在 traceId, 否则按照原逻辑从 header 中获取
        String traceId = Trace.context().get();

        if (StringUtils.isBlank(traceId)) {
            traceId = StringUtils.isBlank(traceId)
                      ? apiServiceHeader.getHeaders().getOrDefault(TraceConstant.X_TRACE_ID,
                                                                   apiServiceHeader.getHeaders().get(AgentConstant.X_AGENT_REQUEST_ID))
                      : traceId;
            // 将 trace 写入到当前线程, 如果没有则写入一个 uuid
            Trace.context().set(traceId);
        }
        response.setHeader(TraceConstant.X_TRACE_ID, StringUtils.isBlank(traceId) ? "" : traceId);
    }

    /**
     * 时间校对, 用于签名时的时间对比, 需要使用服务器时间
     * curl 'http://127.0.0.1:18080/agent/time'
     * curl --location --request GET 'http://127.0.0.1:18080/agent/time'
     *
     * @return result 返回时间戳, 不返回 Date 对象的原因是框架默认将 Date 序列化为 yyyy-MM-dd HH:mm:ss 格式, 在反序列化为 Date 时会丢失精度
     * @since 1.0.0
     */
    @GetMapping(value = AgentConstant.ROOT_ENDPOINT + "/time")
    public Result<?> time() {
        return R.values(BasicConstant.RESULT_WRAPPER_VALUE_KEY, System.currentTimeMillis());
    }

    /**
     * Ping string
     * curl 'http://127.0.0.1:18080/agent/ping'
     * curl --location --request GET 'http://127.0.0.1:18080/agent/ping'
     *
     * @return the string
     * @since 1.0.0
     */
    @GetMapping(value = AgentConstant.ROOT_ENDPOINT + "/ping")
    public Result<?> ping() {
        return R.values(BasicConstant.RESULT_WRAPPER_VALUE_KEY, "pong");
    }
}
