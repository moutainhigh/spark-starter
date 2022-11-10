package info.spark.agent.endpoint.exception;

import info.spark.agent.constant.AgentConstant;
import info.spark.agent.exception.AgentCodes;
import info.spark.agent.exception.AgentServiceException;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.context.Trace;
import info.spark.starter.basic.exception.BasicException;
import info.spark.starter.basic.exception.TimeoutUtilsExecuteException;
import info.spark.starter.common.context.SpringContext;
import info.spark.starter.common.event.AgentInvokeErrorEvent;
import info.spark.starter.common.event.AgentInvokeTimeoutEvent;
import info.spark.starter.common.event.BaseEvent;
import info.spark.starter.util.core.api.R;
import info.spark.starter.util.core.exception.BaseException;
import info.spark.starter.util.StringUtils;
import info.spark.starter.rest.exception.ServletGlobalExceptionHandler;

import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.30 00:27
 * @since 1.5.0
 */
@Slf4j
public class AgentGlobalExceptionHandler extends ServletGlobalExceptionHandler {

    /**
     * Agent global exception handler
     *
     * @since 2022.1.1
     */
    public AgentGlobalExceptionHandler() {
        log.info("加载全局异常处理器: [{}]", AgentGlobalExceptionHandler.class);
    }

    /**
     * Handle agent service exception
     *
     * @param e e
     * @return the result
     * @since 1.5.0
     */
    @ExceptionHandler(value = {
        AgentServiceException.class,
    })
    public Result<?> handleAgentServiceException(@NotNull BaseException e) {
        log.warn(e.getMessage());
        return R.failed(e.getCode(), e.getMessage());
    }

    /**
     * agent service 执行业务逻辑处理超时
     *
     * @param e       e
     * @param request request
     * @return the result
     * @since 1.8.0
     */
    @ExceptionHandler(value = {
        TimeoutUtilsExecuteException.class
    })
    public Result<?> handleTimeoutUtilsExecuteException(@NotNull TimeoutUtilsExecuteException e,
                                                        @NotNull HttpServletRequest request) {

        log.warn("执行超时, 请使用 @ApiService.timeout 或 @ApiServiceMethod.timeout 合理设置超时时间, "
                 + "可使用 [spark.agent.endpoint.request-timeout] 修改全局超时时间, 默认 15 秒.");

        return this.buildResult(e,
                                request,
                                new AgentInvokeTimeoutEvent(e),
                                "执行超时",
                                AgentCodes.INVOKER_TIMEOUT);
    }

    /**
     * agent service 执行业务逻辑失败
     *
     * @param e       e
     * @param request request
     * @return the result
     * @since 1.7.1
     */
    @ExceptionHandler(value = {
        ExecutionException.class
    })
    public Result<?> handleTimeoutUtilsExecuteException(@NotNull ExecutionException e,
                                                        @NotNull HttpServletRequest request) {

        return this.buildResult(e, request, new AgentInvokeErrorEvent(e), "执行失败", AgentCodes.INVOKER_ERROR);
    }

    /**
     * agent service 接口异常处理
     *
     * @param e       e
     * @param request request
     * @return the result
     * @since 1.7.0
     */
    @ExceptionHandler(value = {
        ReflectiveOperationException.class,
        InvocationTargetException.class
    })
    public Result<?> handleReflectiveException(@NotNull ReflectiveOperationException e, @NotNull HttpServletRequest request) {
        // 如果是基础异常, 则直接返回错误信息
        if (e.getCause() instanceof BasicException) {
            BasicException baseException = (BasicException) e.getCause();
            return R.failed(baseException.getCode(), baseException.getMessage());
        } else {
            return this.handleError(e.getCause(), request);
        }
    }

    /**
     * 优先级比父类高, 这里添加 agent 的错误堆栈信息.
     *
     * @param e       异常
     * @param request request
     * @return 异常结果 result
     * @since 1.0.0
     */
    @Override
    @ExceptionHandler(Throwable.class)
    public Result<?> handleError(Throwable e, HttpServletRequest request) {
        this.showLog(request, e, "服务内部错误");
        return super.handleError(e, request);
    }

    /**
     * Build result
     *
     * @param e       e
     * @param request request
     * @param title   title
     * @param event   event
     * @param codes   codes
     * @return the result
     * @since 1.8.0
     */
    @NotNull
    private Result<?> buildResult(@NotNull Exception e,
                                  @NotNull HttpServletRequest request,
                                  BaseEvent<?> event,
                                  String title,
                                  AgentCodes codes) {
        String message = this.showLog(request, e, title);
        SpringContext.publishEvent(event);
        Result<?> failed = R.failed(codes);
        failed.setExtend(message);
        return failed;
    }

    /**
     * Show log
     *
     * @param request request
     * @param e       异常信息
     * @param title   title
     * @return the string
     * @since 1.8.0
     */
    private String showLog(@NotNull HttpServletRequest request,
                           @NotNull Throwable e,
                           String title) {
        String message = StringUtils.format("[{} {} {}] \n{}: {}",
                                            request.getMethod(),
                                            request.getServletPath(),
                                            Trace.context().get(),
                                            title,
                                            MDC.get(AgentConstant.EXECUTE_AGENT_SERVICE_ID));

        Throwable cause = e.getCause();

        if (cause == null) {
            log.error(message, e);
        } else {
            log.error(message, cause);
        }

        MDC.remove(AgentConstant.EXECUTE_AGENT_SERVICE_ID);
        return message;
    }

}
