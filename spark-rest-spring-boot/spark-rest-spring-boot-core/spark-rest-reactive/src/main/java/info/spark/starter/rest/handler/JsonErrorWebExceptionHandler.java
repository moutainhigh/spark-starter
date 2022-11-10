package info.spark.starter.rest.handler;

import com.google.common.collect.Maps;

import info.spark.starter.basic.Result;
import info.spark.starter.basic.context.GlobalContext;
import info.spark.starter.basic.context.Trace;
import info.spark.starter.basic.exception.BasicException;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.enums.SparkEnv;
import info.spark.starter.common.exception.ExceptionInfo;
import info.spark.starter.common.exception.GlobalExceptionHandler;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.api.R;
import info.spark.starter.util.StringUtils;
import info.spark.starter.rest.ReactiveConstants;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * <p>Description: 自定义全局异常包装 </p>
 * 根据 {@link R} 包装异常返回结果
 * 默认的异常处理器:
 * {@link DefaultErrorWebExceptionHandler}
 * todo-dong4j : (2019年08月17日 17:11) [还未处理 sentine 流控异常]
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:24
 * @since 1.0.0
 */
@Slf4j
public class JsonErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
    /** 能找到路由配置, 但是对应的服务不存在 */
    private static final String GATEWAY_NOTFOUNDEXCEPTION = "org.springframework.cloud.gateway.support.NotFoundException";

    /**
     * Instantiates a new Json error web exception handler.
     *
     * @param errorAttributes    the error attributes
     * @param resourceProperties the resource properties
     * @param errorProperties    the error properties
     * @param applicationContext the application context
     * @since 1.0.0
     */
    public JsonErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                        ResourceProperties resourceProperties,
                                        ErrorProperties errorProperties,
                                        ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 获取异常属性, 根据环境包装异常信息
     *
     * @param request           the request
     * @param includeStackTrace the include stack trace
     * @return the error attribute
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("all")
    protected Map<String, Object> getErrorAttributes(@NotNull ServerRequest request, boolean includeStackTrace) {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(4);
        Map<String, Object> data = Maps.newHashMapWithExpectedSize(2);
        map.put(R.DATA, data);
        map.put(R.TRACE_ID, Trace.context().get());
        return this.response(request, map);
    }

    /**
     * 生成环境统一返回 CommonResponseEnum.SERVER_BUSY 错误
     *
     * @param map map
     * @return the map
     * @since 1.0.0
     */
    @Contract("_ -> param1")
    @NotNull
    private static Map<String, Object> response(@NotNull Map<String, Object> map) {
        map.put("type", Result.TYPE_NAME);
        map.put(R.CODE, BaseCodes.SERVER_BUSY.getCode());
        map.put(R.MESSAGE, BaseCodes.SERVER_BUSY.getMessage());
        map.put(R.SUCCESS, false);
        return map;
    }

    /**
     * 如果是本地开发, 则输出更多的错误信息, 且必须包含生产环境有的字段
     *
     * @param request the request
     * @param map     map
     * @return map map
     * @since 1.0.0
     */
    @Contract("_, _ -> param2")
    @NotNull
    private Map<String, Object> response(ServerRequest request, @NotNull Map<String, Object> map) {
        Throwable error = super.getError(request);
        String errorMessage = buildMessage(request, error);
        // todo-dong4j : (2020-06-30 22:54) [待删除]
        map.put("type", Result.TYPE_NAME);
        map.put(R.CODE, request.attribute(R.CODE).orElse(HttpStatus.SERVICE_UNAVAILABLE.value()));
        map.put(R.MESSAGE, errorMessage);
        map.put(R.SUCCESS, false);

        if (error instanceof BasicException) {
            // 捕获自定义异常
            BasicException baseException = (BasicException) error;
            map.put(R.CODE, baseException.getCode());
            map.put(R.MESSAGE, baseException.getMessage());
            errorMessage = baseException.getMessage();
        } else if (GATEWAY_NOTFOUNDEXCEPTION.equals(error.getClass().getName())) {
            // 能找到路由配置, 但是对应的服务不存在 (未在注册中心找到服务)
            map.put(R.CODE, BaseCodes.GATEWAY_NOT_FUND_INSTANCES_ERROR.getCode());
            map.put(R.MESSAGE, BaseCodes.GATEWAY_NOT_FUND_INSTANCES_ERROR.getMessage());
            errorMessage = BaseCodes.GATEWAY_NOT_FUND_INSTANCES_ERROR.getMessage();
        } else if (error instanceof ResponseStatusException) {
            // 路由配置不正确, 导致直接调用 gateway 的 rest 接口, 而 gateway 根本没有这个接口
            ResponseStatusException exception = (ResponseStatusException) error;
            map.put(R.CODE, BaseCodes.GATEWAY_ROUTER_ERROR.getCode());
            map.put(R.MESSAGE, BaseCodes.GATEWAY_ROUTER_ERROR.getMessage() + ": " + exception.getMessage());
            errorMessage = BaseCodes.GATEWAY_ROUTER_ERROR.getMessage() + ": " + exception.getMessage();
        }

        // 日志输出真实的错误信息,
        if (SparkEnv.PROD.equals(ConfigKit.getEnv())) {
            log.error("code: [{}], errorMessage: [{}]", map.get(R.CODE), errorMessage);
            map.put(R.CODE, BaseCodes.SERVER_BUSY.getCode());
            map.put(R.MESSAGE, BaseCodes.SERVER_BUSY.getMessage());
            return map;
        }

        ServerHttpRequest serverHttpRequest = request.exchange().getRequest();
        HttpHeaders httpHeaders = serverHttpRequest.getHeaders();

        ExceptionInfo exceptionEntity = new ExceptionInfo();
        exceptionEntity.setExceptionClass(error.getClass().getName());
        exceptionEntity.setPath(request.path());
        exceptionEntity.setMethod(request.methodName());
        exceptionEntity.setTraceId(StringUtils.isBlank(Trace.context().get()) ? StringUtils.getUid() : Trace.context().get());
        Optional<InetSocketAddress> inetSocketAddress = request.remoteAddress();
        String hostName = "";
        if (inetSocketAddress.isPresent()) {
            hostName = inetSocketAddress.get().getHostName();
        }
        exceptionEntity.setRemoteAddr(hostName);
        exceptionEntity.setParams(serverHttpRequest.getQueryParams().toSingleValueMap());
        exceptionEntity.setHeaders(httpHeaders.toSingleValueMap());
        exceptionEntity.setHyperlink(GlobalExceptionHandler.buildErrorLink());
        map.put(R.EXTEND, exceptionEntity);
        map.put(R.DATA, Collections.emptyMap());
        return map;
    }

    /**
     * 构建异常信息
     *
     * @param request request
     * @param ex      ex
     * @return string string
     * @since 1.0.0
     */
    @NotNull
    private static String buildMessage(@NotNull ServerRequest request, Throwable ex) {
        StringBuilder message = new StringBuilder("Failed to handle request [");
        message.append(request.methodName());
        message.append(" ");
        message.append(request.uri());
        message.append("]");
        if (ex != null) {
            message.append(": ");
            message.append(ex.getMessage());
        }
        return message.toString();
    }

    /**
     * 指定响应处理方法为 JSON 处理的方法
     *
     * @param errorAttributes the error attributes
     * @return the routing functions
     * @since 1.0.0
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 输出错误信息
     *
     * @param request request
     * @return the mono
     * @since 1.6.0
     */
    @Override
    protected @NotNull Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        boolean includeStackTrace = this.isIncludeStackTrace(request, MediaType.ALL);
        Map<String, Object> error = this.getErrorAttributes(request, includeStackTrace);
        error.put(R.CODE, "S.G-" + error.get(R.CODE));
        log.error("router: [{}] \n{}", GlobalContext.get(ReactiveConstants.GATEWAY_ROUTER), JsonUtils.toJson(error, true));
        // 修改 http 状态为 200, 使用 body 判断请求是否成功
        return ServerResponse.status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(error));
    }

}
