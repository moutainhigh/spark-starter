package info.spark.agent.adapter.exception;

import info.spark.agent.constant.AgentConstant;
import info.spark.starter.basic.exception.BasicException;
import info.spark.starter.basic.util.IoUtils;
import info.spark.starter.basic.util.JsonUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 全局处理 agent service 未处理的异常 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.09 14:38
 * @see RestTemplate#handleResponse
 * @since 1.5.0
 */
@Slf4j
@SuppressWarnings("all")
public class AgentResponseErrorHandler extends DefaultResponseErrorHandler {

    /**
     * Has error
     *
     * @param response response
     * @return the boolean
     * @throws IOException io exception
     * @since 1.6.0
     */
    @Override
    public boolean hasError(@NotNull ClientHttpResponse response) throws IOException {
        int rawStatusCode = response.getRawStatusCode();
        try {
            HttpStatus statusCode = HttpStatus.valueOf(rawStatusCode);
            return this.hasError(statusCode);
        } catch (IllegalArgumentException e) {
            return this.hasError(rawStatusCode);
        }
    }

    /**
     * Has error
     *
     * @param statusCode status code
     * @return the boolean
     * @since 1.6.0
     */
    @Override
    protected boolean hasError(@NotNull HttpStatus statusCode) {
        return statusCode.is4xxClientError() || statusCode.is5xxServerError() || statusCode.is3xxRedirection();
    }

    /**
     * Has error
     *
     * @param unknownStatusCode unknown status code
     * @return the boolean
     * @since 1.6.0
     */
    protected boolean hasError(int unknownStatusCode) {
        try {
            HttpStatus.Series series = HttpStatus.Series.valueOf(unknownStatusCode);
            return (series == HttpStatus.Series.CLIENT_ERROR
                    || series == HttpStatus.Series.SERVER_ERROR
                    || series == HttpStatus.Series.REDIRECTION);
        } catch (IllegalArgumentException ignored) {
        }
        return true;
    }

    /**
     * 当 http 请求成功 但是返回的结果不是 http.ok 时处理
     *
     * @param response response
     * @throws IOException io exception
     * @since 1.6.0
     */
    @Override
    public void handleError(@NotNull ClientHttpResponse response) throws IOException {
        try {
            HttpStatus statusCode = HttpStatus.valueOf(response.getRawStatusCode());
            this.handleError(response, statusCode);
        } catch (IllegalArgumentException e) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(),
                                                     response.getStatusText(),
                                                     response.getHeaders(),
                                                     this.getResponseBody(response),
                                                     this.getCharset(response));
        }
    }

    /**
     * 兼容 v4
     *
     * @param response   response
     * @param statusCode status code
     * @throws IOException io exception
     * @since 1.5.0
     */
    public void handleError(@NotNull ClientHttpResponse response, @NotNull HttpStatus statusCode) throws IOException {
        HttpHeaders headers = response.getHeaders();
        byte[] body = this.getResponseBody(response);
        String result = IoUtils.toString(body);

        // 当远程服务不可用时将抛出异常 (服务可能未启动或无法访问, http 请求失败)
        String apiName = headers.getFirst(AgentConstant.X_AGENT_API);
        String version = headers.getFirst(AgentConstant.X_AGENT_VERSION);

        log.error("apiName = {}_{} [{}]",
                  apiName,
                  version,
                  result);

        if (statusCode.equals(HttpStatus.NOT_FOUND)) {
            String path = JsonUtils.readTree(result).path("path").asText();

            if (path.contains(AgentConstant.ROOT_ENDPOINT)) {
                log.error("未成功请求 center 服务, 可能的原因: 1. 网关未正确配置或不能提供服务; 2. agent service 不能提供服务");
            }
            throw new BasicException("path:" + path + " NOT_FOUND");
        }
        if (response.getStatusCode().is5xxServerError()) {
            throw new BasicException("服务不可用");
        }
    }

}
