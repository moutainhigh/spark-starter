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
 * <p>Description: ??????????????????????????? </p>
 * ?????? {@link R} ????????????????????????
 * ????????????????????????:
 * {@link DefaultErrorWebExceptionHandler}
 * todo-dong4j : (2019???08???17??? 17:11) [???????????? sentine ????????????]
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:24
 * @since 1.0.0
 */
@Slf4j
public class JsonErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
    /** ?????????????????????, ?????????????????????????????? */
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
     * ??????????????????, ??????????????????????????????
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
     * ???????????????????????? CommonResponseEnum.SERVER_BUSY ??????
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
     * ?????????????????????, ??????????????????????????????, ???????????????????????????????????????
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
        // todo-dong4j : (2020-06-30 22:54) [?????????]
        map.put("type", Result.TYPE_NAME);
        map.put(R.CODE, request.attribute(R.CODE).orElse(HttpStatus.SERVICE_UNAVAILABLE.value()));
        map.put(R.MESSAGE, errorMessage);
        map.put(R.SUCCESS, false);

        if (error instanceof BasicException) {
            // ?????????????????????
            BasicException baseException = (BasicException) error;
            map.put(R.CODE, baseException.getCode());
            map.put(R.MESSAGE, baseException.getMessage());
            errorMessage = baseException.getMessage();
        } else if (GATEWAY_NOTFOUNDEXCEPTION.equals(error.getClass().getName())) {
            // ?????????????????????, ?????????????????????????????? (??????????????????????????????)
            map.put(R.CODE, BaseCodes.GATEWAY_NOT_FUND_INSTANCES_ERROR.getCode());
            map.put(R.MESSAGE, BaseCodes.GATEWAY_NOT_FUND_INSTANCES_ERROR.getMessage());
            errorMessage = BaseCodes.GATEWAY_NOT_FUND_INSTANCES_ERROR.getMessage();
        } else if (error instanceof ResponseStatusException) {
            // ?????????????????????, ?????????????????? gateway ??? rest ??????, ??? gateway ????????????????????????
            ResponseStatusException exception = (ResponseStatusException) error;
            map.put(R.CODE, BaseCodes.GATEWAY_ROUTER_ERROR.getCode());
            map.put(R.MESSAGE, BaseCodes.GATEWAY_ROUTER_ERROR.getMessage() + ": " + exception.getMessage());
            errorMessage = BaseCodes.GATEWAY_ROUTER_ERROR.getMessage() + ": " + exception.getMessage();
        }

        // ?????????????????????????????????,
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
     * ??????????????????
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
     * ??????????????????????????? JSON ???????????????
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
     * ??????????????????
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
        // ?????? http ????????? 200, ?????? body ????????????????????????
        return ServerResponse.status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(error));
    }

}
