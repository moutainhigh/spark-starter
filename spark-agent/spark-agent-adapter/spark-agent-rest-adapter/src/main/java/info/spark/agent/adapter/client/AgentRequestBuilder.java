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
    /** ?????? 1024, ?????? {@link AgentRequestBuilder#AgentRequestBuilder} ???????????????, ???????????????????????????????????? */
    private Long requestMaxLineLength = 1024L;
    /**
     * ?????? {@link Client} ?????????????????? url
     *
     * @since 1.7.0
     */
    private String customEndpoint;
    /** ?????????????????? */
    private String apiName;
    /** Version */
    private String version = AgentConstant.DEFAULT_VERSION;
    /** ????????????(????????? json ??????) */
    private Object params;
    /** path ?????? */
    private Serializable pathVariable;
    /** ??????????????? request */
    private AgentRequest request;
    /** Method */
    private HttpMethod method;
    /** ?????????????????? */
    private Boolean needSignature = false;
    /** Agent request type */
    private AgentRequestType agentRequestType;
    /** No retry template */
    private RetryTemplate noRetryTemplate;
    /** No consumer */
    private final Consumer<AgentRecord> noConsumer = null;
    /** Validater */
    private Validater validater;
    /** ?????????????????????????????? */
    private static final Long MAX_AYNC_TIMEOUT = 1000 * 60 * 60L;
    /** NO_LIMIT_SYNC_TIMEOUTT */
    private static final Long NO_LIMIT_SYNC_TIMEOUTT = 0L;
    /** ?????????????????????????????? */
    private Long syncTimeout;
    /**
     * ????????????????????? tenantId
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
     * @deprecated ?????? 2.0.0 ??????
     */
    @Deprecated
    public AgentRequestBuilder(HttpMethod httpMethod,
                               String apiName,
                               String serviceName,
                               AgentTemplate agentTemplate) {
        log.warn("?????????????????? AgentRequestBuilder(HttpMethod, String, String, AgentTemplate) ?????????, ????????? AgentClient ??????");
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
            log.warn("????????? api() ???????????? {}() ??????: [{}]", httpMethod.name().toLowerCase(), apiName);
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
     * ?????? body ??????
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
        Assertions.notNull(request, "agent request ????????? null");
        this.request = request;
        return this;
    }

    /**
     * ??????????????????????????????
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
        // ??????????????? request(), ???????????????????????? agentRequest ??????
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
     * ???????????? AgentRequest, ???????????????????????? request.
     *
     * @since 1.7.1
     */
    private void rebuildRequet() {
        Assertions.notBlank(this.request.getApiName(), "apiName ??????");
        this.apiName = this.request.getApiName();
        Assertions.notNull(this.request.getMethod(), "httpMethod ??????");
        this.method = this.request.getMethod();
        this.params = this.request.getParams();
        this.pathVariable = this.request.getPathVariable();
    }

    /**
     * ??????????????????.
     *
     * @param newRequest new request
     * @since 1.7.1
     */
    private void check(AgentRequest newRequest) {
        // ?????? GET/DELETE ???????????? pathVariable ??????
        Assertions.isFalse(!HttpMethod.GET.equals(newRequest.getMethod())
                           && !HttpMethod.DELETE.equals(newRequest.getMethod())
                           && this.pathVariable != null,
                           AgentClientErrorCodes.METHOD_PARAMETER_ERRORR.getMessage());

        // ????????????????????? params ??? pathVariable
        Assertions.isFalse(this.pathVariable != null && this.params != null,
                           AgentClientErrorCodes.PARAMETER_TYPE_ERRORR.getMessage());

        boolean isBodyRequest = HttpMethod.POST.equals(newRequest.getMethod())
                                || HttpMethod.PATCH.equals(newRequest.getMethod())
                                || HttpMethod.PUT.equals(newRequest.getMethod());
        // POST/PUT/PATCH ??????????????? body ??????
        Assertions.isFalse(isBodyRequest && this.params == null,
                           AgentClientErrorCodes.POST_PARAMETER_ERRORR.getMessage());
    }

    /**
     * ???????????? api() ???, ????????????????????????????????? method,
     * ??????:
     * 1. ?????? body ??????, ??? get ??????
     * 2. ?????? body ??????, ???????????????????????????????????? get ????????????????????????????????? post, ????????? post;
     * 3. ?????? body ??????, ???????????????????????????, ????????? post
     *
     * @since 1.7.1
     */
    private void choiceMethod() {
        if (this.params == null) {
            this.method = HttpMethod.GET;
        } else if (DataTypeUtils.isExtendPrimitive(this.params.getClass())) {
            // ?????????????????????????????????????????????, ?????? post ????????? body ??????, ??? get ??????????????? base64, ???????????????????????????????????????(????????? base64)????????????
            Integer dataLength = AgentUtils.getDataLength(this.params);
            if (dataLength >= this.requestMaxLineLength) {
                log.info("??????????????????: [{}] ?????? GET ???????????????????????????: [{}], ?????? POST ??????",
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
     * ????????????
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
     * ????????? timeout ??????????????? {@link AgentRequestBuilder#perform()}
     *
     * @param timeout timeout
     * @return the result actions
     * @since 1.7.0
     */
    public ResultActions perform(long timeout) {
        return this.perform(this.noRetryTemplate, timeout);
    }

    /**
     * ??????????????????????????????
     *
     * @param retryTemplate retry template
     * @return the result actions
     * @since 1.7.1
     */
    public ResultActions perform(RetryTemplate retryTemplate) {
        return this.perform(retryTemplate, this.syncTimeout);
    }

    /**
     * ????????? timeout ??????????????? {@link AgentRequestBuilder#perform(org.springframework.retry.support.RetryTemplate)}
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
     * ????????? timeout ??????????????? {@link AgentRequestBuilder#perform(java.util.function.Consumer)}
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
     * @param timeout       ??????????????????????????????(???????????????????????????, ????????????:??????)
     * @return the result actions
     * @since 1.7.1
     */
    @SuppressWarnings(value = {"checkstyle:MethodLength", "PMD.MethodTooLongRule", "ConstantConditions"})
    public ResultActions perform(Consumer<AgentRecord> consumer, RetryTemplate retryTemplate, long timeout) {
        AgentRequest agentRequest = this.buildRequest();
        if (timeout > NO_LIMIT_SYNC_TIMEOUTT) {
            // ??? header ???????????????????????????, ???????????????????????????????????????
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
             * ??????????????????
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
                    log.error("??????????????????????????????", e);
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
                    log.error("??????????????????????????????", e);
                }
                return this;
            }

            /**
             * ????????????????????????
             *
             * @param level ??????????????????
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
             * ??????????????????
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
             * ?????????????????????????????? {@link AgentRequestFailedException} ??????, ?????????????????????????????????????????????.
             *
             * @return the result actions
             * @throws AgentRequestFailedException ?????????????????????
             * @since 1.7.0
             */
            @Contract(" -> this")
            @Override
            public ResultActions failException() {
                return this.failException(() -> new AgentRequestFailedException(result.getCode(), result.getMessage()));
            }

            /**
             * result.success ??? false ????????????????????????
             *
             * @param exceptionSupplier ???????????????, ????????????????????????
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
             * result.success ??? false ????????????????????????
             *
             * @param exceptionClass ???????????????, ??????????????? result ??? message ??? code
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
                        throw new ServiceInternalException(StrFormatter.format("???????????? [{}(String code, String message)] ????????????",
                                                                               exceptionClass.getSimpleName()),
                                                           e);
                    }
                }
                return this;
            }

            /**
             * mock ???????????? (???????????????????????? mock ?????????)
             *
             * @param mockResult mock ?????????
             * @return the result actions
             * @since 1.7.0
             */
            @Override
            public ResultActions mock(Result<?> mockResult) {
                AgentRequestBuilder.this.replace(mockResult, result, agentResult);
                return this;
            }

            /**
             * ????????????
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
     * ????????????????????? mock ????????????
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
     * @param result       ????????????
     * @param agentRequest ????????????
     * @param requestTime  ????????????
     * @param responseTime ????????????
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
     * ????????????????????? (????????????????????????)
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
            log.debug("[{}] ????????????: [{}]", agentRequest.getApiName(), retryContext.getRetryCount());
            return this.agentTemplate.executeForResult(agentRequest, Object.class);
        }, retryContext -> {
            if (retryContext.getRetryCount() > 1) {
                // ????????????
                log.error("?????? [{}] ?????????????????????, ??????????????????", retryContext.getRetryCount());
            }
            log.error("", retryContext.getLastThrowable());
            return StandardResult.failed();
        });
    }

}
