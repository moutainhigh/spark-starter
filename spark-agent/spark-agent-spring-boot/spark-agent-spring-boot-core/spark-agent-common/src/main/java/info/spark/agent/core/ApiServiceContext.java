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
 * <p>Description: 全局单例上线文, 用于服务的注册(本地动态注入到 IoC) </p>
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
    /** 保存 X-Agent-Api 与 invoker 的映射关系 */
    private static final ConcurrentMap<String, ApiServiceInvoker> API_SERVICES = Maps.newConcurrentMap();
    /** 保存 X-Agent-Api code名称 与 invoker 的映射关系 */
    private static final ConcurrentMap<String, ApiServiceInvoker> CODE_SERVICES = Maps.newConcurrentMap();
    /** 保存序列化, 参数验证, 扩展插件 */
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
    /** todo-dong4j : (2020年01月31日 16:27) [最大重试次数] */
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
     * 枚举实现单例
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
     * 同步初始化
     *
     * @param ready ready
     * @since 1.0.0
     */
    public void ready(Ready ready) {
        REENTRANT_LOCK.lock();
        try {
            // 自定义服务
            if (ready != null) {
                ready.ready();
            }
            // 开启注册中心
            this.registryReady();
            // 注册所有服务
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
            throw new AgentServiceException("暂不支持远程调用");
        }
    }

    /**
     * 从 IoC 中获取所有的 {@link ApiServiceDefinition} 实例,
     * 这些实例已通过 {@link EnableAgentService} 注解和 {@link info.spark.agent.annotation.ApiService} 注入到了 IoC 中,
     * 逻辑入口为 {@link AgentServiceRegistrar}, 这里使用 bean 生成 invoker.
     *
     * @since 1.0.0
     */
    private void registerAllApiService() {
        // 获取容器中指定某类型、或实现某接口、或继承某父类所有的 Bean
        Map<String, ApiServiceDefinition> maps = this.applicationContext.getBeansOfType(ApiServiceDefinition.class);
        log.debug("已注入 IoC 的 ApiService bean: \n[{}]", maps.size());
        if (maps.isEmpty()) {
            log.warn("未找到 ApiServiceDefinition 实现类");
            return;
        }

        Collection<ApiServiceDefinition> apiServiceDefinitions = maps.values();

        StopWatch sw = new StopWatch();
        sw.start("registerInvokers-task");
        apiServiceDefinitions.forEach(this::registerInvokers);
        sw.stop();
        log.debug("Finish register all service spend time: [{}]", sw.prettyPrint());

        if (ModeType.REMOTE.equals(this.mode)) {
            throw new AgentServiceException("暂不支持远程调用");
        }

        Set<String> valueSet = new HashSet<>(API_SERVICES.keySet());
        Set<String> codeSet = new HashSet<>(CODE_SERVICES.keySet());
        // todo-dong4j : (2020.09.8 18:35) [发送注册消息事件, 发送所有的 agent api 信息]
        Map<String, Set<String>> allApi = Maps.newHashMapWithExpectedSize(2);
        allApi.put(ComponentThreadLocal.AGENT_API, valueSet);
        allApi.put(ComponentThreadLocal.AGENT_CODE, codeSet);
        AgentRegisteredEvent event = new AgentRegisteredEvent(allApi);
        this.applicationContext.publishEvent(event);
        log.trace("agent service 注册完成, 发送 [{}] 事件", AgentRegisteredEvent.class);
        ComponentThreadLocal.context().set(new HashMap<String, Object>(2) {
            private static final long serialVersionUID = 860424951913475865L;

            {
                this.put(ComponentThreadLocal.AGENT_SERVICES, allApi);
            }
        });
        // todo-dong4j : (2021.01.31 18:56) [元数据写入 zookeeper, 便于测试 (需要添加 zk 依赖)]
    }

    /**
     * 循环注册 ApiServiceDefinition 接口实现类
     * 1. 解析被 @ApiService 标记的 class
     * 2. 解析被 @ApiServiceMethod 标识的方法
     *
     * @param apiServiceDefinition api service definition
     * @see AbstractApiService#handler
     * @since 1.0.0
     */
    private void registerInvokers(ApiServiceDefinition apiServiceDefinition) {
        // 执行 invoker 时需要强转, 代理对象强转会导致异常, 这里需要获取到原始的 class
        Class<?> originalClass = this.getOriginalClass(apiServiceDefinition);
        info.spark.agent.annotation.ApiService apiService = this.checkClass(originalClass);

        String apiName = apiService.apiName();
        if (StringUtils.isBlank(apiName)) {
            // 如果没有指定 apiName, 则使用全类名
            apiName = originalClass.getName();
        }

        // 提前获取 @ApiServiceMethod 标识的方法, 用于检查写法
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(originalClass, ApiServiceMethod.class, true, true);
        // 排除桥接接口（主要用于处理实现了 ICrudDelegate 的情况下会找到 2 个重复的 method））
        methods = methods.stream().filter(m -> !m.isBridge()).collect(Collectors.toList());

        // 如果 class 是 ApiService 接口的实现类, 只需要实现 info.spark.agent.core.AbstractApiService.service 即可
        if (ApiService.class.isAssignableFrom(originalClass)) {
            if (CollectionUtils.isNotEmpty(methods)) {
                throw new AgentServiceException("[{}] 写法错误: 实现 ApiService 接口不需要使用 @ApiServiceMethod 注解, 实现指定接口即可",
                                                originalClass);
            }

            this.registryHandler(apiServiceDefinition, originalClass, apiService, apiName);
        } else {
            // bean 被 @ApiService 标识且实现了 ApiServiceDefinition 接口, 同时在方法上使用了 @ApiServiceMethod
            if (CollectionUtils.isEmpty(methods) && this.agentProperties.getEndpoint().isEnableFailFast()) {
                throw new AgentServiceException("[{}] 写法错误: 实现 ApiServiceDefinition 接口, "
                                                + "需要使用 @ApiServiceMethod 注解, 标识业务入口方法", originalClass);
            }
            if (!this.checkPublicMethod(originalClass, methods)) {
                // 如果方法存在不是 public 修饰，直接报错
                throw new StarterException("[{}] 写法错误: @ApiServiceMethod 注解的方法必须是 public 修饰", originalClass.getName());
            }
            this.registryMethod(apiServiceDefinition, originalClass, apiService, apiName, methods);
        }
    }

    /**
     * 如果实现的是 ApiService, 则注册 handler()/service()
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
        // AbstractApiService 子类处理
        if (AbstractApiService.class.isAssignableFrom(originalClass)) {
            methodName = AgentConstant.TMP_METHOD_SERVICE;
            log.debug("[{}] 继承 AbstractApiService , 业务入口方法为: [{}]", originalClass, methodName);
        } else {
            // ApiService 实现类处理
            methodName = AgentConstant.TMP_METHOD_HANDLER;
            log.warn("推荐继承 AbstractApiService 实现业务逻辑. [{}] 实现 ApiService , 业务入口方法为: [{}]", originalClass, methodName);
        }

        // 搜索实现类的 handler()/service() 方法, agent 将请求路由到此方法执行业务逻辑.
        method = MethodUtils.getMatchingMethod(originalClass,
                                               methodName,
                                               // 入参泛型类型
                                               findGenericType(originalClass),
                                               ApiExtend.class);

        long timeout = apiService.timeout() == 0L ? this.agentProperties.getEndpoint().getRequestTimeout().toMillis() :
                       apiService.timeout();

        this.registerInvoker(apiServiceDefinition,
                             method,
                             apiService.protocol(),
                             method.getParameterTypes()[0],
                             getType(apiServiceDefinition.getClass(), method),
                             // 出参泛型类型
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
     * 如果实现的是 ApiServiceDefinition, 则注册被 @ApiServiceMethod 标识的方法
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
            // apiName.value_version默认逻辑
            if (!API_SERVICES.containsKey(finalName)) {
                this.registerDefaultCache(apiServiceDefinition, originalClass, apiService, method, methodApiServiceName);
            }
            // code.code_version 逻辑，为了兼容上方的逻辑，相同于一个二级缓存，API_SERVICE里面有则CODE_SERVICE里面可能就有
            // 1. 如果class和method上都没有code则忽略，不放入code map中
            if (!this.ignoreCode(apiService, apiServiceMethod)
                // 2. 如果1条件成立，在启动时候进行断言class、method 注解必须同时存在code，不通过给出异常提示
                && this.assertSurvives(apiService, apiServiceMethod, originalClass, method)) {
                ApiServiceInvoker apiServiceInvoker = API_SERVICES.get(finalName);
                // 3. 将invoker引用放入对应的code map 中
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
            throw new AgentServiceException("已存在相同的 apiName: [{}] version [{}] class: [{}]",
                                            apiService.code() + StringPool.DOT + apiServiceMethod.code(),
                                            apiServiceMethod.version(),
                                            apiServiceDefinition.getClass());
        }
        // 重设 serviceName
        apiServiceInvoker.setServiceName(apiName);
        // 为了兼容之前的 API_SERVICE 映射并效率为主，使用同一引用即可
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
        // 如果当前方法是抽象的[非接口、非基本数据类型]
        if (Modifier.isAbstract(parameterType.getModifiers())
            && !Modifier.isInterface(parameterType.getModifiers())
            && !parameterType.isPrimitive()) {
            // 如果入参是抽象类
            // todo-dong4j : (2021-10-14 20:0) [重构, 获取泛型类型需要与 AbstractAgentService 定义的顺序一致, 0:Service, 1: Query, 2: DTO]
            if (isAbstractAgentService(originalClass, method)) {
                genericParameterType = ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[2];
                parameterType = (Class<?>) ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[2];
            } else if (AbstractAgentService.class.isAssignableFrom(originalClass)) {
                // 如果是 AbstractAgentService 且不是特定的方法， 则取第二个
                genericParameterType = ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[1];
                parameterType = (Class<?>) ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[1];
            } else {
                genericParameterType = ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[0];
                parameterType = (Class<?>) ((ParameterizedType) originalClass.getGenericSuperclass()).getActualTypeArguments()[0];
            }
        }

        Class<?> outClass = method.getReturnType();
        Type outType = method.getGenericReturnType();

        // ApiService sign 为 true 时, 全部接口都要检查签名, 否则使用 ApiServiceMethod sign 配置
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
                             // 方法级 api 使用 方法名后缀
                             methodApiServiceName,
                             // 方法级 api 使用方法上的 version, 类级别的 version 不再有效
                             apiServiceMethod.version(),
                             signCheck,
                             nonceCheck);
    }

    /**
     * 如果class和method上都没有code 返回 true
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
     * 在双方code都不为空的前提下，class、method 注解必须同时存在code，不通过给出异常提示
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
            throw new AgentServiceException("\n 请确保 [{}]#[{}] ApiService.code 、ApiServiceMethod.code 同时存在或同时不存在 \n",
                                            originalClass.getName(), method.getName());
        }
        return true;
    }

    /**
     * 通过 apiservivce bean 生成 invoker, 使用 map 缓存映射关系,
     * 在处理请求时将通过 header 中的 {@link AgentConstant#X_AGENT_API} 和 {@link AgentConstant#X_AGENT_VERSION} 作为 key 获取指定的 invoker
     *
     * @param apiServiceDefinition agent 接口
     * @param method               业务逻辑方法
     * @param protocolType         协议类型
     * @param in                   入参
     * @param inType               in type
     * @param out                  出参
     * @param outType              out type
     * @param timeout              请求超时时间
     * @param max                  最大重试次数
     * @param apiName              api name
     * @param version              版本号
     * @param signCheck            是否需要签名检查
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

        // 所有 agent 都是 in/out 机制, 验证写法是否符合要求 (排除 AbstractAgentService 的几个公共方法)
        if (enableFailFast
            && hasApiExtendParam == null
            && noApiExtendParam == null
            && !method.getDeclaringClass().isAssignableFrom(AbstractAgentService.class)) {
            throw new AgentServiceException("\n\n[{}] 方法签名错误, 请确保业务入口方法签名正确: \n\n"
                                            + "public 出参实体 methodName(入参实体 params[, ApiExtend extend])\n",
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
            throw new AgentServiceException("已存在相同的 apiName: [{}] version [{}] class: [{}]",
                                            apiName, version, apiServiceDefinition.getClass());
        }
        API_SERVICES.put(finalName, apiServiceInvoker);
    }

    /**
     * 构建 ApiServiceInvoker 对象并设置插件
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
     * @param signCheck            是否需要签名检查
     * @param nonceCheck           nonce check
     * @param enableFailFast       是否为快速失败检查
     * @param hasApiExtend         是否有 ApiExtend 参数
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
            throw new AgentServiceException("暂不支持其他序列化/反序列化协议");
        }

        apiServiceInvoker.setProtocolType(protocolType);

        // todo-dong4j : (2019年12月31日 11:18) [后续支持其他协议]
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
        // 等待处理事务完成
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
            log.debug("注销远程服务");
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
                                                        () -> log.error("[{}] 不存在, "
                                                                        + "请在类上使用 @ApiService 且继承 AbstractApiService. "
                                                                        + "(如果是自定义方法, 请使用 @ApiServiceMethod 标识业务入口方法且方法签名正确, "
                                                                        + "需要实现 ApiServiceDefinition 接口)", serviceName));
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
        // code 方式 不做异常抛出，没有就继续从下面的开始获取，直到最后一层没有获取到才会异常
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
            throw new AgentServiceException("插件加载失败: [{}]", cls.getName());
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
     * 定义的 ApiService 必须使用 @ApiService 标识
     *
     * @param serviceClass service class
     * @return the com . sparkwl . agent . annotation . api service
     * @since 1.0.0
     */
    private @NotNull info.spark.agent.annotation.ApiService checkClass(@NotNull Class<?> serviceClass) {
        info.spark.agent.annotation.ApiService apiService = serviceClass.getAnnotation(info.spark.agent.annotation.ApiService.class);
        if (apiService == null) {
            throw new AgentServiceException("[{}] 未使用 @ApiService 标识", serviceClass);
        }

        return apiService;
    }

    /**
     * 检查 出/入 参数是否符合规范
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
            log.warn("请不要使用 [{}] 作为入参对象, 推荐使用实体入参, 提升可维护性: [{}]", paramName, method.toString());
        }
    }

    /**
     * 获取原始的 class
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
     * 查找 clazz 父类或接口入参的泛型类型 class 对象
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
     * 获取入参的泛型类型
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
     * 验证 @AiServiceMethod 修饰的方法是否是 public
     * 如果全部是 public，返回 true
     * 如果存在不是 public，返回 false
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
                // 如果不是 public 返回 false
                result = false;
                log.error("[{}.{}] 是被 @ApiServiceMethod 修饰的方法，方法访问修饰符必须是 public", originalClass, method);
                break;
            }
        }
        return result;
    }

    /**
     * 判断是否为 AbstractAgentService 且是否为指定 methodName, 因为需要获取特定的范型类型。
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
