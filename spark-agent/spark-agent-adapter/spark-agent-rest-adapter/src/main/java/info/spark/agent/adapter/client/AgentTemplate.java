package info.spark.agent.adapter.client;

import com.fasterxml.jackson.core.type.TypeReference;
import info.spark.agent.adapter.AgentBundle;
import info.spark.agent.adapter.config.AgentRestProperties;
import info.spark.agent.adapter.enums.AgentClientErrorCodes;
import info.spark.agent.adapter.exception.AgentClientException;
import info.spark.agent.adapter.exception.AgentResponseErrorHandler;
import info.spark.agent.adapter.interceptor.SignatureInterceptor;
import info.spark.agent.adapter.util.AgentUtils;
import info.spark.agent.constant.AgentConstant;

import info.spark.starter.basic.Result;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.BasicUtils;
import info.spark.starter.basic.util.ClassUtils;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.basic.util.StringUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 此类为 {@link RestTemplate} 再次封装, 为了简化 v4 调用 v5 服务, 主要是为了解析被 ResponseEntity 包装的数据.
 * V5 接口的返回数据全部被 Result 包装, 为了在 V4 直接获取 data 数据, 我们提供了
 * {@link AgentOperations#executeForObject(AgentRequest, java.lang.Class)} 接口, 此接口支持返回非泛型的 data 数据.
 * 如果 Result 中的 data 是另一个泛型, 可以使用
 * {@link AgentOperations#executeForObject(AgentRequest, com.fasterxml.jackson.core.type.TypeReference)} 接口
 * 比如 {@code List<User>}, 值得注意的是, 此接口不能使用 {@code Result<List<User>>}.
 * 如果需要使用 {@code Result<List<User>>} 的方式返回数据, 可以使用
 * {@link AgentOperations#executeForObject(AgentRequest,
 * org.springframework.core.ParameterizedTypeReference)}**** 接口
 * 我们还提供了类似 RestTemplate.getForEntity 的接口 executeForResult, 此类接口需要业务端判断 Result 是否成功,
 * 使用方式与 RestTemplate.getForEntity 类型接口类型,
 * 只是返回的类型由 {@code ResponseEntity<T>} 修改为了 {@code Result<T>}, 此类的所有接口我们都对 {@code ResponseEntity<T>} 做了全局处理,
 * 适配 V5 的返回结构, 简化 V4 业务端处理逻辑.
 * </p>
 * org.springframework.security.oauth2.client.OAuth2RestTemplate
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.20 10:25
 * @since 1.0.0
 */
@Slf4j
public class AgentTemplate extends RestTemplate implements AgentOperations, ApplicationContextAware {

    /** 网关地址前缀, 发起请求时将使用 spark.gateway.servers 配置的 ip 地址替换此字符串 */
    private static final String GATEWAY_PREFIX = ConfigKey.AgentConfigKey.GATEWAY_PREFIX;
    /** agent 服务接口后缀 */
    private static final String AGENT_SUFFIX = ConfigKey.AgentConfigKey.AGENT_SUFFIX;
    /** Response type cache */
    private final Map<String, Class<?>> responseTypeCache = new ConcurrentHashMap<>(512);
    /** Agent rest properties */
    private final AgentRestProperties agentRestProperties;
    /** Environment */
    private final Environment environment;

    /**
     * Agent template
     *
     * @param environment         environment
     * @param agentRestProperties 配置类
     * @since 1.0.0
     */
    public AgentTemplate(Environment environment, AgentRestProperties agentRestProperties) {
        super();
        this.environment = environment;
        this.agentRestProperties = agentRestProperties;
    }

    /**
     * 直接返回 Result 包装的数据, 不包括 Result, 不支持泛型.
     * 如果返回的数据为泛型, 请使用 {@link AgentTemplate#executeForObject(AgentRequest,
     * com.fasterxml.jackson.core.type.TypeReference)}
     * 如果 responseType 为 {@link Result} 将抛出 {@link IllegalArgumentException} 异常, 因为此类型会将 data 数据反序列为 LinkedHashMap,
     * 在业务端使用时可能会造成类型转换异常, 请确认 Result 泛型类型, 如果 data 为 null,
     * 请使用此方法;
     * 此接口在原来的 {@link RestOperations#getForObject(java.net.URI, java.lang.Class)} 接口上封装, 由于 v5 所有接口返回的数据都会被 Result 包装,
     * 由于在 GET 请求中添加 header, 只能使用 RestTemplate.exchange 类型的接口,
     * 此类型接口会返回 {@link ResponseEntity} 包装后的数据, 这里为了返回真实的 data 数据, 会对 T 进行 2 次处理,
     * 第一次是去除 ResponseEntity 包装, 第二次是去除 Result 包装
     * 此接口一般用于查询操作.
     * example:
     * 1. 正确的方式: {@code User result = agentTemplate.executeForObject(agentRequest, User.class);}
     * 2. 错误的方式: {@code Result result = agentTemplate.executeForObject(agentRequest, Result.class);}
     *
     * @param <T>          parameter
     * @param request      请求参数
     * @param responseType Result data 的 class 类型
     * @return 反序列化后的具体数据, 如果 Result.isFail 将返回 null
     * @throws AgentClientException     agent client exception
     * @throws IllegalArgumentException 如果 responseType 为 Result.class, 将抛出此异常
     * @since 1.0.0
     */
    @Override
    public <T> T executeForObject(@NotNull AgentRequest request, Class<T> responseType)
        throws AgentClientException, IllegalArgumentException {
        Assertions.isFalse(Result.class.isAssignableFrom(responseType),
                           "ResonseType 不能为 Result, 如果 data 为空或者不需要知道 data, 请使用 Void.class");
        Result<T> result = this.getResultResponseEntity(request);
        return this.getGeneric(result, responseType, request.getUseSdk());
    }

    /**
     * 直接返回 Result 包装的数据, 不包括 Result, 支持泛型.
     * example:
     * 1. 正确的方式: {@code List<User> result = agentTemplate.executeForObject(agentRequest, new TypeReference<List<User>>() {})};
     * 2. 错误的方式: {@code Result<List<User>> result =
     * agentTemplate.executeForObject(agentRequest, new TypeReference<Result<List<User>>>() {});}
     *
     * @param <T>          parameter
     * @param request      请求参数
     * @param responseType 泛型包装器
     * @return 反序列化后的具体数据, 如果 Result.isFail 将返回 null
     * @throws AgentClientException     agent client exception
     * @throws IllegalArgumentException 如果 responseType 为 {@code new TypeReference<Result<T>() {}}, 将抛出此异常
     * @since 1.0.0
     */
    @Override
    public <T> T executeForObject(@NotNull AgentRequest request, @NotNull TypeReference<?> responseType)
        throws AgentClientException, IllegalArgumentException {
        // 判断 responseType 是否为 new TypeReference<Result<T>() {}, 不支持这样包装方式
        ParameterizedType parameterizedType = (ParameterizedType) responseType.getType();
        Assertions.isFalse(Result.class.equals(parameterizedType.getRawType()), AgentBundle.message("result.type.error"));

        Result<T> result = this.getResultResponseEntity(request);
        return this.getGeneric(result, responseType, request.getUseSdk());
    }

    /**
     * 此接口返回的结果必须是 Result 封装的数据, 业务端需要判断结果是否为 null
     *
     * @param <T>          parameter
     * @param request      request
     * @param responseType response type
     * @return the t
     * @throws AgentClientException     agent client exception
     * @throws IllegalArgumentException illegal argument exception
     * @since 1.0.0
     * @deprecated 请使用 {@link AgentTemplate#executeForObject(AgentRequest, TypeReference)}
     */
    @Override
    @Deprecated
    public <T> T executeForObject(@NotNull AgentRequest request,
                                  @NotNull ParameterizedTypeReference<T> responseType)
        throws AgentClientException, IllegalArgumentException {
        ParameterizedType parameterizedType = (ParameterizedType) responseType.getType();
        Assertions.isTrue(Result.class.equals(parameterizedType.getRawType()), "ResonseType 必须为 Result<>");
        Assert.notNull(request.getMethod(), "未设置 HttpMethod");
        // 构建 url (http://gateway/服务名/agent)
        String url = this.buildUrl(request);
        // header 处理
        HttpEntity<?> httpEntity = this.processHeaders(request);
        // v5 返回的数据全部会被 Result 包装一次
        ResponseEntity<T> responseEntity = this.exchange(url,
                                                         request.getMethod(),
                                                         httpEntity,
                                                         responseType);
        log.debug("HTTP 原始响应结果: [{}]", responseEntity);

        // 如果 http code 非 200, 则由 handleResponse() 处理
        return responseEntity.getBody();
    }

    /**
     * 返回的结果(非泛型)最外层由 Result 包装.
     * 如果只需要关心 Result 而不需要使用 data, 请使用 {@code  Result<Void> result = agentTemplate.executeForResult(agentRequest, Void.class);}
     * example:
     * 1. 正确的方式:
     * {@code Result<User> result = agentTemplate.executeForResult(agentRequest, User.class);}
     * {@code Result<Void> result = agentTemplate.executeForResult(agentRequest, Void.class);}
     * 2. 错误的方式: {@code Result result = agentTemplate.executeForResult(agentRequest, Result.class);}
     *
     * @param <T>          parameter
     * @param request      request
     * @param responseType response type
     * @return the for entity
     * @throws AgentClientException     agent client exception
     * @throws IllegalArgumentException illegal argument exception
     * @since 1.0.0
     */
    @Override
    @NotNull
    public <T> Result<T> executeForResult(@NotNull AgentRequest request,
                                          Class<T> responseType)
        throws AgentClientException, IllegalArgumentException {
        Assertions.isFalse(Result.class.isAssignableFrom(responseType), "ResonseType 不能为 Result, "
                                                                        + "如果 data 为空或者不需要知道 data, 请使用 Void.class");

        Result<T> result = this.getResultResponseEntity(request);
        return this.getResult(result, responseType, request.getUseSdk());

    }

    /**
     * 返回的结果最外层由 Result 包装, 支持返回泛型.
     * 1. 正确的方式: {@code Result<List<User>> result = agentTemplate.executeForResult(agentRequest, new TypeReference<List<User>>() {});}
     * 2. 错误的方式: {@code Result<List<User>> result =
     * agentTemplate.executeForResult(agentRequest, new TypeReference<Result<List<User>>>() {});}**
     *
     * @param <T>          parameter
     * @param request      request
     * @param responseType response type
     * @return the result
     * @throws AgentClientException     agent client exception
     * @throws IllegalArgumentException illegal argument exception
     * @since 1.0.0
     */
    @Override
    @NotNull
    public <T> Result<T> executeForResult(@NotNull AgentRequest request,
                                          @NotNull TypeReference<?> responseType)
        throws AgentClientException, IllegalArgumentException {
        // 判断 responseType 是否为 new TypeReference<Result<T>() {}, 不支持这样包装方式
        ParameterizedType parameterizedType = (ParameterizedType) responseType.getType();
        Assertions.isFalse(Result.class.equals(parameterizedType.getRawType()), AgentBundle.message("result.type.error"));

        Result<T> result = this.getResultResponseEntity(request);
        return this.getResult(result, responseType, request.getUseSdk());
    }

    /**
     * 调用接口返回 ResponseEntity 包装后的 {@code Result<T>}, 此时的 T 类型是 LinkedHashMap, 为避免类型转换异常, 还需要再次处理.
     * http 请求成功后, 不管 Result 是成功还是失败, 都将返回原始数据, http 请求失败后, 将返回使用 Result 包装后的数据, data 为 null.
     *
     * @param <T>     parameter
     * @param request request
     * @return the result response entity
     * @since 1.0.0
     */
    private <T> Result<T> getResultResponseEntity(@NotNull AgentRequest request) {
        Assertions.notNull(request.getMethod(), "未设置 HttpMethod");
        // 构建 url (http://gateway/服务名/agent)
        String url = this.buildUrl(request);
        log.debug("url: {}", url);

        // header 处理
        HttpEntity<?> httpEntity = this.processHeaders(request);
        // v5 返回的数据全部会被 Result 包装一次
        ResponseEntity<Result<T>> responseEntity = this
            .exchange(url,
                      request.getMethod(),
                      httpEntity,
                      new ParameterizedTypeReference<Result<T>>() {});
        log.trace("agent response: \n[{}]", JsonUtils.toJson(responseEntity, true));

        // 如果 http 请求成功, 返回结果是 Result 类型的数据
        Result<T> result = responseEntity.getBody();
        // 强制验证返回的数据格式
        Assertions.notNull(result, "返回的数据格式错误: 未使用 Result 包装");
        return result;
    }

    /**
     * 再次处理泛型数据, 将 LinkedHashMap 转换为具体数据, 避免类型转换异常
     *
     * @param <T>          parameter
     * @param result       result
     * @param responseType response type
     * @param useSdk       use sdk
     * @return the t
     * @since 1.0.0
     */
    @Nullable
    private <T> T getGeneric(Result<T> result, Object responseType, boolean useSdk) {
        // result 返回失败时, 返回的 data 有可能为 null(生产环境) 或者是 ExceptionInfo, 这里直接返回 null
        if (Result.isFail(result)) {
            return null;
        }
        // 去除 Result 包装 (Result 包装的 (T)data 有可能为 null, 交由 业务端判断), 此时的 T 为 LinkedHashMap 类型, 需要再次处理
        T data = result.getData();
        return this.unpackData(data, responseType, useSdk);
    }

    /**
     * 泛型 Result 包装后的数据
     *
     * @param <T>          parameter
     * @param result       result
     * @param responseType response type
     * @param useSdk       use sdk
     * @return the result
     * @since 1.0.0
     */
    @NotNull
    private <T> Result<T> getResult(Result<T> result, Object responseType, boolean useSdk) {
        // result 返回失败时, 直接返回 result, 此时需要业务端判断 Result 结果
        if (Result.isFail(result)) {
            return result;
        }
        // 去除 Result 包装 (Result 包装的 (T)data 有可能为 null, 交由 业务端判断), 此时的 T 为 LinkedHashMap 类型, 需要再次处理
        T data = result.getData();
        data = this.unpackData(data, responseType, useSdk);
        result.setData(data);
        return result;
    }

    /**
     * data 解包处理.
     * 由于在 agent service 会对基础类型的数据进行二次包装, 这里为了减少应用端修改, 进行自动拆包处理.
     *
     * @param <T>          parameter
     * @param data         data
     * @param responseType response type
     * @param useSdk       use sdk
     * @return the t
     * @since 1.7.0
     */
    @SneakyThrows
    @SuppressWarnings(value = {"unchecked", "checkstyle:Indentation"})
    private <T> @Nullable T unpackData(T data, Object responseType, boolean useSdk) {
        T unpacked = null;
        if (data != null) {
            if (data instanceof LinkedHashMap) {
                Map<?, ?> dataMap = (Map<?, ?>) data;
                if (dataMap.size() == 0) {
                    return null;
                } else if (dataMap.containsKey(AgentConstant.DATA_VALUE)) {
                    Map<String, Object> tempData = (LinkedHashMap<String, Object>) data;

                    String value = String.valueOf(tempData.get(AgentConstant.DATA_VALUE));

                    if (StringUtils.isBlank(value) || StringPool.NULL.equals(value)) {
                        return null;
                    }

                    String valueType = String.valueOf(tempData.get(AgentConstant.DATA_TYPE));
                    if (String.class.getName().equals(valueType)) {
                        return (T) value;
                    } else if (useSdk) {
                        try {
                            Class<?> cached = responseTypeCache.computeIfAbsent(valueType,
                                                                                key -> ClassUtils.toClassConfident(valueType));
                            return (T) JsonUtils.parse(value, cached);
                        } catch (Exception e) {
                            log.error("SDK 中不存在 class: [{}]", valueType);
                            return JsonUtils.parse(value, responseType);
                        }
                    } else {
                        return JsonUtils.parse(value, responseType);
                    }
                }
            }
            // 重新使用 responseType 反序列化 data, 避免类型转换异常
            unpacked = JsonUtils.parse(JsonUtils.toJson(data), responseType);
        }
        return unpacked;
    }

    /**
     * Process headers http entity
     *
     * @param request request
     * @return the http entity
     * @since 1.0.0
     */
    @NotNull
    @Contract("_ -> new")
    @SuppressWarnings("PMD.RemoveCommentedCodeRule")
    private HttpEntity<?> processHeaders(@NotNull AgentRequest request) {
        // api 和 version 处理
        Assertions.notBlank(request.getApiName(), "请指明需要调用的 API");

        // 将 AgentRequest 中的 header 写入到 http header
        HttpHeaders httpHeaders = new HttpHeaders();
        Map<String, String> requestHeaders = request.getHeaders();
        if (!CollectionUtils.isEmpty(requestHeaders)) {
            requestHeaders.forEach(httpHeaders::add);
        }

        this.addSignatureHeader(request, httpHeaders);

        boolean isBodyParams = request.getMethod() == HttpMethod.POST
                               || request.getMethod() == HttpMethod.PUT
                               || request.getMethod() == HttpMethod.PATCH
                               || request.getMethod() == HttpMethod.DELETE;

        if (isBodyParams) {

            Object params = request.getParams();
            byte[] body = JsonUtils.EMPTY_ARRAY;
            if (params != null) {
                // 兼容 httpclient 不支持 patch 和 delete 无法使用 body 传参的问题
                if (request.getMethod() == HttpMethod.DELETE || request.getMethod() == HttpMethod.PATCH) {
                    request.setMethod(HttpMethod.PUT);
                }
                body = JsonUtils.toJsonAsBytes(params);
            }

            request.setDataLength(body.length);
            return new HttpEntity<>(body, httpHeaders);
        }

        return new HttpEntity<>(httpHeaders);
    }

    /**
     * 添加签名标识.
     *
     * @param request     request
     * @param httpHeaders http headers
     * @see SignatureInterceptor
     * @since 1.8.0
     */
    private void addSignatureHeader(@NotNull AgentRequest request, HttpHeaders httpHeaders) {
        if (request.isSignature()) {
            httpHeaders.add(AgentConstant.X_AGENT_NEED_SIGNATURE, String.valueOf(true));
        }
    }

    /**
     * 根据是否存在 endpoint 构建不同的 url (生产环境不使用 endpoint, 全部走网关)
     *
     * @param request request
     * @return the string
     * @since 1.7.0
     */
    private @NotNull String buildUrl(@NotNull AgentRequest request) {
        String customEndpoint = request.getCustomEndpoint();
        String url;
        if (BasicUtils.isLocalLaunch()
            && this.agentRestProperties.enableEndpoint
            && StringUtils.isNotBlank(customEndpoint)) {
            log.debug("本地开发且开启 endpoint, 使用自定义 endpoint: {}", customEndpoint);
            url = this.buildUrlByEndpoint(request);
            request.header(AgentConstant.X_AGENT_ENDPOINT, customEndpoint);
        } else {
            url = this.buildUrlByServiceName(request);
        }

        // 通过 path 参数构建 url
        if (StringUtils.hasText(request.getPathVariable())) {
            url += StringPool.SLASH + request.getPathVariable();
        }

        // GET 请求设置 url 中的 data 参数
        if (request.getMethod() == HttpMethod.GET) {
            Object params = request.getParams();
            url += StringPool.QUESTION_MARK + AgentConstant.GET_PARAM_NAME + StringPool.EQUALS;
            if (params != null) {
                // http://ip:port/[服务名]/agent[/pathVariable]?data=
                url += this.buildGetForm(request);
            }
        }
        return url.replace(ConfigKey.RibbonConfigKey.CLIENT_NAME, this.agentRestProperties.getServers());
    }

    /**
     * Process params string
     *
     * @param request request
     * @return the string
     * @since 1.0.0
     */
    @NotNull
    @SuppressWarnings("deprecation")
    private String buildUrlByServiceName(@NotNull AgentRequest request) {
        String finalUrl = GATEWAY_PREFIX;
        if (!StringUtils.hasText(request.getServiceName())) {
            throw new AgentClientException(AgentClientErrorCodes.NO_SERVICE_NAME_ERROR);
        }

        if (request.getParams() != null && StringUtils.hasText(request.getPathVariable())) {
            throw new AgentClientException(AgentClientErrorCodes.PARAMETER_TYPE_ERRORR);
        }

        // 是否使用网关路由, 主要区别是在开发时使用服务 ip:port/agent, 非开发环境使用网关 ip:port/serviceName/agent
        boolean enableRouter = this.agentRestProperties.isEnableRouter();

        String oldKey = ConfigKey.AgentConfigKey.REST_ENABLE_ROUTER;
        String key = ConfigKey.AgentConfigKey.GATEWAY_REST_ENABLE_ROUTER;
        boolean noGatewayConfig = StringUtils.isAllBlank(this.environment.getProperty(key), this.environment.getProperty(oldKey));
        if (BasicUtils.isV5Framework()
            && BasicUtils.isLocalLaunch()
            && noGatewayConfig) {
            enableRouter = false;

            log.warn("使用 V5 框架本地开发且未设置 [{}] 时, 自动配置 {}=false, 将忽略 serviceName 参数, 如果需要走网关请显式设置 {}=true",
                     key,
                     key,
                     key);
        }

        if (enableRouter) {
            finalUrl += request.getServiceName() + AGENT_SUFFIX;
        } else {
            finalUrl += AGENT_SUFFIX.replace(StringPool.SLASH, StringPool.EMPTY);
            if (!BasicUtils.isV5Framework()) {
                log.warn("已使用 {}=false 显式关闭路由功能, 将忽略 serviceName 参数,"
                         + " 可直接调用本地服务, 如需通过网关调用, 请删除此配置项或设置为 true (默认)",
                         ConfigKey.AgentConfigKey.GATEWAY_REST_ENABLE_ROUTER);
            }
        }

        return finalUrl;
    }

    /**
     * 通过 endpoint 构建的 url 不再走网关, 通过直连的方式调用
     *
     * @param request request
     * @return the string
     * @since 1.7.0
     */
    @NotNull
    private String buildUrlByEndpoint(@NotNull AgentRequest request) {
        String endpoint = request.getCustomEndpoint();
        endpoint = AgentUtils.addProtocol(endpoint);
        if (!endpoint.endsWith(AGENT_SUFFIX)) {
            endpoint += AGENT_SUFFIX;
        }
        return endpoint;
    }


    /**
     * get 参数先转换为 byte[], 再转为 base64
     *
     * @param request 请求参数
     * @return the string
     * @since 1.0.0
     */
    private @NotNull String buildGetForm(@NotNull AgentRequest request) {
        String data = AgentUtils.object2String(request.getParams());
        Integer dataLength = data.length();
        request.setDataLength(dataLength);

        if (dataLength >= this.agentRestProperties.getRequestMaxLineLength()) {
            log.warn("请求参数长度: [{}] 超过 GET 请求设置的最大长度: [{}], 自动转换为 POST 请求",
                     dataLength,
                     this.agentRestProperties.getRequestMaxLineLength());
            request.setMethod(HttpMethod.POST);
            return StringPool.EMPTY;
        }
        return data;
    }

    /**
     * Sets application context *
     *
     * @param applicationContext application context
     * @throws BeansException beans exception
     * @since 1.0.0
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
    }

    /**
     * 远程服务 http 调用成功但是返回结果不是 200 时处理逻辑
     *
     * @param request  request
     * @param response response
     * @throws IOException io exception
     * @since 1.6.0
     */
    private void handleResponse(ClientHttpRequest request,
                                @NotNull ClientHttpResponse response) throws IOException {
        ResponseErrorHandler errorHandler = this.getErrorHandler();
        boolean hasError = errorHandler.hasError(response);
        if (this.logger.isDebugEnabled()) {
            try {
                int code = response.getRawStatusCode();
                this.logger.debug("Response " + code);
            } catch (IOException ignored) {
            }
        }
        if (hasError) {
            // 获取主要的 header 信息
            HttpHeaders headers = request.getHeaders();
            String apiName = headers.getFirst(AgentConstant.X_AGENT_API);
            String version = headers.getFirst(AgentConstant.X_AGENT_VERSION);
            response.getHeaders().add(AgentConstant.X_AGENT_API, apiName);
            response.getHeaders().add(AgentConstant.X_AGENT_VERSION, version);
            errorHandler.handleError(response);
        }
    }

    /**
     * 重写的目的是使用自定义异常处理器, 传入 ClientHttpRequest, 避免在 {@link AgentResponseErrorHandler#handleError} 获取不到 request header 中的信息.
     * 当然也可用拦截器实现.
     *
     * @param <T>               parameter
     * @param url               url
     * @param method            method
     * @param requestCallback   request callback
     * @param responseExtractor response extractor
     * @return the t
     * @throws RestClientException rest client exception
     * @since 2.0.0
     */
    @Override
    @Nullable
    protected <T> T doExecute(@NotNull URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,
                              @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {

        Assert.notNull(url, "URI is required");
        Assert.notNull(method, "HttpMethod is required");
        ClientHttpResponse response = null;
        try {
            ClientHttpRequest request = this.createRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            response = request.execute();
            this.handleResponse(request, response);
            return (responseExtractor != null ? responseExtractor.extractData(response) : null);
        } catch (IOException ex) {
            String resource = url.toString();
            String query = url.getRawQuery();
            resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);

            String reason = "\nI/O error on " + method.name()
                            + " request for \""
                            + resource
                            + "\": "
                            + ex.getMessage();

            throw new ResourceAccessException("远程服务不可用, 请确认服务已启动并配置正确的连接: " + reason, ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
