package info.spark.feign.adapter.exception;

import info.spark.starter.basic.Result;
import info.spark.starter.basic.StandardResult;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description: 全局异常处理器, 优先级最低, 防止提前处理 v4 相关异常 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:10
 * @since 1.0.0
 */
@Order
@ControllerAdvice
public class FeignGlobalExceptionHandler {

    /**
     * Handler feign adapter exception result
     *
     * @param e       e
     * @param request request
     * @return the result
     * @since 1.0.0
     */
    @ExceptionHandler(FeignAdapterException.class)
    public Result<?> handlerFeignAdapterException(@NotNull FeignAdapterException e, @NotNull HttpServletRequest request) {
        return StandardResult.failed(e.getCode(), e.getMessage());
    }

    /**
     * Handler internal exception result
     *
     * @param e       e
     * @param request request
     * @return the result
     * @since 1.0.0
     */
    @ExceptionHandler(InternalException.class)
    public Result<?> handlerInternalException(@NotNull InternalException e, @NotNull HttpServletRequest request) {
        return StandardResult.failed(Result.FAILURE_CODE, e.getMessage());
    }

}

