package info.spark.agent.core;

import com.google.common.collect.Maps;

import info.spark.agent.AbstractAgentService;
import info.spark.agent.annotation.ApiServiceMethod;
import info.spark.agent.annotation.EnableAgentService;
import info.spark.agent.constant.AgentConstant;
import info.spark.agent.entity.ApiExtend;
import info.spark.agent.enums.ModeType;
import info.spark.agent.enums.ProtocolType;
import info.spark.agent.exception.AgentServiceException;
import info.spark.agent.invoker.ApiServiceInvoker;
import info.spark.agent.plugin.ApiServiceCodec;
import info.spark.agent.plugin.ApiServiceExpandIdsCheck;
import info.spark.agent.plugin.ApiServiceReplayCheck;
import info.spark.agent.plugin.ApiServiceSignCheck;
import info.spark.agent.plugin.ApiServiceValidate;
import info.spark.agent.plugin.Plugin;
import info.spark.agent.register.AgentServiceRegistrar;
import info.spark.agent.register.Ready;
import info.spark.agent.register.Registry;
import info.spark.agent.sender.AgentService;
import info.spark.starter.util.core.api.BaseCodes;

import info.spark.starter.basic.context.ComponentThreadLocal;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.event.AgentRegisteredEvent;
import info.spark.starter.common.exception.StarterException;
import info.spark.starter.util.ClassUtils;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.util.ThreadUtils;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: ?????????????????????, ?????????????????????(????????????????????? IoC) </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 02:27
 * @since 1.0.0
 */
@Data
@Slf4j
public class ApiServiceContext {
    /** ?????? X-Agent-Api ??? invoker ??????????????? */
    private static final ConcurrentMap<String, ApiServiceInvoker> API_SERVICES = Maps.newConcurrentMap();
    /** ?????? X-Agent-Api code?????? ??? invoker ??????????????? */
    private static final ConcurrentMap<String, ApiServiceInvoker> CODE_SERVICES = Maps.newConcurrentMap();
    /** ???????????????, ????????????, ???????????? */
    private static final Map<Class<?>, Plugin> PLUGINS = Maps.newHashMapWithExpectedSize(8);
    /** Application context */
    private ApplicationContext applicationContext;
    /** REENTRANT_LOCK */
    private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();
    /** Executor service */
    private ExecutorService executorService = null;
    /** Working */
    private AtomicInteger working = new AtomicInteger(0);
    /** Registry */
    private Registry registry;
    /** Agent properties */
    private AgentProperties agentProperties;
    /** todo-dong4j : (2020???01???31??? 16:27) [??????????????????] */
    private Integer max = 1;
    /** Shutdown */
    private boolean shutdown = false;
    /** Mode */
    private ModeType mode = ModeType.LOCAL;
    /** NOT_ALLOWED_PARAMETER */
    private static final Set<String> NOT_ALLOWED_PARAMETER = new HashSet<String>(4) {
        private static final long serialVersionUID = 860424951913475865L;

        {
            this.add("com.alibaba.fastjson.JSONObject");
            this.add("java.util.Map");
            this.add("java.util.HashMap");
        }
    };

    /**
     * ??????????????????
     *
     * @param agentProperties agent properties
     * @return the instance
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static ApiServiceContext getInstance(AgentProperties agentProperties) {
        return ApiServiceContextSingleton.INSTANCE.getApiServiceContext(agentProperties);
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2019.12.30 18:21
     * @since 1.0.0
     */
    private enum ApiServiceContextSingleton {
        /** Instance api service context singleton */
        INSTANCE;

        /** Api service context */
        private final transient ApiServiceContext apiServiceContext;

        /**
         * Api service context singleton
         *
         * @since 1.0.0
         */
        @Contract(pure = true)
        ApiServiceContextSingleton() {
            this.apiServiceContext = new ApiServiceContext();
        }

        /**
         * Gets api service context *
         *
         * @param agentProperties agent properties
         * @return the api service context
         * @since 1.0.0
         */
        @Contract(pure = true)
        public ApiServiceContext getApiServiceContext(AgentProperties agentProperties) {
            this.apiServiceContext.setAgentProperties(agentProperties);
            return this.apiServiceContext;
        }
    }

    /**
     * ???????????????
     *
     * @param ready ready
     * @since 1.0.0
     */
    public void ready(Ready ready) {
        REENTRANT_LOCK.lock();
        try {
            // ???????????????
            if (ready != null) {
                ready.ready();
            }
            // ??????????????????
            this.registryReady();
            // ??????????????????
            this.registerAllApiService();
        } finally {
            REENTRANT_LOCK.unlock();
        }
    }

    /**
     * Registry ready
     *
     * @since 1.0.0
     */
    private void registryReady() {
        if (!ModeType.LOCAL.equals(this.mode)) {
            throw new AgentServiceException("????????????????????????");
        }
    }

    /**
     * ??? IoC ?????????????????? {@link ApiServiceDefinition} ??????,
     * ????????????????????? {@link EnableAgentService} ????????? {@link info.spark.agent.annotation.ApiService} ???????????? IoC ???,
     * ??????????????? {@link AgentServiceRegistrar}, ???????????? bean ?????? invoker.
     *
     * @since 1.0.0
     */
    private void registerAllApiService() {
        // ????????????????????????????????????????????????????????????????????????????????? Bean
        Map<String, ApiServiceDefinition> maps = this.applicationContext.getBeansOfType(ApiServiceDefinition.class);
        log.debug("????????? IoC ??? ApiService bean: \n[{}]", maps.size());
        if (maps.isEmpty()) {
            log.warn("????????? ApiServiceDefinition ?????????");
            return;
        }

        Collection<ApiServiceDefinition> apiServiceDefinitions = maps.values();

        StopWatch sw = new StopWatch();
        sw.start("registerInvokers-task");
        apiServiceDefinitions.forEach(this::registerInvokers);
        sw.stop();
        log.debug("Finish register all service spend time: [{}]", sw.prettyPrint());

        if (ModeType.REMOTE.equals(this.mode)) {
            throw new AgentServiceException("????????????????????????");
        }

        Set<String> valueSet = new HashSet<>(API_SERVICES.keySet());
        Set<String> codeSet = new HashSet<>(CODE_SERVICES.keySet());
        // todo-dong4j : (2020.09.8 18:35) [????????????????????????, ??????????????? agent api ??????]
        Map<String, Set<String>> allApi = Maps.newHashMapWithExpectedSize(2);
        allApi.put(ComponentThreadLocal.AGENT_API, valueSet);
        allApi.put(ComponentThreadLocal.AGENT_CODE, codeSet);
        AgentRegisteredEvent event = new AgentRegisteredEvent(allApi);
        this.applicationContext.publishEvent(event);
        log.trace("agent service ????????????, ?????? [{}] ??????", AgentRegisteredEvent.class);
        ComponentThreadLocal.context().set(new HashMap<String, Object>(2) {
            private static final long serialVersionUID = 860424951913475865L;

            {
                this.put(ComponentThreadLocal.AGENT_SERVICES, allApi);
            }
        });
        // todo-dong4j : (2021.01.31 18:56) [??????????????? zookeeper, ???????????? (???????????? zk ??????)]
    }

    /**
     * ???????????? ApiServiceDefinition ???????????????
     * 1. ????????? @ApiService ????????? class
     * 2. ????????? @ApiServiceMethod ???????????????
     *
     * @param apiServiceDefinition api service definition
     * @see AbstractApiService#handler
     * @since 1.0.0
     */
    private void registerInvokers(ApiServiceDefinition apiServiceDefinition) {
        // ?????? invoker ???????????????, ?????????????????????????????????, ?????????????????????????????? class
        Class<?> originalClass = this.getOriginalClass(apiServiceDefinition);
        info.spark.agent.annotation.ApiService apiService = this.checkClass(originalClass);

        String apiName = apiService.apiName();
        if (StringUtils.isBlank(apiName)) {
            // ?????????????????? apiName, ??????????????????
            apiName = originalClass.getName();
        }

        // ???????????? @ApiServiceMethod ???????????????, ??????????????????
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(originalClass, ApiServiceMethod.class, true, true);
        // ???????????????????????????????????????????????? ICrudDelegate ????????????????????? 2 ???????????? method??????
        methods = methods.stream().filter(m -> !m.isBridge()).collect(Collectors.toList());

        // ?????? class ??? ApiService ??????????????????, ??????????????? info.spark.agent.core.AbstractApiService.service ??????
        if (ApiService.class.isAssignableFrom(originalClass)) {
            if (CollectionUtils.isNotEmpty(methods)) {
                throw new AgentServiceException("[{}] ????????????: ?????? ApiService ????????????????????? @ApiServiceMethod ??????, ????????????????????????",
                                                originalClass);
            }

            this.registryHandler(apiServiceDefinition, originalClass, apiService, apiName);
        } else {
            // bean ??? @ApiService ?????????????????? ApiServiceDefinition ??????, ??????????????????????????? @ApiServiceMethod
            if (CollectionUtils.isEmpty(methods) && this.agentProperties.getEndpoint().isEnableFailFast()) {
                throw new AgentServiceException("[{}] ????????????: ?????? ApiServiceDefinition ??????, "
                                                + "???????????? @ApiServiceMethod ??????, ????????????????????????", originalClass);
            }
            if (!this.checkPublicMethod(originalClass, methods)) {
                // ???????????????????????? public ?????????????????????
                throw new StarterException("[{}] ????????????: @ApiServiceMethod ???????????????????????? public ??????", originalClass.getName());
            }
            this.registryMethod(apiServiceDefinition, originalClass, apiService, apiName, methods);
        }
    }

    /**
     * ?????????????????? ApiService, ????????? handler()/service()
     *
     * @param apiServiceDefinition api service definition
     * @param originalClass        original class
     * @param apiService           api service
     * @param apiName              api name
     * @since 1.6.0
     */
    private void registryHandler(ApiServiceDefinition apiServiceDefinition,
                                 Class<?> originalClass,
                                 info.spark.agent.annotation.ApiService apiService,
                                 String apiName) {

        String methodName;
        Method method;
        // AbstractApiService ????????????
        if (AbstractApiService.class.isAssignableFrom(originalClass)) {
            methodName = AgentConstant.TMP_METHOD_SERVICE;
            log.debug("[{}] ?????? AbstractApiService , ?????????????????????: [{}]", originalClass, methodName);
        } else {
            // ApiService ???????????????
            methodName = AgentConstant.TMP_METHOD_HANDLER;
            log.warn("???????????? AbstractApiService ??????????????????. [{}] ?????? ApiService , ?????????????????????: [{}]", originalClass, methodName);
        }

        // ?????????????????? handler()/service() ??????, agent ?????????????????????????????????????????????.
        method = MethodUtils.getMatchingMethod(originalClass,
                                               methodName,
                                               // ??????????????????
                                               findGenericType(originalClass),
                                               ApiExtend.class);

        long timeout = apiService.timeout() == 0L ? this.agentProperties.getEndpoint().getRequestTimeout().toMillis() :
                       apiService.timeout();

        this.registerInvoker(apiServiceDefinition,
                             method,
                             apiService.protocol(),
                             method.getParameterTypes()[0],
                             getType(apiServiceDefinition.getClass(), method),
                             // ??????????????????
                             method.getReturnType(),
                             method.getGenericReturnType(),
                             timeout,
                             this.max,
                             apiName,
                             apiService.version(),
                             apiService.sign(),
                             apiService.nonce());
    }

    /**
     * ?????????????????? ApiServiceDefinition, ???????????? @ApiServiceMethod ???????????????
     *
     * @param apiServiceDefinition api service definition
     * @param originalClass        original class
     * @param apiService           api service
     * @param apiName              api name
     * @param methods              methods
     * @since 1.6.0
     */
    private void registryMethod(ApiServiceDefinition apiServiceDefinition,
                                Class<?> originalClass,
                                info.spark.agent.annotation.ApiService apiService,
                                String apiName,
                                @NotNull List<Method> methods) {

        methods.forEach(method -> {
            ApiServiceMethod apiServiceMethod = method.getAnnotation(ApiServiceMethod.class);
            String methodApiServiceName = apiName;
            if (StringUtils.isBlank(apiServiceMethod.value())) {
                methodApiServiceName += "." + method.getName();
            } else {
                methodApiServiceName += "." + apiServiceMethod.value();
            }

            String finalName = methodApiServiceName + StringPool.UNDERSCORE + apiServiceMethod.version();
            // apiName.value_version????????????
            if (!API_SERVICES.containsKey(finalName)) {
                this.registerDefaultCache(apiServiceDefinition, originalClass, apiService, method, methodApiServiceName);
            }
            // code.code_version ?????????????????????????????????????????????????????????????????????API_SERVICE????????????CODE_SERVICE??????????????????
            // 1. ??????class???method????????????code?????????????????????code map???
            if (!this.ignoreCode(apiService, apiServiceMethod)
                // 2. ??????1??????????????????????????????????????????class???method ????????????????????????code??????????????????????????????
                && this.assertSurvives(apiService, apiServiceMethod, originalClass, method)) {
                ApiServiceInvoker apiServiceInvoker = API_SERVICES.get(finalName);
                // 3. ???invoker?????????????????????code map ???
                this.registerCodeCache(apiService, apiServiceMethod, apiServiceInvoker, apiServiceDefinition);
            }
        });
    }

    /**
     * Register code cache
     *
     * @param apiService           api service
     * @param apiServiceMethod     api service method
     * @param apiServiceInvoker    api service invoker
     * @param apiServiceDefinition api service definition
     * @since 1.8.0
     */
    private void registerCodeCache(info.spark.agent.annotation.ApiService apiService,
                                   ApiServiceMethod apiServiceMethod,
                                   ApiServiceInvoker apiServiceInvoker,
                                   ApiServiceDefinition apiServiceDefinition) {
        String apiName = apiService.code() + StringPool.DOT + apiServiceMethod.code();
        String key = apiName + StringPool.UNDERSCORE + apiServiceMethod.version();
        if (CODE_SERVICES.containsKey(key)) {
            throw new AgentServiceException("?????????????????? apiName: [{}] version [{}] class: [{}]",
                                            apiService.code() + StringPool.DOT + apiServiceMethod.code(),
                                            apiServiceMethod.version(),
                                            apiServiceDefinition.getClass());
        }
        // ?????? serviceName
        apiServiceInvoker.setServiceName(apiName);
        // ????????????????????? API_SERVICE ????????????????????????????????????????????????
        CODE_SERVICES.put(key, apiServiceInvoker);
    }

    /**
     * Register default cache
     *
     * @param apiServiceDefinition api service definition
     * @param originalClass        original class
     * @param apiService           api service
     * @param method               method
     * @param methodApiServiceName method api service name
     * @since 1.8.0
     */
    @SuppressWarnings(value = {"PMD.UndefineMagicConstantRule", "PMD.AvoidComplexConditionRule"})
    private void registerDefaultCache(ApiServiceDefinition apiServiceDefinition,
                                      Class<?> originalClass,
                                      info.spark.agent.annotation.ApiService apiService,
                                      Method method,
                                      String methodApiServiceName) {
        ApiServiceMethod apiServiceMethod = method.getAnnotation(ApiServiceMethod.class);
        Type genericParameterType = method.getGenericParameterTypes()[0];
        Class<?> parameterType = method.getParameterTypes()[0];
        // ??????????????????????????????[?????????????????????????????????]
        if (Modifier.isAbstract(parameterType.getModifiers())
            && !Modifier.isInterface(parameterType.getModifiers())
            && !parameterType.isPrimitive()) {
            // ????????????????????????
            // todo-dong4j : (2021-10-14 20:0) [??????, ??????????????????????????? AbstractAgentService ?????????????????????, 0:Service, 1: Query, 2: DTO]
            if (isAbstractAgentService(originalClass, method)) {
                genericParameterType = ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[2];
                parameterType = (Class<?>) ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[2];
            } else if (AbstractAgentService.class.isAssignableFrom(originalClass)) {
                // ????????? AbstractAgentService ??????????????????????????? ???????????????
                genericParameterType = ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[1];
                parameterType = (Class<?>) ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[1];
            } else {
                genericParameterType = ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[0];
                parameterType = (Class<?>) ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[0];
            }
        }

        Class<?> outClass = method.getReturnType();
        Type outType = method.getGenericReturnType();

        // ApiService sign ??? true ???, ??????????????????????????????, ???????????? ApiServiceMethod sign ??????
        boolean signCheck = apiServiceMethod.sign() || apiService.sign();
        boolean nonceCheck = apiServiceMethod.nonce() || apiService.nonce();

        long defaultRequestTimeout = this.agentProperties.getEndpoint().getRequestTimeout().toMillis();

        long timeout = apiServiceMethod.timeout() == 0L
                       ? (apiService.timeout() == 0L ? defaultRequestTimeout : apiService.timeout())
                       : apiServiceMethod.timeout();

        this.registerInvoker(apiServiceDefinition,
                             method,
                             apiServiceMethod.protocol(),
                             parameterType,
                             genericParameterType,
                             outClass,
                             outType,
                             timeout,
                             this.max,
                             // ????????? api ?????? ???????????????
                             methodApiServiceName,
                             // ????????? api ?????????????????? version, ???????????? version ????????????
                             apiServiceMethod.version(),
                             signCheck,
                             nonceCheck);
    }

    /**
     * ??????class???method????????????code ?????? true
     *
     * @param apiService       api service
     * @param apiServiceMethod api service method
     * @return the boolean
     * @since 1.8.0
     */
    private boolean ignoreCode(info.spark.agent.annotation.ApiService apiService, ApiServiceMethod apiServiceMethod) {
        return StringUtils.isBlank(apiService.code()) && StringUtils.isBlank(apiServiceMethod.code());
    }

    /**
     * ?????????code???????????????????????????class???method ????????????????????????code??????????????????????????????
     *
     * @param apiService       api service
     * @param apiServiceMethod api service method
     * @param originalClass    originalClass
     * @param method           method
     * @return the boolean
     * @since 1.8.0
     */
    private boolean assertSurvives(info.spark.agent.annotation.ApiService apiService,
                                   ApiServiceMethod apiServiceMethod,
                                   Class<?> originalClass,
                                   Method method) {
        if (!(StringUtils.isNotBlank(apiService.code()) && StringUtils.isNotBlank(apiServiceMethod.code()))) {
            throw new AgentServiceException("\n ????????? [{}]#[{}] ApiService.code ???ApiServiceMethod.code ?????????????????????????????? \n",
                                            originalClass.getName(), method.getName());
        }
        return true;
    }

    /**
     * ?????? apiservivce bean ?????? invoker, ?????? map ??????????????????,
     * ??????????????????????????? header ?????? {@link AgentConstant#X_AGENT_API} ??? {@link AgentConstant#X_AGENT_VERSION} ?????? key ??????????????? invoker
     *
     * @param apiServiceDefinition agent ??????
     * @param method               ??????????????????
     * @param protocolType         ????????????
     * @param in                   ??????
     * @param inType               in type
     * @param out                  ??????
     * @param outType              out type
     * @param timeout              ??????????????????
     * @param max                  ??????????????????
     * @param apiName              api name
     * @param version              ?????????
     * @param signCheck            ????????????????????????
     * @param nonceCheck           nonce check
     * @see AgentService#send
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    private void registerInvoker(@NotNull ApiServiceDefinition apiServiceDefinition,
                                 @NotNull Method method,
                                 ProtocolType protocolType,
                                 Class<?> in,
                                 Type inType,
                                 Class<?> out,
                                 Type outType,
                                 long timeout,
                                 int max,
                                 String apiName,
                                 String version,
                                 boolean signCheck,
                                 boolean nonceCheck) {

        Method hasApiExtendParam = MethodUtils.getMatchingMethod(apiServiceDefinition.getClass(),
                                                                 method.getName(),
                                                                 in, ApiExtend.class);

        Method noApiExtendParam = MethodUtils.getMatchingMethod(apiServiceDefinition.getClass(),
                                                                method.getName(),
                                                                in);

        boolean enableFailFast = this.agentProperties.getEndpoint().isEnableFailFast();

        // ?????? agent ?????? in/out ??????, ?????????????????????????????? (?????? AbstractAgentService ?????????????????????)
        if (enableFailFast
            && hasApiExtendParam == null
            && noApiExtendParam == null
            && !method.getDeclaringClass().isAssignableFrom(AbstractAgentService.class)) {
            throw new AgentServiceException("\n\n[{}] ??????????????????, ???????????????????????????????????????: \n\n"
                                            + "public ???????????? methodName(???????????? params[, ApiExtend extend])\n",
                                            method);
        }

        ApiServiceInvoker apiServiceInvoker = this.buildInvoker(apiServiceDefinition,
                                                                method,
                                                                protocolType,
                                                                in,
                                                                inType,
                                                                out,
                                                                outType,
                                                                timeout,
                                                                max,
                                                                apiName,
                                                                version,
                                                                signCheck,
                                                                nonceCheck,
                                                                enableFailFast,
                                                                hasApiExtendParam != null);
        String finalName = apiServiceInvoker.finalName();
        log.trace("Registered Agent ApiName:[{}] version: [{}] finalName: [{}] timeout: [{}]", apiName, version, finalName, timeout);
        if (API_SERVICES.containsKey(finalName)) {
            throw new AgentServiceException("?????????????????? apiName: [{}] version [{}] class: [{}]",
                                            apiName, version, apiServiceDefinition.getClass());
        }
        API_SERVICES.put(finalName, apiServiceInvoker);
    }

    /**
     * ?????? ApiServiceInvoker ?????????????????????
     *
     * @param apiServiceDefinition api service definition
     * @param method               method
     * @param protocolType         protocol type
     * @param in                   in
     * @param inType               in type
     * @param out                  out
     * @param outType              out type
     * @param timeout              timeout
     * @param max                  max
     * @param apiName              api name
     * @param version              version
     * @param signCheck            ????????????????????????
     * @param nonceCheck           nonce check
     * @param enableFailFast       ???????????????????????????
     * @param hasApiExtend         ????????? ApiExtend ??????
     * @return the api service invoker
     * @see ApiServiceInvoker#invoke
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    private @NotNull ApiServiceInvoker buildInvoker(ApiServiceDefinition apiServiceDefinition,
                                                    @NotNull Method method,
                                                    ProtocolType protocolType,
                                                    Class<?> in,
                                                    Type inType,
                                                    Class<?> out,
                                                    Type outType,
                                                    long timeout,
                                                    int max,
                                                    String apiName,
                                                    String version,
                                                    boolean signCheck,
                                                    boolean nonceCheck,
                                                    boolean enableFailFast,
                                                    boolean hasApiExtend) {

        ApiServiceInvoker apiServiceInvoker = ApiServiceInvoker.builder()
            .apiServiceDefinition(apiServiceDefinition)
            .method(method)
            .in(in)
            .inType(inType)
            .out(out)
            .outType(outType)
            .timeout(timeout)
            .max(max)
            .serviceName(apiName)
            .version(version)
            .signCheck(signCheck)
            .nonceCheck(nonceCheck)
            .enableFailFast(enableFailFast)
            .hasApiExtend(hasApiExtend)
            .build();

        Class<?> originalClass = this.getOriginalClass(apiServiceDefinition);
        this.checkParam(method, in, inType, out, outType);

        apiServiceInvoker.setServiceClass(originalClass);

        if (protocolType != ProtocolType.JSON) {
            throw new AgentServiceException("???????????????????????????/??????????????????");
        }

        apiServiceInvoker.setProtocolType(protocolType);

        // todo-dong4j : (2019???12???31??? 11:18) [????????????????????????]
        apiServiceInvoker.setApiServiceCodec(this.findPlugin(ApiServiceCodec.class));
        apiServiceInvoker.setApiServiceValidate(this.findPlugin(ApiServiceValidate.class));
        apiServiceInvoker.setApiServiceSignCheck(this.findPlugin(ApiServiceSignCheck.class));
        apiServiceInvoker.setApiServiceReplayCheck(this.findPlugin(ApiServiceReplayCheck.class));
        apiServiceInvoker.setApiServiceExpandIdsCheck(this.findPlugin(ApiServiceExpandIdsCheck.class));

        return apiServiceInvoker;
    }

    /**
     * Shutdown
     *
     * @since 1.0.0
     */
    public void shutdown() {
        this.setShutdown(true);
        // ????????????????????????
        while (this.working.get() != 0) {
            log.info("there also " + this.working.get() + " threads running. program will exit after there running over.");
            try {
                ThreadUtils.sleep(500);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        this.registryClose();

        if (this.executorService != null) {
            this.executorService.shutdown();
        }
    }

    /**
     * Registry close
     *
     * @since 1.0.0
     */
    private void registryClose() {
        if (ModeType.LOCAL.equals(this.mode)) {
            return;
        }
        if (this.registry != null) {
            log.debug("??????????????????");
        } else {
            log.warn("registry is null");
        }

    }

    /**
     * Find api service invoker api service invoker
     *
     * @param service service
     * @param version version
     * @return the api service invoker
     * @since 1.0.0
     */
    public ApiServiceInvoker findApiServiceInvoker(String service, String version) {
        String serviceName = ApiServiceInvoker.name(service, version);
        ApiServiceInvoker apiServiceInvoker = API_SERVICES.get(serviceName);
        BaseCodes.AGENT_SERVICE_NOT_FOUND_ERROR.notNull(API_SERVICES.get(serviceName),
                                                        () -> log.error("[{}] ?????????, "
                                                                        + "?????????????????? @ApiService ????????? AbstractApiService. "
                                                                        + "(????????????????????????, ????????? @ApiServiceMethod ?????????????????????????????????????????????, "
                                                                        + "???????????? ApiServiceDefinition ??????)", serviceName));
        return apiServiceInvoker;
    }

    /**
     * Find code service invoker
     *
     * @param code    code
     * @param version version
     * @return the api service invoker
     * @since 1.8.0
     */
    public ApiServiceInvoker findCodeServiceInvoker(String code, String version) {
        String serviceName = ApiServiceInvoker.name(code, version);
        // code ?????? ????????????????????????????????????????????????????????????????????????????????????????????????????????????
        return CODE_SERVICES.get(serviceName);
    }

    /**
     * Find plugin t
     *
     * @param <T> parameter
     * @param cls cls
     * @return the t
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public <T extends Plugin> @NotNull T findPlugin(Class<? extends Plugin> cls) {
        if (Plugin.class.isAssignableFrom(cls)) {
            Plugin plugin = PLUGINS.get(cls);
            if (plugin == null) {
                plugin = this.applicationContext.getBean(cls);
                PLUGINS.put(cls, plugin);
            }
            return (T) plugin;
        } else {
            throw new AgentServiceException("??????????????????: [{}]", cls.getName());
        }
    }

    /**
     * Sets application context *
     *
     * @param applicationContext application context
     * @since 1.0.0
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * ????????? ApiService ???????????? @ApiService ??????
     *
     * @param serviceClass service class
     * @return the com . sparkwl . agent . annotation . api service
     * @since 1.0.0
     */
    private @NotNull info.spark.agent.annotation.ApiService checkClass(@NotNull Class<?> serviceClass) {
        info.spark.agent.annotation.ApiService apiService = serviceClass.getAnnotation(info.spark.agent.annotation.ApiService.class);
        if (apiService == null) {
            throw new AgentServiceException("[{}] ????????? @ApiService ??????", serviceClass);
        }

        return apiService;
    }

    /**
     * ?????? ???/??? ????????????????????????
     *
     * @param method  method
     * @param in      in
     * @param inType  in type
     * @param out     out
     * @param outType out type
     * @since 1.7.1
     */
    private void checkParam(Method method, Class<?> in, Type inType, Class<?> out, Type outType) {
        String paramName;
        if (inType instanceof ParameterizedType) {
            paramName = ((ParameterizedType) inType).getRawType().getTypeName();
        } else {
            paramName = in.getName();
        }

        if (NOT_ALLOWED_PARAMETER.contains(paramName)) {
            log.warn("??????????????? [{}] ??????????????????, ????????????????????????, ??????????????????: [{}]", paramName, method.toString());
        }
    }

    /**
     * ??????????????? class
     *
     * @param apiServiceDefinition api service definition
     * @return the original class
     * @since 1.0.0
     */
    @NotNull
    private Class<?> getOriginalClass(ApiServiceDefinition apiServiceDefinition) {
        return AopUtils.isAopProxy(apiServiceDefinition)
               ? AopUtils.getTargetClass(apiServiceDefinition)
               : apiServiceDefinition.getClass();
    }

    /**
     * ?????? clazz ???????????????????????????????????? class ??????
     *
     * @param clazz clazz
     * @return the class
     * @since 1.0.0
     */
    @Contract(pure = true)
    private static Class<?> findGenericType(@NotNull Class<?> clazz) {
        if (AbstractApiService.class.isAssignableFrom(clazz)) {
            return ClassUtils.getSuperClassT(clazz, 0);
        } else {
            return ClassUtils.getInterfaceT(clazz, ApiService.class, 0);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param clazz  clazz
     * @param method method
     * @return the type
     * @since 1.5.0
     */
    private static Type getType(@NotNull Class<?> clazz, Method method) {
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments()[0];
        }
        return method.getParameterTypes()[0];
    }

    /**
     * ?????? @AiServiceMethod ???????????????????????? public
     * ??????????????? public????????? true
     * ?????????????????? public????????? false
     *
     * @param originalClass original class
     * @param methodList    method list
     * @return the boolean
     * @since 1.7.0
     */
    private boolean checkPublicMethod(Class<?> originalClass, @NotNull List<Method> methodList) {
        boolean result = true;
        for (Method method : methodList) {
            if (!Modifier.isPublic(method.getModifiers())) {
                // ???????????? public ?????? false
                result = false;
                log.error("[{}.{}] ?????? @ApiServiceMethod ???????????????????????????????????????????????? public", originalClass, method);
                break;
            }
        }
        return result;
    }

    /**
     * ??????????????? AbstractAgentService ?????????????????? methodName, ??????????????????????????????????????????
     *
     * @param originalClass original class
     * @param method        method
     * @return the boolean
     * @see AbstractAgentService#create(BaseDTO)
     * @since 2.1.0
     */
    private boolean isAbstractAgentService(Class<?> originalClass, Method method) {
        return AbstractAgentService.class.isAssignableFrom(originalClass)
               && ("create".equals(method.getName())
                   || "createIgnore".equals(method.getName())
                   || "createReplace".equals(method.getName())
                   || "createOrUpdate".equals(method.getName())
                   || "update".equals(method.getName())
               );
    }

}
