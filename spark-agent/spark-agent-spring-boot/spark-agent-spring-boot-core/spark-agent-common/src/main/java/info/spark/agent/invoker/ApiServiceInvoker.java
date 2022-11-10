package info.spark.agent.invoker;

import com.google.common.collect.Maps;

import info.spark.agent.constant.AgentConstant;
import info.spark.agent.core.AbstractApiService;
import info.spark.agent.core.ApiService;
import info.spark.agent.core.ApiServiceContext;
import info.spark.agent.core.ApiServiceDefinition;
import info.spark.agent.entity.ApiExtend;
import info.spark.agent.entity.ApiServiceHeader;
import info.spark.agent.entity.ApiServiceRequest;
import info.spark.agent.enums.ProtocolType;
import info.spark.agent.plugin.ApiServiceCodec;
import info.spark.agent.plugin.ApiServiceExpandIdsCheck;
import info.spark.agent.plugin.ApiServiceReplayCheck;
import info.spark.agent.plugin.ApiServiceSignCheck;
import info.spark.agent.plugin.ApiServiceValidate;
import info.spark.agent.validation.ValidateMessage;
import info.spark.agent.validation.Validater;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.basic.util.TimeoutUtils;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.common.util.GsonUtils;
import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.api.R;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.core.util.DataTypeUtils;
import info.spark.starter.util.ObjectUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.core.util.UrlUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.MDC;
import org.springframework.util.StopWatch;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: api service 委托类, 调用 ApiService 的 service 或 headler </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 02:28
 * @since 1.0.0
 */
@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("all")
public class ApiServiceInvoker {
    /** Api service definition */
    private ApiServiceDefinition apiServiceDefinition;
    /** Service name */
    private String serviceName;
    /** 是否需要接口签名检查 */
    private Boolean signCheck;
    /** Nonce check */
    private Boolean nonceCheck;
    /** Enable fail fast */
    private Boolean enableFailFast;
    /** Has api extend */
    private Boolean hasApiExtend;
    /** Version */
    private String version;
    /** Service class */
    private Class<?> serviceClass;
    /** In */
    private Class<?> in;
    /** 入参泛型类型 */
    private Type inType;
    /** Out */
    private Class<?> out;
    /** 出参类型 */
    private Type outType;
    /** Method */
    private Method method;
    /** 出参入参序列化/反序列化协议 */
    private ProtocolType protocolType;
    /** Timeout */
    private Long timeout;
    /** Max */
    private Integer max;
    /** Api service codec */
    private ApiServiceCodec<Object, Object> apiServiceCodec;
    /** Api service validate */
    private ApiServiceValidate apiServiceValidate;
    /** Api service sign check */
    private ApiServiceSignCheck apiServiceSignCheck;
    /** Api service replay check */
    private ApiServiceReplayCheck apiServiceReplayCheck;
    /** Api service expand ids check */
    private ApiServiceExpandIdsCheck apiServiceExpandIdsCheck;
    /** url 参数正则解析 */
    private final Pattern URL_PARAMS_PATTERN = Pattern.compile("&?(\\w.+?)=(.+?)&");

    /**
     * apiName 格式:
     * 1. 全类名_version
     * 2. @ApiService.apiName.version
     *
     * @param serviceName service name
     * @param version     version
     * @return the string
     * @since 1.0.0
     */
    public static String name(String serviceName, String version) {
        return StringUtils.format("{}_{}", serviceName, version);
    }

    /**
     * Final name string
     *
     * @return the string
     * @since 1.0.0
     */
    public String finalName() {
        return name(this.serviceName, this.version);
    }

    /**
     * Invoke byte [ ]
     *
     * @param executor executor
     * @param data     data
     * @param extend   extend
     * @return the byte [ ]
     * @throws Exception exception
     * @see ApiServiceContext#findApiServiceInvoker(java.lang.String, java.lang.String)
     * @since 1.0.0
     */
    public byte[] invoke(ExecutorService executor,
                         byte[] data,
                         @NotNull ApiExtend extend) throws Exception {
        MDC.put(ConfigKey.LogSystemConfigKey.ROUTING_APPENDER_KEY, AgentConstant.AGENT_SERVICE);
        String dataToString = new String(data, Charsets.UTF_8);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 1. 前置处理
        data = this.preProcessor(data, dataToString);
        // 2. 参数处理
        Object inParam = this.parameProcessor(data, extend, dataToString);
        // 3. 调用业务逻辑
        Object outResult = this.execute(executor, inParam, extend);
        // 4. 返回结果处理
        byte[] bytes = this.responseProcessor(outResult);

        stopWatch.stop();
        log.trace("耗时: [{}ms] api: [{}] version: [{}] timeout: [{}] data length: [{}]", stopWatch.getTotalTimeMillis(),
                  this.serviceName,
                  this.version,
                  this.timeout,
                  data == null ? 0 : data.length);

        MDC.remove(ConfigKey.LogSystemConfigKey.ROUTING_APPENDER_KEY);
        return bytes;
    }

    /**
     * 前置处理: 将 url 参数转换为 json (不通过 AgentTemplate 调用时, 请求方式为 application/x-www-form-urlencoded)
     * example:
     * curl --location --request POST 'http://localhost:18080/auth-center/agent' \
     * --header 'X-Agent-Api: orization.token' \
     * --header 'X-Agent-Version: 1.0.0' \
     * --header 'Content-Type: application/x-www-form-urlencoded' \
     * --data-urlencode 'username=admin' \
     * --data-urlencode 'password=123456' \
     * --data-urlencode 'code=y66d' \
     * --data-urlencode 'uuid=a110db11c0dec878a559088f1e9b3bb5' \
     * --data-urlencode 'client_id=spark_dashboard' \
     * --data-urlencode 'client_secret=spark_dashboard' \
     * --data-urlencode 'grant_type=password' \
     * --data-urlencode 'scope=all'
     *
     * @param data         data
     * @param dataToString data to string
     * @return the byte [ ]
     * @since 1.7.0
     */
    private byte[] preProcessor(byte[] data, String dataToString) {
        // 如果未开启快速失败模式, 将在调用时检查参数是否正确
        if (!this.enableFailFast) {
            Assertions.notNull(this.in);
        }
        if (JsonUtils.isJson(dataToString)) {
            return data;
        }
        if (dataToString.contains(StringPool.EQUALS) || dataToString.contains(StringPool.AMPERSAND)) {
            Matcher matcher = this.URL_PARAMS_PATTERN.matcher(dataToString);
            if (matcher.find()) {
                // 将 url 参数转换为 json
                Map<String, String> stringStringMap = UrlUtils.buildMapByUrlParams(dataToString);
                return JsonUtils.toJsonAsBytes(stringStringMap);
            }
        }
        return data;
    }

    /**
     * 将 data 根据接口入参类型进行转换并做参数校验
     *
     * @param data         data
     * @param extend       extend
     * @param dataToString data to string
     * @return the object
     * @since 1.7.0
     */
    @Nullable
    private Object parameProcessor(byte[] data, @NotNull ApiExtend extend, String dataToString) {
        Object inParam = null;
        // 如果不是 Void 入参
        if (!Void.class.getTypeName().equals(this.inType.getTypeName())) {
            // 如果 data 为 null, 则说明业务端未传递参数, 则设置为 null, 交由参数校验逻辑处理.
            if (ObjectUtils.isEmpty(data)) {
                inParam = null;
            } else if (this.inType instanceof ParameterizedType) {
                // 如果是集合则使用 jackson 解析， 避免 gson 将 Integer/Long 默认序列化为 Double，暂时避免 fixme-dong4j 中的问题
                if (Collection.class.equals(((ParameterizedType) inType).getRawType())) {
                    log.trace("use JsonUtils: [{}] data: [{}]", this.in.getName(), dataToString);
                    inParam = this.apiServiceCodec.decode(data, this.in);
                } else {
                    // 如果入参是泛型, 则使用 GsonUtils 解析
                    // fixme-dong4j : (2021.01.25 20:56) [如果是集合类型且元素中的字段有 Object 时, 将导致 Integer/Long 默认反序列化为 Double]
                    log.trace("use GsonUtils: [{}] data: [{}]", this.inType.getTypeName(), dataToString);
                    inParam = GsonUtils.fromJson(dataToString, this.inType, true);
                }
            } else {
                log.trace("use JsonUtils: [{}]", this.in.getName());
                inParam = this.apiServiceCodec.decode(data, this.in);
            }

            log.trace("Request params: [{}]", JsonUtils.toJson(inParam));

            List<ValidateMessage> validateMessages =
                this.apiServiceValidate.validate(inParam,
                                                 Validater.getMethodParameterNamesByAnnotation(this.method));
            // 全局参数验证
            BaseCodes.PARAM_VERIFY_ERROR.isTrue(CollectionUtils.isEmpty(validateMessages),
                                                validateMessages
                                                    .stream()
                                                    .map(validateMessage -> StringUtils.format("[{}]: [{}]",
                                                                                               validateMessage.getProperty(),
                                                                                               validateMessage.getMessage()))
                                                    .collect(Collectors.joining(StringPool.COMMA)));

            extend.setMessages(validateMessages);
        }
        return inParam;
    }

    /**
     * 将最终的结果转换为 byte[]
     *
     * @param outResult out result
     * @return the byte [ ]
     * @since 1.7.0
     */
    private byte[] responseProcessor(Object outResult) {
        // 将响应结果 (使用 Result 包装)序列化为 byte[] todo-dong4j : (2019年12月31日 11:12) [使用泛型重构]
        Object finalResult = outResult;
        finalResult = this.processResult(finalResult);
        return this.apiServiceCodec.encode(finalResult);
    }

    /**
     * 处理最终的返回结果:
     * 1. 如果返回的是基础类型, 则包装为 map;
     * 2. 如果不是 Result 类型, 则包装为 Result;
     * <p>
     * 2021-10-29 13:01 dong4j
     * 如果返回 null 则直接包装为 R.succeed(), 不再判断是否为基础类型.
     *
     * @param finalResult final result
     * @return the object
     * @since 1.7.0
     */
    @NotNull
    private Object processResult(Object finalResult) {
        // 返回接口为 null 有 2 种情况, 1 是接口返回值定义为 Vold, 2 是返回的数据就是 null
        if (ObjectUtils.isNull(finalResult)) {
            finalResult = R.succeed();
        }
        // 如果是基础类型/基础类型数组/基础类型包装类/基础类型包装类型数组/List/Serializable 全部需要包装为 Map
        if (List.class.isAssignableFrom(out)
            || DataTypeUtils.isExtendPrimitive(this.method)
            || (Serializable.class.getName().equals(this.out.getName())
                // 目前只支持 id 为 Long/Integer/String, 所以这里只判断 3 个类型
                && (finalResult instanceof Long || finalResult instanceof String || finalResult instanceof Integer))) {
            finalResult = mapWrapper(finalResult);
            log.debug("预期返回类型为: [{}], 实际返回类型: [{}], 使用 Map 包装", this.out.getName(), finalResult.getClass().getName());
        } else {
            log.debug("返回结果类型为: [{}], 不需要 Map 包装", this.out.getName());
        }

        return resultWrapper(finalResult);
    }

    /**
     * 包装成 Result 实体
     *
     * @param finalResult final result
     * @return the object
     * @since 1.7.0
     */
    @NotNull
    private Object resultWrapper(@NotNull Object finalResult) {
        if (!Result.class.isAssignableFrom(finalResult.getClass())) {
            // 如果不是 Result 类型, 则包装为 Result
            finalResult = R.succeed(finalResult);
        }

        log.debug("finalResult: [{}]", finalResult);
        return finalResult;
    }

    /**
     * 将 list 或基础类型包装为 map
     *
     * @param finalResult final result
     * @return the object
     * @since 1.7.0
     */
    @NotNull
    private Object mapWrapper(@NotNull Object finalResult) {
        Map<String, String> map = Maps.newHashMapWithExpectedSize(2);

        if (Result.class.isAssignableFrom(finalResult.getClass())) {
            // 如果是 Result 包装的【基础类型或基础类型包装类或基础类型集合】 需要先取出data值再包装为Map
            Object resultData = ((Result) finalResult).getData();
            map.put(AgentConstant.DATA_VALUE, JsonUtils.toJson(resultData));
            map.put(AgentConstant.DATA_TYPE, resultData.getClass().getName());
        } else {
            map.put(AgentConstant.DATA_VALUE, JsonUtils.toJson(finalResult));
            map.put(AgentConstant.DATA_TYPE, finalResult.getClass().getName());
        }

        finalResult = R.succeed(map);
        return finalResult;
    }

    /**
     * 调用业务逻辑入口方法:
     * 1. 实现 ApiService 接口则调用 {@link ApiService#service(Object, ApiExtend)};
     * 2. 继承 AbstractApiService 则调用 {@link AbstractApiService#handler(Object, ApiExtend)},
     * 最终还是会调用子类的 {@link AbstractApiService#service(Object, ApiExtend)};
     * 3. 实现 ApiServiceDefinition 接口则调用指定的通过 @ApiServiceMethod 标识的方法, 方法签名确保正确, 否则将抛出异常.
     *
     * @param executor executor
     * @param in       入参
     * @param extend   扩展参数
     * @return the object       出参
     * @throws Exception exception
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    private Object execute(ExecutorService executor,
                           Object in,
                           @NotNull ApiExtend extend) throws Exception {

        writeStack(extend);

        // 从 header 获取 timeout, 如果不存在则使用服务端配置
        long finalTimeout = timeout;
        final String timeoutFromHeader = extend.getHeader().getHeaders().get(AgentConstant.X_AGENT_TIMEOUT);
        if (StringUtils.isNotBlank(timeoutFromHeader)) {
            finalTimeout = Long.valueOf(timeoutFromHeader);
        }

        return TimeoutUtils.process(executor,
                                    () -> {return execute(in, extend);},
                                    finalTimeout,
                                    TimeUnit.MILLISECONDS);
    }

    /**
     * Gets object *
     *
     * @param in     in
     * @param extend extend
     * @return the object
     * @throws IllegalAccessException    illegal access exception
     * @throws InvocationTargetException invocation target exception
     * @since 2.1.0
     */
    private Object execute(Object in, @NotNull ApiExtend extend) throws IllegalAccessException, InvocationTargetException {
        if (ApiService.class.isAssignableFrom(serviceClass)) {
            ApiService<Object, Object> service = (ApiService<Object, Object>) apiServiceDefinition;
            return hasApiExtend ? service.service(in, extend) : service.service(in);
        } else {
            return hasApiExtend
                   ? method.invoke(apiServiceDefinition, in, extend)
                   : method.invoke(apiServiceDefinition, in);
        }
    }

    /**
     * Write stack
     *
     * @param extend extend
     * @since 1.8.0
     */
    private void writeStack(@NotNull ApiExtend extend) {
        final Map<String, String> headers = extend.getHeader().getHeaders();

        // info.spark.gateway.filter.TokenFilter#buildAgentExchange 网关写入的
        final String original = headers.get(BasicConstant.X_GATEWAY_ORIGINAL);
        final String router = headers.get(BasicConstant.X_GATEWAY_ROUTER);
        // info.spark.agent.adapter.client.AgentTemplate.buildUrl 写入
        final String endpoint = headers.get(AgentConstant.X_AGENT_ENDPOINT);
        // info.spark.agent.adapter.client.AgentTemplate.processHeaders 写入
        final String clientHost = headers.get(AgentConstant.X_AGENT_HOST);
        // info.spark.agent.adapter.interceptor.ApplicationNameInterceptor.intercept 写入
        final String clientApplicationName = headers.get(AgentConstant.X_AGENT_APPNAME);
        // info.spark.agent.adapter.interceptor.ClientIdInterceptor.intercept 写入或者是 auth 组件
        final String clientId = headers.get(AgentConstant.X_AGENT_APPID);

        String stack = StringUtils.format("\n\n"
                                          + "[{}] -> [{}]::[{}]::[{}@{}@{}]\n"
                                          + "\t└── {}\n"
                                          + "\t\t└── {}\n"
                                          + "\t\t\t└── {}\n"
                                          + "\t\t\t\t└── {} ms\n"
                                          + "\t\t\t\t\t└── {}",
                                          original,
                                          router,
                                          endpoint,
                                          clientHost,
                                          clientApplicationName,
                                          ConfigKit.getAppName() + "_" + ConfigKit.getAppVersion(),
                                          serviceClass.getName(),
                                          method.toString(),
                                          finalName(),
                                          this.timeout);

        MDC.put(AgentConstant.EXECUTE_AGENT_SERVICE_ID, stack);
    }

    /**
     * To string string
     *
     * @return the string
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "ApiServiceInvoker{"
               + "serviceName='"
               + this.serviceName
               + '\''
               + ", version='"
               + this.version
               + '\''
               + ", serviceClass="
               + this.serviceClass
               + ", in="
               + this.in
               + ", out="
               + this.out
               + ", method="
               + this.method
               + ", protocolType="
               + this.protocolType
               + ", timeout="
               + this.timeout
               + ", max="
               + this.max
               + '}';
    }

    /**
     * 接口签名检查和重放攻击检查
     *
     * @param apiServiceHeader  api service header
     * @param apiServiceRequest api service request
     * @since 1.6.0
     */
    public void check(@NotNull ApiServiceHeader apiServiceHeader, @NotNull ApiServiceRequest apiServiceRequest) {
        if (this.nonceCheck) {
            this.apiServiceReplayCheck.check(apiServiceHeader, apiServiceRequest);
        }
        if (this.signCheck) {
            this.apiServiceSignCheck.check(apiServiceHeader, apiServiceRequest);
        }
        this.apiServiceExpandIdsCheck.check(apiServiceHeader, apiServiceRequest);
    }


}
