package info.spark.agent.adapter.interceptor;

import info.spark.agent.adapter.ClientService;
import info.spark.agent.adapter.client.AgentRequest;
import info.spark.agent.adapter.client.AgentRequestBuilder;
import info.spark.agent.adapter.exception.AgentClientException;
import info.spark.agent.constant.AgentConstant;
import info.spark.agent.constant.SdkConstant;
import info.spark.agent.enums.SignType;
import info.spark.agent.utils.SignUtils;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.basic.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 使用拦截器对客户端请求进行签名, 客户端在调用时, 需要根据 agent service 文档获知被调用接口是否需要签名, 如果需要则需要手动进行如下调用:
 * 1. 使用 sdk 时, 显式调用 {@link AgentRequestBuilder#needSignature()};
 * 2. 使用 AgentTemplate 时, 手动设置 {@link AgentRequest#signature = true};
 * <p>
 * 如果在明确 agent service 接口需要签名的情况下, 不按照上述去设置 signature, 将导致接口调用失败.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.24 19:46
 * @since 1.8.0
 */
@Slf4j
@SuppressWarnings("all")
public class SignatureInterceptor implements ClientHttpRequestInterceptor, Ordered {

    /** Client service */
    private final ClientService clientService;

    /**
     * Trace agent interceptor
     *
     * @param clientService client service
     * @since 1.8.0
     */
    public SignatureInterceptor(ClientService clientService) {
        log.info("Loading Signature Interceptor: {}", SignatureInterceptor.class);
        this.clientService = clientService;
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
    public @NotNull ClientHttpResponse intercept(@NotNull HttpRequest request,
                                                 byte[] body,
                                                 @NotNull ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders headers = request.getHeaders();
        String needSignature = headers.getFirst(AgentConstant.X_AGENT_NEED_SIGNATURE);
        if (Boolean.parseBoolean(needSignature)) {
            this.sign(request, body);
        }
        return execution.execute(request, body);
    }

    /**
     * Sign
     *
     * @param request request
     * @param body    body
     * @since 1.8.0
     */
    private void sign(@NotNull HttpRequest request, @NotNull byte[] body) {
        // 简单处理 client_id, 所有的业务端都使用一个密钥
        String clientId = clientService.getClientId();
        if (StringUtils.isBlank(clientId)) {
            throw new AgentClientException(40001, "认证失败: clientId 不存在");
        }
        String secret = this.clientService.getSecret(clientId);

        // 需要签名检查的接口都需要传递 client_id 到服务端
        HttpHeaders headers = request.getHeaders();
        headers.add(AgentConstant.X_AGENT_APPID, clientId);
        headers.set(AgentConstant.X_AGENT_SIGNATURE_TYPE, SignType.PATTERN_BYTE.name());
        headers.set(AgentConstant.X_AGENT_NONCE, String.valueOf(System.currentTimeMillis()));

        String path = request.getURI().getPath();

        if (body.length == 0) {
            if (!AgentConstant.ROOT_ENDPOINT.equals(path)) {
                byte[] bytes = JsonUtils.toJsonAsBytes(path.replace(AgentConstant.ROOT_ENDPOINT
                                                                    + StringPool.SLASH,
                                                                    ""));

                body = Base64.getDecoder().decode(Base64.getUrlEncoder().encodeToString(bytes));

            } else if (request.getURI().getQuery() != null) {
                String[] urlParamSplit = request.getURI().getQuery().split(StringPool.EQUALS);
                int urlParamsCount = urlParamSplit.length;
                // get 请求如果是 url 参数, 只会是 data=xxx 格式
                if (urlParamsCount == 2) {
                    body = Base64.getDecoder().decode(urlParamSplit[1]);
                }
            }
        }

        Map<String, String> headerMap = headers.toSingleValueMap();
        String signature = SignUtils.sign(Objects.requireNonNull(request.getMethod()).name(),
                                          secret,
                                          headerMap,
                                          path,
                                          body);
        // 加入参与签名的 header
        headers.set(SdkConstant.CLOUDAPI_X_AGENT_SIGNATURE_HEADERS, headerMap.get(SdkConstant.CLOUDAPI_X_AGENT_SIGNATURE_HEADERS));
        headers.set(AgentConstant.X_AGENT_SIGNATURE, signature);
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.8.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 300;
    }
}
