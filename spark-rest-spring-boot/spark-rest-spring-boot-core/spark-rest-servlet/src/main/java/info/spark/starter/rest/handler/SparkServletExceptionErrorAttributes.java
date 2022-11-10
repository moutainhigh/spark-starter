package info.spark.starter.rest.handler;

import com.google.common.collect.Maps;

import info.spark.starter.basic.context.Trace;
import info.spark.starter.basic.util.Exceptions;
import info.spark.starter.common.exception.ExceptionInfo;
import info.spark.starter.common.exception.GlobalExceptionHandler;
import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.api.R;
import info.spark.starter.util.core.exception.BaseException;
import info.spark.starter.util.ResultCodeUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 所有错误都返回自定义错误信息</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:22
 * @since 1.0.0
 */
@Slf4j
public class SparkServletExceptionErrorAttributes extends DefaultErrorAttributes {

    /** Include exception */
    private final boolean includeException;

    /**
     * 只有在本地开发时才会输出异常堆栈
     *
     * @param includeException the include exception
     * @since 1.0.0
     */
    public SparkServletExceptionErrorAttributes(boolean includeException) {
        super(includeException);
        this.includeException = includeException;
    }

    /**
     * 自定义异常信息
     *
     * @param webRequest        the web request
     * @param includeStackTrace the include stack trace
     * @return the error attribute
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        // 根据是否为 prod 环境来获取 map, 不使用原来的配置
        if (!this.includeException) {
            Throwable error = this.getError(webRequest);
            // 如果是系统自定义异常
            if (error instanceof BaseException) {
                return R.failMap(((BaseException) error).getResultCode());
            }
            return R.failMap(BaseCodes.FAILURE);
        }
        return this.buildExceptionErrorAttributes(webRequest);
    }

    /**
     * 非生成环境输出全量异常信息
     *
     * @param webRequest web request
     * @return the map
     * @since 1.0.0
     */
    @NotNull
    private Map<String, Object> buildExceptionErrorAttributes(WebRequest webRequest) {
        Map<String, Object> result = R.failMap(BaseCodes.FAILURE);
        this.addStatus(result, webRequest);
        result.put(R.DATA, this.buildData(webRequest));
        return result;
    }

    /**
     * Add status *
     *
     * @param errorAttributes error attributes
     * @param webRequest      web request
     * @since 1.0.0
     */
    private void addStatus(Map<String, Object> errorAttributes, WebRequest webRequest) {
        IResultCode resultCode = null;
        Throwable error = this.getError(webRequest);
        if (error != null) {
            log.debug("处理异常信息: [{}]", error.getClass());
            // 如果是系统自定义异常
            if (error instanceof BaseException) {
                resultCode = ((BaseException) error).getResultCode();
            }
            if (error.getCause() instanceof BaseException) {
                resultCode = ((BaseException) error.getCause()).getResultCode();
            }
        }

        if (resultCode != null) {
            errorAttributes.put(R.CODE, ResultCodeUtils.generateCode(resultCode));
            errorAttributes.put(R.MESSAGE, resultCode.getMessage());
        } else {
            Integer code = this.getAttribute(webRequest, "javax.servlet.error.status_code");
            errorAttributes.put(R.CODE, code);
            try {
                errorAttributes.put(R.MESSAGE, HttpStatus.valueOf(code).getReasonPhrase());
            } catch (Exception ex) {
                errorAttributes.put(R.MESSAGE, this.getAttribute(webRequest, "javax.servlet.error.message"));
            }
        }
    }

    /**
     * Build data object
     *
     * @param webRequest web request
     * @return the object
     * @since 1.0.0
     */
    private @NotNull Object buildData(@NotNull WebRequest webRequest) {
        ExceptionInfo exceptionEntity = new ExceptionInfo();
        HttpMethod httpMethod = ((ServletWebRequest) webRequest).getHttpMethod();
        String method = httpMethod == null ? "" : httpMethod.name();
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(16);
        Iterator<String> headerNames = webRequest.getHeaderNames();
        while (headerNames.hasNext()) {
            String headerName = headerNames.next();
            headers.put(headerName, webRequest.getHeader(headerName));
        }
        exceptionEntity.setMethod(method);
        exceptionEntity.setPath(this.fetchPath(webRequest));
        exceptionEntity.setParams(webRequest.getParameterMap());
        exceptionEntity.setRemoteAddr(webRequest.getRemoteUser());
        exceptionEntity.setHeaders(headers);
        exceptionEntity.setTraceId(Trace.context().get());
        exceptionEntity.setHyperlink(GlobalExceptionHandler.buildErrorLink());

        Throwable error = this.getError(webRequest);
        if (error != null) {
            while ((error instanceof BaseException || error instanceof ServletException) && error.getCause() != null) {
                error = error.getCause();
            }
            exceptionEntity.setExceptionClass(error.getClass().getName());
            this.addErrorMessage(exceptionEntity, error);
            exceptionEntity.setStackTrace(Exceptions.getStackTraceAsString(error));
        }
        return exceptionEntity;
    }

    /**
     * Gets attribute *
     *
     * @param <T>               parameter
     * @param requestAttributes request attributes
     * @param name              name
     * @return the attribute
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    private <T> T getAttribute(@NotNull RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * Fetch path string
     *
     * @param requestAttributes request attributes
     * @return the string
     * @since 1.0.0
     */
    private @NotNull String fetchPath(RequestAttributes requestAttributes) {
        String path = this.getAttribute(requestAttributes, "javax.servlet.error.request_uri");
        return path != null ? path : "";
    }

    /**
     * Add error message *
     *
     * @param exceptionEntity exception entity
     * @param error           error
     * @since 1.0.0
     */
    private void addErrorMessage(ExceptionInfo exceptionEntity, Throwable error) {
        BindingResult result = this.extractBindingResult(error);
        if (result == null) {
            exceptionEntity.setErrorMessage(error.getMessage());
            return;
        }
        if (result.hasErrors()) {
            exceptionEntity.setErrorMessage("Validation failed for object='"
                                            + result.getObjectName()
                                            + "'. Error count: "
                                            + result.getErrorCount());
        } else {
            exceptionEntity.setErrorMessage("No errors");
        }
    }

    /**
     * Extract binding result binding result
     *
     * @param error error
     * @return the binding result
     * @since 1.0.0
     */
    @Contract("null -> null")
    private BindingResult extractBindingResult(Throwable error) {
        if (error instanceof BindingResult) {
            return (BindingResult) error;
        }
        if (error instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) error).getBindingResult();
        }
        return null;
    }

}
