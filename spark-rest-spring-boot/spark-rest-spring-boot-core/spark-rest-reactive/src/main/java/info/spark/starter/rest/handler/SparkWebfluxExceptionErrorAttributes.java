package info.spark.starter.rest.handler;

import info.spark.starter.basic.context.Trace;
import info.spark.starter.basic.util.Exceptions;
import info.spark.starter.common.exception.ExceptionInfo;
import info.spark.starter.common.exception.GlobalExceptionHandler;
import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.api.R;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * <p>Description: 所有错误都返回自定义错误信息</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:23
 * @since 1.0.0
 */
public class SparkWebfluxExceptionErrorAttributes extends DefaultErrorAttributes {

    /** ERROR_ATTRIBUTE */
    private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";
    /** Include exception */
    private final boolean includeException;

    /**
     * 只有在本地开发时才会输出异常堆栈
     *
     * @param includeException the include exception
     * @since 1.0.0
     */
    public SparkWebfluxExceptionErrorAttributes(boolean includeException) {
        super(includeException);
        this.includeException = includeException;
    }

    /**
     * 自定义异常信息
     *
     * @param request           the request
     * @param includeStackTrace include stack trace
     * @return the error attribute
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        // 根据是否为 prod 环境来获取 map, 不使用原来的配置
        if (!this.includeException) {
            // 当为生产环境,不适合把具体的异常信息展示给用户,比如数据库异常信息.
            return R.failMap(BaseCodes.FAILURE);
        }
        return this.buildExceptionErrorAttributes(request);
    }

    /**
     * Build exception error attributes map
     *
     * @param webRequest web request
     * @return the map
     * @since 1.0.0
     */
    @NotNull
    private Map<String, Object> buildExceptionErrorAttributes(ServerRequest webRequest) {
        Map<String, Object> result = R.failMap(BaseCodes.FAILURE);
        Throwable error = this.getError(webRequest);
        HttpStatus errorStatus = determineHttpStatus(error);
        addStatus(result, errorStatus);
        result.put(R.DATA, this.buildData(webRequest, error));
        return result;
    }

    /**
     * Determine http status http status
     *
     * @param error error
     * @return the http status
     * @since 1.0.0
     */
    private static HttpStatus determineHttpStatus(Throwable error) {
        if (error instanceof ResponseStatusException) {
            return ((ResponseStatusException) error).getStatus();
        }
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(error.getClass(),
                                                                                   ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.code();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * Add status *
     *
     * @param errorAttributes error attributes
     * @param errorStatus     error status
     * @since 1.0.0
     */
    private static void addStatus(@NotNull Map<String, Object> errorAttributes, @NotNull HttpStatus errorStatus) {
        errorAttributes.put(R.CODE, errorStatus.value());
        errorAttributes.put(R.MESSAGE, errorStatus.getReasonPhrase());
    }

    /**
     * Build data object
     *
     * @param webRequest web request
     * @param error      error
     * @return the object
     * @since 1.0.0
     */
    private Object buildData(@NotNull ServerRequest webRequest, Throwable error) {
        ServerHttpRequest serverHttpRequest = webRequest.exchange().getRequest();
        HttpHeaders httpHeaders = serverHttpRequest.getHeaders();
        ExceptionInfo exceptionEntity = new ExceptionInfo();
        exceptionEntity.setPath(webRequest.path());
        exceptionEntity.setErrorMessage(determineMessage(error));
        exceptionEntity.setExceptionClass(error.getClass().getName());
        exceptionEntity.setParams(serverHttpRequest.getQueryParams().toSingleValueMap());
        exceptionEntity.setMethod(webRequest.methodName());
        exceptionEntity.setRemoteAddr(webRequest.remoteAddress().isPresent() ? webRequest.remoteAddress().get().getHostName() : "");
        exceptionEntity.setHeaders(httpHeaders.toSingleValueMap());
        exceptionEntity.setTraceId(Trace.context().get());
        exceptionEntity.setStackTrace(Exceptions.getStackTraceAsString(error));
        exceptionEntity.setHyperlink(GlobalExceptionHandler.buildErrorLink());
        this.addStackTrace(exceptionEntity, error);
        return exceptionEntity;
    }

    /**
     * Determine message string
     *
     * @param error error
     * @return the string
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    private static String determineMessage(Throwable error) {
        if (error instanceof WebExchangeBindException) {
            return error.getMessage();
        }
        if (error instanceof ResponseStatusException) {
            return ((ResponseStatusException) error).getReason();
        }
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(error.getClass(),
                                                                                   ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.reason();
        }
        return error.getMessage();
    }

    /**
     * Add stack trace *
     *
     * @param exceptionEntity exception entity
     * @param error           error
     * @since 1.0.0
     */
    @SuppressWarnings("all")
    private void addStackTrace(@NotNull ExceptionInfo exceptionEntity, @NotNull Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        exceptionEntity.setStackTrace(stackTrace.toString());
    }

    /**
     * Gets error *
     *
     * @param request request
     * @return the error
     * @since 1.0.0
     */
    @Override
    public Throwable getError(@NotNull ServerRequest request) {
        return (Throwable) request.attribute(ERROR_ATTRIBUTE)
            .orElseThrow(() -> new IllegalStateException("Missing exception attribute in ServerWebExchange"));
    }

    /**
     * Store error information *
     *
     * @param error    error
     * @param exchange exchange
     * @since 1.0.0
     */
    @Override
    public void storeErrorInformation(Throwable error, @NotNull ServerWebExchange exchange) {
        exchange.getAttributes().putIfAbsent(ERROR_ATTRIBUTE, error);
    }
}
