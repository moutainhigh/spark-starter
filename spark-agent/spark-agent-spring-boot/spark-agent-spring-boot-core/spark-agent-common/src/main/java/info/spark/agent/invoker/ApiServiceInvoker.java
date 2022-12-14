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
 * <p>Description: api service ?????????, ?????? ApiService ??? service ??? headler </p>
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
    /** ?????????????????????????????? */
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
    /** ?????????????????? */
    private Type inType;
    /** Out */
    private Class<?> out;
    /** ???????????? */
    private Type outType;
    /** Method */
    private Method method;
    /** ?????????????????????/?????????????????? */
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
    /** url ?????????????????? */
    private final Pattern URL_PARAMS_PATTERN = Pattern.compile("&?(\\w.+?)=(.+?)&");

    /**
     * apiName ??????:
     * 1. ?????????_version
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

        // 1. ????????????
        data = this.preProcessor(data, dataToString);
        // 2. ????????????
        Object inParam = this.parameProcessor(data, extend, dataToString);
        // 3. ??????????????????
        Object outResult = this.execute(executor, inParam, extend);
        // 4. ??????????????????
        byte[] bytes = this.responseProcessor(outResult);

        stopWatch.stop();
        log.trace("??????: [{}ms] api: [{}] version: [{}] timeout: [{}] data length: [{}]", stopWatch.getTotalTimeMillis(),
                  this.serviceName,
                  this.version,
                  this.timeout,
                  data == null ? 0 : data.length);

        MDC.remove(ConfigKey.LogSystemConfigKey.ROUTING_APPENDER_KEY);
        return bytes;
    }

    /**
     * ????????????: ??? url ??????????????? json (????????? AgentTemplate ?????????, ??????????????? application/x-www-form-urlencoded)
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
        // ?????????????????????????????????, ???????????????????????????????????????
        if (!this.enableFailFast) {
            Assertions.notNull(this.in);
        }
        if (JsonUtils.isJson(dataToString)) {
            return data;
        }
        if (dataToString.contains(StringPool.EQUALS) || dataToString.contains(StringPool.AMPERSAND)) {
            Matcher matcher = this.URL_PARAMS_PATTERN.matcher(dataToString);
            if (matcher.find()) {
                // ??? url ??????????????? json
                Map<String, String> stringStringMap = UrlUtils.buildMapByUrlParams(dataToString);
                return JsonUtils.toJsonAsBytes(stringStringMap);
            }
        }
        return data;
    }

    /**
     * ??? data ??????????????????????????????????????????????????????
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
        // ???????????? Void ??????
        if (!Void.class.getTypeName().equals(this.inType.getTypeName())) {
            // ?????? data ??? null, ?????????????????????????????????, ???????????? null, ??????????????????????????????.
            if (ObjectUtils.isEmpty(data)) {
                inParam = null;
            } else if (this.inType instanceof ParameterizedType) {
                // ???????????????????????? jackson ????????? ?????? gson ??? Integer/Long ?????????????????? Double??????????????? fixme-dong4j ????????????
                if (Collection.class.equals(((ParameterizedType) inType).getRawType())) {
                    log.trace("use JsonUtils: [{}] data: [{}]", this.in.getName(), dataToString);
                    inParam = this.apiServiceCodec.decode(data, this.in);
                } else {
                    // ?????????????????????, ????????? GsonUtils ??????
                    // fixme-dong4j : (2021.01.25 20:56) [????????????????????????????????????????????? Object ???, ????????? Integer/Long ????????????????????? Double]
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
            // ??????????????????
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
     * ??????????????????????????? byte[]
     *
     * @param outResult out result
     * @return the byte [ ]
     * @since 1.7.0
     */
    private byte[] responseProcessor(Object outResult) {
        // ??????????????? (?????? Result ??????)???????????? byte[] todo-dong4j : (2019???12???31??? 11:12) [??????????????????]
        Object finalResult = outResult;
        finalResult = this.processResult(finalResult);
        return this.apiServiceCodec.encode(finalResult);
    }

    /**
     * ???????????????????????????:
     * 1. ??????????????????????????????, ???????????? map;
     * 2. ???????????? Result ??????, ???????????? Result;
     * <p>
     * 2021-10-29 13:01 dong4j
     * ???????????? null ?????????????????? R.succeed(), ?????????????????????????????????.
     *
     * @param finalResult final result
     * @return the object
     * @since 1.7.0
     */
    @NotNull
    private Object processResult(Object finalResult) {
        // ??????????????? null ??? 2 ?????????, 1 ??????????????????????????? Vold, 2 ???????????????????????? null
        if (ObjectUtils.isNull(finalResult)) {
            finalResult = R.succeed();
        }
        // ?????????????????????/??????????????????/?????????????????????/??????????????????????????????/List/Serializable ????????????????????? Map
        if (List.class.isAssignableFrom(out)
            || DataTypeUtils.isExtendPrimitive(this.method)
            || (Serializable.class.getName().equals(this.out.getName())
                // ??????????????? id ??? Long/Integer/String, ????????????????????? 3 ?????????
                && (finalResult instanceof Long || finalResult instanceof String || finalResult instanceof Integer))) {
            finalResult = mapWrapper(finalResult);
            log.debug("?????????????????????: [{}], ??????????????????: [{}], ?????? Map ??????", this.out.getName(), finalResult.getClass().getName());
        } else {
            log.debug("?????????????????????: [{}], ????????? Map ??????", this.out.getName());
        }

        return resultWrapper(finalResult);
    }

    /**
     * ????????? Result ??????
     *
     * @param finalResult final result
     * @return the object
     * @since 1.7.0
     */
    @NotNull
    private Object resultWrapper(@NotNull Object finalResult) {
        if (!Result.class.isAssignableFrom(finalResult.getClass())) {
            // ???????????? Result ??????, ???????????? Result
            finalResult = R.succeed(finalResult);
        }

        log.debug("finalResult: [{}]", finalResult);
        return finalResult;
    }

    /**
     * ??? list ???????????????????????? map
     *
     * @param finalResult final result
     * @return the object
     * @since 1.7.0
     */
    @NotNull
    private Object mapWrapper(@NotNull Object finalResult) {
        Map<String, String> map = Maps.newHashMapWithExpectedSize(2);

        if (Result.class.isAssignableFrom(finalResult.getClass())) {
            // ????????? Result ???????????????????????????????????????????????????????????????????????? ???????????????data???????????????Map
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
     * ??????????????????????????????:
     * 1. ?????? ApiService ??????????????? {@link ApiService#service(Object, ApiExtend)};
     * 2. ?????? AbstractApiService ????????? {@link AbstractApiService#handler(Object, ApiExtend)},
     * ?????????????????????????????? {@link AbstractApiService#service(Object, ApiExtend)};
     * 3. ?????? ApiServiceDefinition ?????????????????????????????? @ApiServiceMethod ???????????????, ????????????????????????, ?????????????????????.
     *
     * @param executor executor
     * @param in       ??????
     * @param extend   ????????????
     * @return the object       ??????
     * @throws Exception exception
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    private Object execute(ExecutorService executor,
                           Object in,
                           @NotNull ApiExtend extend) throws Exception {

        writeStack(extend);

        // ??? header ?????? timeout, ???????????????????????????????????????
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

        // info.spark.gateway.filter.TokenFilter#buildAgentExchange ???????????????
        final String original = headers.get(BasicConstant.X_GATEWAY_ORIGINAL);
        final String router = headers.get(BasicConstant.X_GATEWAY_ROUTER);
        // info.spark.agent.adapter.client.AgentTemplate.buildUrl ??????
        final String endpoint = headers.get(AgentConstant.X_AGENT_ENDPOINT);
        // info.spark.agent.adapter.client.AgentTemplate.processHeaders ??????
        final String clientHost = headers.get(AgentConstant.X_AGENT_HOST);
        // info.spark.agent.adapter.interceptor.ApplicationNameInterceptor.intercept ??????
        final String clientApplicationName = headers.get(AgentConstant.X_AGENT_APPNAME);
        // info.spark.agent.adapter.interceptor.ClientIdInterceptor.intercept ??????????????? auth ??????
        final String clientId = headers.get(AgentConstant.X_AGENT_APPID);

        String stack = StringUtils.format("\n\n"
                                          + "[{}] -> [{}]::[{}]::[{}@{}@{}]\n"
                                          + "\t????????? {}\n"
                                          + "\t\t????????? {}\n"
                                          + "\t\t\t????????? {}\n"
                                          + "\t\t\t\t????????? {} ms\n"
                                          + "\t\t\t\t\t????????? {}",
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
     * ???????????????????????????????????????
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
