package info.spark.agent.adapter.client;

import info.spark.agent.adapter.config.AgentRestProperties;
import info.spark.agent.adapter.entity.AgentRecord;
import info.spark.agent.adapter.enums.AgentClientErrorCodes;
import info.spark.agent.adapter.enums.AgentRequestType;
import info.spark.agent.adapter.exception.AgentClientException;
import info.spark.agent.adapter.exception.AgentRequestFailedException;
import info.spark.agent.adapter.util.AgentUtils;
import info.spark.agent.constant.AgentConstant;
import info.spark.agent.validation.ValidateMessage;
import info.spark.agent.validation.Validater;

import info.spark.agent.adapter.annotation.Client;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.StandardResult;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.exception.BasicException;
import info.spark.starter.basic.exception.ServiceInternalException;
import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.core.util.DataTypeUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;
import org.springframework.http.HttpMethod;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cn.hutool.core.thread.ThreadUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.07 23:11
 * @since 1.6.0
 */
@Slf4j
public class AgentRequestBuilder implements RequestBuilder {

    /** Headers */
    private final Map<String, String> headers = new HashMap<>(8);
    /** Service name */
    @Getter
    private String serviceName;
    /** Agent template */
    @Getter
    private AgentTemplate agentTemplate;
    /** Agent rest properties */
    private AgentRestProperties agentRestProperties;
    /** 默认 1024, 使用 {@link AgentRequestBuilder#AgentRequestBuilder} 会重新设置, 这里需要兼容老的构造方法 */
    private Long requestMaxLineLength = 1024L;
    /**
     * 通过 {@link Client} 透传的自定义 url
     *
     * @since 1.7.0
     */
    private String customEndpoint;
    /** 请求的接口名 */
    private String apiName;
    /** Version */
    private String version = AgentConstant.DEFAULT_VERSION;
    /** 请求参数(全部为 json 格式) */
    private Object params;
    /** path 参数 */
    private Serializable pathVariable;
    /** 用户传入的 request */
    private AgentRequest request;
    /** Method */
    private HttpMethod method;
    /** 是否需要签名 */
    private Boolean needSignature = false;
    /** Agent request type */
    private AgentRequestType agentRequestType;
    /** No retry template */
    private RetryTemplate noRetryTemplate;
    /** No consumer */
    private final Consumer<AgentRecord> noConsumer = null;
    /** Validater */
    private Validater validater;
    /** 异步请求最大超时时间 */
    private static final Long MAX_AYNC_TIMEOUT = 1000 * 60 * 60L;
    /** NO_LIMIT_SYNC_TIMEOUTT */
    private static final Long NO_LIMIT_SYNC_TIMEOUTT = 0L;
    /** 同步请求默认超时时间 */
    private Long syncTimeout;
    /**
     * 业务端主动传递 tenantId
     *
     * @since 1.8.0
     */
    private String tenantId;

    /**
     * Agent request builder
     *
     * @param httpMethod    http method
     * @param apiName       api name
     * @param serviceName   service name
     * @param agentTemplate agent template
     * @since 1.6.0
     * @deprecated 将在 2.0.0 删除
     */
    @Deprecated
    public AgentRequestBuilder(HttpMethod httpMethod,
                               String apiName,
                               String serviceName,
                               AgentTemplate agentTemplate) {
        log.warn("不要直接使用 AgentRequestBuilder(HttpMethod, String, String, AgentTemplate) 构造器, 请使用 AgentClient 接口");
        this.init(httpMethod, apiName, serviceName, agentTemplate);
    }

    /**
     * Agent request builder
     *
     * @param customEndpoint      custom endpoint
     * @param httpMethod          http method
     * @param apiName             api name
     * @param serviceName         service name
     * @param agentTemplate       agent template
     * @param agentRestProperties agent rest properties
     * @param request             request
     * @param agentRequestType    agent request type
     * @param tenantId            tenant id
     * @param validater           validater
     * @since 1.7.0
     */
    public AgentRequestBuilder(String customEndpoint,
                               HttpMethod httpMethod,
                               String apiName,
                               String serviceName,
                               AgentTemplate agentTemplate,
                               AgentRestProperties agentRestProperties,
                               AgentRequest request,
                               AgentRequestType agentRequestType,
                               String tenantId,
                               Validater validater) {
        this.init(httpMethod, apiName, serviceName, agentTemplate);

        this.agentRestProperties = agentRestProperties;
        this.requestMaxLineLength = agentRestProperties.getRequestMaxLineLength();
        this.syncTimeout = agentRestProperties.getReadTimeout();

        this.customEndpoint = customEndpoint;
        this.agentRequestType = agentRequestType;
        this.request = request;
        this.tenantId = tenantId;
        this.validater = validater;

        if (agentRequestType == AgentRequestType.HTTP_METHOD && httpMethod != null) {
            log.warn("请使用 api() 接口代替 {}() 接口: [{}]", httpMethod.name().toLowerCase(), apiName);
        }
    }

    /**
     * Init
     *
     * @param httpMethod    http method
     * @param apiName       api name
     * @param serviceName   service name
     * @param agentTemplate agent template
     * @since 1.7.1
     */
    private void init(HttpMethod httpMethod, String apiName, String serviceName, AgentTemplate agentTemplate) {
        this.method = httpMethod;
        this.apiName = apiName;
        this.serviceName = serviceName;
        this.agentTemplate = agentTemplate;
        this.noRetryTemplate = new RetryTemplate();
        this.noRetryTemplate.setRetryPolicy(new NeverRetryPolicy());
        this.noRetryTemplate.setBackOffPolicy(new NoBackOffPolicy());
    }

    /**
     * 验证 body 参数
     *
     * @param params params
     * @return the agent request builder
     * @since 1.6.0
     */
    public AgentRequestBuilder params(Object params) {
        this.params = params;

        List<ValidateMessage> validateMessages = this.validater.validate(params);

        if (!CollectionUtils.isEmpty(validateMessages)) {
            String message = validateMessages
                .stream()
                .map(validateMessage -> StrFormatter.format("[{}]: [{}]",
                                                            validateMessage.getProperty(),
                                                            validateMessage.getMessage()))
                .collect(Collectors.joining(StringPool.COMMA));

            throw new AgentClientException(message);

        }
        return this;
    }

    /**
     * Version
     *
     * @param version version
     * @return the agent request builder
     * @since 1.6.0
     */
    public AgentRequestBuilder version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Path
     *
     * @param pathVariable path variable
     * @return the agent request builder
     * @since 1.6.0
     */
    @SuppressWarnings("unused")
    public AgentRequestBuilder pathVariable(Serializable pathVariable) {
        this.pathVariable = pathVariable;
        return this;
    }

    /**
     * Header
     *
     * @param key   key
     * @param value value
     * @return the agent request builder
     * @since 1.6.0
     */
    public AgentRequestBuilder header(String key, String value) {
        this.headers.putIfAbsent(key, value);
        return this;
    }

    /**
     * Request
     *
     * @param request request
     * @return the agent request builder
     * @since 1.7.0
     */
    public AgentRequestBuilder request(AgentRequest request) {
        Assertions.notNull(request, "agent request 不能为 null");
        this.request = request;
        return this;
    }

    /**
     * 设置当前请求需要签名
     *
     * @return the agent request builder
     * @since 1.8.0
     */
    public AgentRequestBuilder needSignature() {
        this.needSignature = true;
        return this;
    }

    /**
     * Build request
     *
     * @return the agent request
     * @since 1.6.0
     */
    @Override
    public AgentRequest buildRequest() {
        // 如果调用了 request(), 则直接使用传入的 agentRequest 对象
        if (AgentRequestType.REQUEST_MENTOD == this.agentRequestType) {
            this.rebuildRequet();
        } else if (AgentRequestType.API_METHOD == this.agentRequestType) {
            this.choiceMethod();
        }

        this.headers.put(AgentConstant.X_AGENT_TENANTID, this.tenantId);
        this.headers.put(AgentConstant.X_AGENT_SDK, this.serviceName);

        AgentRequest newRequest = AgentRequest.builder()
            .apiName(this.apiName)
            .version(this.version)
            .method(this.method)
            .headers(this.headers)
            .customEndpoint(this.customEndpoint)
            .signature(this.needSignature)
            .useSdk(true)
            .build();

        newRequest.setServiceName(this.serviceName);

        this.check(newRequest);

        newRequest.setParams(this.params);
        newRequest.setPathVariable(this.pathVariable == null ? null : String.valueOf(this.pathVariable));
        return newRequest;
    }

    /**
     * 如果传入 AgentRequest, 则使用此实例重建 request.
     *
     * @since 1.7.1
     */
    private void rebuildRequet() {
        Assertions.notBlank(this.request.getApiName(), "apiName 必填");
        this.apiName = this.request.getApiName();
        Assertions.notNull(this.request.getMethod(), "httpMethod 必填");
        this.method = this.request.getMethod();
        this.params = this.request.getParams();
        this.pathVariable = this.request.getPathVariable();
    }

    /**
     * 请求规则检查.
     *
     * @param newRequest new request
     * @since 1.7.1
     */
    private void check(AgentRequest newRequest) {
        // 只有 GET/DELETE 请求支持 pathVariable 参数
        Assertions.isFalse(!HttpMethod.GET.equals(newRequest.getMethod())
                           && !HttpMethod.DELETE.equals(newRequest.getMethod())
                           && this.pathVariable != null,
                           AgentClientErrorCodes.METHOD_PARAMETER_ERRORR.getMessage());

        // 不支持同时使用 params 和 pathVariable
        Assertions.isFalse(this.pathVariable != null && this.params != null,
                           AgentClientErrorCodes.PARAMETER_TYPE_ERRORR.getMessage());

        boolean isBodyRequest = HttpMethod.POST.equals(newRequest.getMethod())
                                || HttpMethod.PATCH.equals(newRequest.getMethod())
                                || HttpMethod.PUT.equals(newRequest.getMethod());
        // POST/PUT/PATCH 请求必须有 body 参数
        Assertions.isFalse(isBodyRequest && this.params == null,
                           AgentClientErrorCodes.POST_PARAMETER_ERRORR.getMessage());
    }

    /**
     * 直接调用 api() 时, 根据传入的参数自动设置 method,
     * 规则:
     * 1. 未传 body 参数, 走 get 请求
     * 2. 传了 body 参数, 入参是基础类型且长度超过 get 请求设置的最大长度时走 post, 否则走 post;
     * 3. 传了 body 参数, 但是不是基础类型时, 统一走 post
     *
     * @since 1.7.1
     */
    private void choiceMethod() {
        if (this.params == null) {
            this.method = HttpMethod.GET;
        } else if (DataTypeUtils.isExtendPrimitive(this.params.getClass())) {
            // 基础类型需要判断传入的数据长度, 由于 post 只需要 body 长度, 而 get 需要转换成 base64, 因此这里使用可能的最大长度(转换为 base64)进行判断
            Integer dataLength = AgentUtils.getDataLength(this.params);
            if (dataLength >= this.requestMaxLineLength) {
                log.info("请求参数长度: [{}] 超过 GET 请求设置的最大长度: [{}], 使用 POST 请求",
                         dataLength,
                         this.agentRestProperties.getRequestMaxLineLength());
                this.method = HttpMethod.POST;
            } else {
                this.method = HttpMethod.GET;
            }
        } else {
            this.method = HttpMethod.POST;
        }
    }

    /**
     * 异步请求
     *
     * @return the result actions
     * @since 1.7.0
     */
    @SuppressWarnings("unused")
    public CompletableFuture<ResultActions> asyncPerform() {
        return this.asyncPerform(this.noRetryTemplate);
    }

    /**
     * Async perform
     *
     * @param retryTemplate retry template
     * @return the future
     * @since 1.7.0
     */
    @SuppressWarnings("unused")
    public CompletableFuture<ResultActions> asyncPerform(RetryTemplate retryTemplate) {
        return this.getResultActionsCompletableFuture(() -> this.perform(retryTemplate, MAX_AYNC_TIMEOUT));
    }

    /**
     * Async perform
     *
     * @param consumer consumer
     * @return the future
     * @since 1.7.0
     */
    @SuppressWarnings("unused")
    public CompletableFuture<ResultActions> asyncPerform(Consumer<AgentRecord> consumer) {
        return this.asyncPerform(consumer, this.noRetryTemplate);
    }

    /**
     * Async perform
     *
     * @param consumer      consumer
     * @param retryTemplate retry template
     * @return the future
     * @since 1.7.0
     */
    @SuppressWarnings("unused")
    public CompletableFuture<ResultActions> asyncPerform(Consumer<AgentRecord> consumer, RetryTemplate retryTemplate) {
        return this.getResultActionsCompletableFuture(() -> this.perform(consumer, retryTemplate, MAX_AYNC_TIMEOUT));
    }

    /**
     * Gets result actions completable future *
     *
     * @param supplier supplier
     * @return the result actions completable future
     * @since 1.7.1
     */
    @NotNull
    private CompletableFuture<ResultActions> getResultActionsCompletableFuture(Supplier<ResultActions> supplier) {
        CompletableFuture<ResultActions> future = CompletableFuture.supplyAsync(supplier);
        future.exceptionally(e -> {
            throw new AgentClientException(e);
        });
        return future;
    }

    /**
     * Perform
     *
     * @return the result actions
     * @since 1.6.0
     */
    public ResultActions perform() {
        return this.perform(this.syncTimeout);
    }

    /**
     * 添加了 timeout 后为了兼容 {@link AgentRequestBuilder#perform()}
     *
     * @param timeout timeout
     * @return the result actions
     * @since 1.7.0
     */
    public ResultActions perform(long timeout) {
        return this.perform(this.noRetryTemplate, timeout);
    }

    /**
     * 带重试功能的请求方式
     *
     * @param retryTemplate retry template
     * @return the result actions
     * @since 1.7.1
     */
    public ResultActions perform(RetryTemplate retryTemplate) {
        return this.perform(retryTemplate, this.syncTimeout);
    }

    /**
     * 添加了 timeout 后为了兼容 {@link AgentRequestBuilder#perform(org.springframework.retry.support.RetryTemplate)}
     *
     * @param retryTemplate retry template
     * @param timeout       timeout
     * @return the result actions
     * @since 1.7.0
     */
    @SuppressWarnings("ConstantConditions")
    public ResultActions perform(RetryTemplate retryTemplate, long timeout) {
        return this.perform(this.noConsumer, retryTemplate, timeout);
    }

    /**
     * Perform
     *
     * @param consumer consumer
     * @return the result actions
     * @since 1.7.1
     */
    @SuppressWarnings("unused")
    public ResultActions perform(Consumer<AgentRecord> consumer) {
        return this.perform(consumer, this.syncTimeout);
    }

    /**
     * 添加了 timeout 后为了兼容 {@link AgentRequestBuilder#perform(java.util.function.Consumer)}
     *
     * @param consumer consumer
     * @param timeout  timeout
     * @return the result actions
     * @since 2.0.0
     */
    public ResultActions perform(Consumer<AgentRecord> consumer, long timeout) {
        return this.perform(consumer, this.noRetryTemplate, timeout);
    }

    /**
     * Perform
     *
     * @param consumer      consumer
     * @param retryTemplate retry template
     * @param timeout       应用端预计的超时时间(业务端优先于服务端, 单位时间:毫秒)
     * @return the result actions
     * @since 1.7.1
     */
    @SuppressWarnings(value = {"checkstyle:MethodLength", "PMD.MethodTooLongRule", "ConstantConditions"})
    public ResultActions perform(Consumer<AgentRecord> consumer, RetryTemplate retryTemplate, long timeout) {
        AgentRequest agentRequest = this.buildRequest();
        if (timeout > NO_LIMIT_SYNC_TIMEOUTT) {
            // 向 header 中添加超时时间配置, 服务端优先使用应用端的配置
            agentRequest.getHeaders().put(AgentConstant.X_AGENT_TIMEOUT, String.valueOf(timeout));
        }

        Date requestTime = new Date();
        Result<?> result = this.retry(retryTemplate, agentRequest);
        Date responseTime = new Date();
        AgentResult[] agentResult = new AgentResult[] {new DefaultAgentResult(result)};

        if (consumer != this.noConsumer) {
            consumer.accept(AgentRequestBuilder.this.buildAgentRecord(result,
                                                                      agentRequest,
                                                                      requestTime,
                                                                      responseTime));
            return () -> agentResult[0];
        }
        return new ResultActions() {

            /**
             * 请求日志记录
             *
             * @param recordService record service
             * @return the result actions
             * @since 1.6.0
             */
            @Contract("_ -> this")
            @Override
            public ResultActions record(RecordService recordService) {
                try {
                    ThreadUtil.execAsync(() -> recordService.save(AgentRequestBuilder.this.buildAgentRecord(result,
                                                                                                            agentRequest,
                                                                                                            requestTime,
                                                                                                            responseTime)));
                } catch (Exception e) {
                    log.error("异步存储调用日志失败", e);
                }
                return this;
            }

            /**
             * Record
             *
             * @param consumer consumer
             * @return the result actions
             * @since 1.7.1
             */
            @Contract("_ -> this")
            @Override
            public ResultActions record(Consumer<AgentRecord> consumer) {
                try {
                    ThreadUtil.execAsync(() -> consumer.accept(AgentRequestBuilder.this.buildAgentRecord(result,
                                                                                                         agentRequest,
                                                                                                         requestTime,
                                                                                                         responseTime)));
                } catch (Exception e) {
                    log.error("异步存储调用日志失败", e);
                }
                return this;
            }

            /**
             * 日志输出请求结果
             *
             * @param level 日志输出等级
             * @return the agent result
             * @since 1.7.0
             */
            @Override
            public ResultActions print(Level level) {
                String message = "";
                try {
                    message = StrFormatter.format("request: [{}] \nresult: [{}]",
                                                  JsonUtils.toJson(agentRequest, true),
                                                  JsonUtils.toJson(result, true));
                } catch (Exception ignored) {
                    // nothing to do
                }

                switch (level) {
                    case TRACE:
                        log.trace(message);
                        break;
                    case DEBUG:
                        log.debug(message);
                        break;
                    case INFO:
                        log.info(message);
                        break;
                    case WARN:
                        log.warn(message);
                        break;
                    default:
                        log.error(message);
                        break;
                }

                return this;
            }

            /**
             * 请求响应回调
             *
             * @param callback callback
             * @return the result actions
             * @since 1.7.0
             */
            @Override
            public ResultActions callback(ResultCallback callback) {
                AgentRecord agentRecord = AgentRequestBuilder.this.buildAgentRecord(result, agentRequest, requestTime, responseTime);

                if (result.isOk()) {
                    callback.successed(agentRecord);
                } else {
                    callback.failed(agentRecord);
                }
                return this;
            }

            /**
             * 请求结果失败时将抛出 {@link AgentRequestFailedException} 异常, 业务端可捕获此异常进行其他处理.
             *
             * @return the result actions
             * @throws AgentRequestFailedException 请求失败时抛出
             * @since 1.7.0
             */
            @Contract(" -> this")
            @Override
            public ResultActions failException() {
                return this.failException(() -> new AgentRequestFailedException(result.getCode(), result.getMessage()));
            }

            /**
             * result.success 为 false 时抛出自定义异常
             *
             * @param exceptionSupplier 自定义异常, 异常消息可自定义
             * @return the result actions
             * @since 1.6.0
             */
            @Contract("_ -> this")
            @Override
            public ResultActions failException(Supplier<? extends BasicException> exceptionSupplier) {
                if (result.isFail()) {
                    log.debug("{}", JsonUtils.toJson(result));
                    Assertions.fail(exceptionSupplier);
                }
                return this;
            }

            /**
             * result.success 为 false 时抛出自定义异常
             *
             * @param exceptionClass 自定义异常, 异常消息为 result 的 message 和 code
             * @return the result actions
             * @since 1.6.0
             */
            @Contract("_ -> this")
            @Override
            public ResultActions failException(Class<? extends BasicException> exceptionClass) {
                if (result.isFail()) {
                    log.debug("{}", JsonUtils.toJson(result, true));
                    Constructor<? extends BasicException> constructor;
                    try {
                        constructor = exceptionClass.getConstructor(String.class, String.class);
                        throw constructor.newInstance(result.getCode(), result.getMessage());
                    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        throw new ServiceInternalException(StrFormatter.format("没有找到 [{}(String code, String message)] 构造方法",
                                                                               exceptionClass.getSimpleName()),
                                                           e);
                    }
                }
                return this;
            }

            /**
             * mock 返回结果 (当调用失败时返回 mock 的数据)
             *
             * @param mockResult mock 的数据
             * @return the result actions
             * @since 1.7.0
             */
            @Override
            public ResultActions mock(Result<?> mockResult) {
                AgentRequestBuilder.this.replace(mockResult, result, agentResult);
                return this;
            }

            /**
             * 结果返回
             *
             * @return the agent result
             * @since 1.6.0
             */
            @Override
            public AgentResult andReturn() {
                return agentResult[0];
            }

        };
    }

    /**
     * 调用失败时使用 mock 数据替换
     *
     * @param mockResult  mock result
     * @param result      result
     * @param agentResult agent result
     * @since 1.7.1
     */
    private void replace(Result<?> mockResult, Result<?> result, AgentResult[] agentResult) {
        if (result.isFail()) {
            agentResult[0] = new DefaultAgentResult(mockResult);
        }
    }

    /**
     * Build agent record
     *
     * @param result       响应接口
     * @param agentRequest 请求实体
     * @param requestTime  请求时间
     * @param responseTime 响应时间
     * @return the agent record
     * @since 1.7.1
     */
    private AgentRecord buildAgentRecord(Result<?> result, AgentRequest agentRequest, Date requestTime, Date responseTime) {
        return AgentRecord.builder()
            .result(result)
            .agentRequest(agentRequest)
            .requestTime(requestTime)
            .responseTime(responseTime)
            .build();
    }

    /**
     * 调用失败时重试 (不是返回结果失败)
     * see org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient
     *
     * @param retryTemplate retry template
     * @param agentRequest  agent request
     * @return the result actions
     * @since 1.7.0
     */
    @SneakyThrows
    @SuppressWarnings(value = {"deprecation", "checkstyle:Indentation"})
    public Result<?> retry(RetryTemplate retryTemplate, AgentRequest agentRequest) {
        return retryTemplate.execute((RetryCallback<Result<?>, Exception>) retryContext -> {
            log.debug("[{}] 重试次数: [{}]", agentRequest.getApiName(), retryContext.getRetryCount());
            return this.agentTemplate.executeForResult(agentRequest, Object.class);
        }, retryContext -> {
            if (retryContext.getRetryCount() > 1) {
                // 兜底回调
                log.error("重试 [{}] 次后仍调用失败, 执行兜底逻辑", retryContext.getRetryCount());
            }
            log.error("", retryContext.getLastThrowable());
            return StandardResult.failed();
        });
    }

}
