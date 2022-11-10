package info.spark.starter.rest.advice;

import info.spark.starter.basic.Result;
import info.spark.starter.rest.handler.CustomizeReturnValueHandler;
import info.spark.starter.util.core.api.R;
import info.spark.starter.rest.annotation.OriginalResponse;
import info.spark.starter.rest.utis.RestUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

import javax.servlet.Servlet;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 处理使用 {@link Result} 进行包装 </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.04 11:46
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class})
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {
    /**
     * 如果是 @RestController 标记的 Controller 才自动封装为 Result
     *
     * @param returnType    return type
     * @param converterType converter type
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return RestUtils.sparkClass(returnType) && RestUtils.supportsAdvice(returnType);
    }

    /**
     * 在返回数据之前, 使用 Result 包装.
     * 如果需要返回原始数据, 可使用 {@link OriginalResponse} 标识方法, 或者直接使用 Response 写数据, 需要修改 Content-Type 为非 json.
     *
     * @param body                  body
     * @param returnType            return type
     * @param selectedContentType   selected content type
     * @param selectedConverterType selected converter type
     * @param request               request
     * @param response              response
     * @return the object
     * @see CustomizeReturnValueHandler#handleReturnValue
     * @since 1.0.0
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  @NotNull MethodParameter returnType,
                                  @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NotNull ServerHttpRequest request,
                                  @NotNull ServerHttpResponse response) {

        OriginalResponse originalResponse = Objects.requireNonNull(returnType.getMethod()).getAnnotation(OriginalResponse.class);
        // 没有使用 OriginalResponse 标识且 Content-Type 为 application/json 时才包装
        if (originalResponse == null && selectedContentType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
            return this.wrapper(body);
        }
        return body;
    }

    /**
     * Wrapper
     *
     * @param body body
     * @return the object
     * @since 1.8.0
     */
    @NotNull
    private Object wrapper(Object body) {
        if (body instanceof Result) {
            log.trace("请求响应已使用 Result 包装, 原始响应: [{}]", body);
            return body;
        } else {
            if (body == null) {
                return R.succeed();
            }
            log.trace("重写请求响应, 使用 Result 包装, 原始响应: [{}]", body);
            return R.succeed(body);
        }
    }
}
