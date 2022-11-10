package info.spark.agent.adapter.registrar;

import info.spark.agent.adapter.annotation.ServiceName;
import info.spark.agent.adapter.aop.SdkAnnotationAdvisor;
import info.spark.agent.adapter.client.AgentClient;
import info.spark.agent.adapter.client.AgentRequest;
import info.spark.agent.adapter.client.AgentRequestBuilder;
import info.spark.agent.adapter.client.AgentTemplate;
import info.spark.agent.adapter.config.AgentRestProperties;
import info.spark.agent.adapter.enums.AgentRequestType;
import info.spark.agent.adapter.util.AgentUtils;
import info.spark.agent.constant.AgentConstant;
import info.spark.agent.validation.Validater;

import info.spark.agent.adapter.annotation.Client;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.util.StringUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Method;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 这里使用 spring 的 cglib 实现动态代理 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.22 09:01
 * @since 1.6.0
 */
@Slf4j
public class AgentClientProxy implements InvocationHandler {

    /** Service name */
    private final String serviceName;
    /** Agent template */
    private final AgentTemplate agentTemplate;
    /** Agent rest properties */
    private final AgentRestProperties agentRestProperties;
    /** Validater */
    private final Validater validater;
    /** Environment */
    private final Environment environment;
    /**
     * 通过 {@link Client} 透传的自定义 url
     *
     * @since 1.7.0
     */
    private final String customEndpoint;
    /** 通过 client.setEndpoint 覆写的 endpoint */
    private String overridedEndpoint;
    /** tenantLocal */
    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Service proxy
     *
     * @param customEndpoint      custom endpoint
     * @param serviceName         service name
     * @param agentTemplate       agent template
     * @param agentRestProperties agent rest properties
     * @param environment         environment
     * @param validater           参数校验
     * @since 1.6.0
     */
    @Contract(pure = true)
    AgentClientProxy(String customEndpoint,
                     String serviceName,
                     AgentTemplate agentTemplate,
                     AgentRestProperties agentRestProperties,
                     Environment environment,
                     Validater validater) {
        this.customEndpoint = customEndpoint;
        this.overridedEndpoint = customEndpoint;
        this.serviceName = serviceName;
        this.agentTemplate = agentTemplate;
        this.agentRestProperties = agentRestProperties;
        this.environment = environment;
        this.validater = validater;
    }

    /**
     * 在执行 Client 接口方法时, 返回 {@link AgentRequestBuilder} 实例, 并自动注入 serviceName 和 agentTemplate
     *
     * @param proxy  proxy
     * @param method method
     * @param args   args
     * @return the object
     * @see AgentClient
     * @since 1.6.0
     */
    @SneakyThrows
    @Override
    public Object invoke(Object proxy, @NotNull Method method, Object[] args) {

        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        if (method.getName().equals(ServiceName.SERVICE_NAME)) {
            return this.serviceName;
        }

        if (method.getName().equals(AgentConstant.CLIENT_METHOD_GET_ENDPOINT)) {
            return this.getEndpoint();
        }

        if (method.getName().equals(AgentConstant.CLIENT_METHOD_SET_TENANTID)) {
            // 业务端主动传递 tenantId
            String tenantId = args[0] == null ? "" : String.valueOf(args[0]);
            THREAD_LOCAL.set(tenantId);
            return null;
        }

        if (method.getName().equals(AgentConstant.CLIENT_METHOD_SET_ENDPOINT)) {
            String newEndpoint = AgentUtils.checkEndpointPattern(String.valueOf(args[0]));
            if (StringUtils.isNotBlank(this.overridedEndpoint)) {
                log.warn("覆写 endpoint: [{} -> {}]", this.overridedEndpoint, newEndpoint);
            } else {
                log.warn("通过 API 设置 endpoint: [{}]", newEndpoint);
            }
            this.overridedEndpoint = AgentUtils.addProtocol(newEndpoint);
            return null;
        }

        return this.createAgentRequestBuilder(method, args);
    }

    /**
     * 优先从配置文件中获取 endpoint
     *
     * @return the endpoint
     * @since 1.8.0
     */
    private String getEndpoint() {
        // 获取配置中的 endpoint
        String endpoint = this.environment.getProperty(AgentConstant.PREPERTIES_PREFIX + this.serviceName);
        // 如果未配置 endpoint, 则获取 CLient.endpoint
        endpoint = StringUtils.isBlank(endpoint) ? this.overridedEndpoint : endpoint;
        return AgentUtils.checkEndpointPattern(endpoint);
    }


    /**
     * Gets agent request builder *
     *
     * @param method method
     * @param args   args
     * @return the agent request builder
     * @since 1.7.1
     */
    @Contract("_, _ -> new")
    @NotNull
    private AgentRequestBuilder createAgentRequestBuilder(@NotNull Method method, Object[] args) {
        String tenantId = THREAD_LOCAL.get();
        tenantId = StringUtils.isBlank(tenantId) ? "" : tenantId;
        THREAD_LOCAL.remove();

        Assertions.notNull(args[0], "apiName 或 agentRequest 不能为 null");
        Assertions.notBlank(String.valueOf(args[0]), "apiName 不能为空白字符");

        HttpMethod httpMethod = null;
        String apiName;
        AgentRequest request = null;
        AgentRequestType type;

        if (method.getName().equalsIgnoreCase(HttpMethod.PATCH.name())) {
            httpMethod = HttpMethod.PATCH;
            apiName = String.valueOf(args[0]);
            type = AgentRequestType.HTTP_METHOD;
        } else if (method.getName().equalsIgnoreCase(HttpMethod.DELETE.name())) {
            httpMethod = HttpMethod.DELETE;
            apiName = String.valueOf(args[0]);
            type = AgentRequestType.HTTP_METHOD;
        } else if (method.getName().equalsIgnoreCase(HttpMethod.POST.name())) {
            httpMethod = HttpMethod.POST;
            apiName = String.valueOf(args[0]);
            type = AgentRequestType.HTTP_METHOD;
        } else if (method.getName().equalsIgnoreCase(HttpMethod.PUT.name())) {
            httpMethod = HttpMethod.PUT;
            apiName = String.valueOf(args[0]);
            type = AgentRequestType.HTTP_METHOD;
        } else if (method.getName().equals(AgentConstant.CLIENT_METHOD_API)) {
            apiName = String.valueOf(args[0]);
            type = AgentRequestType.API_METHOD;
        } else if (method.getName().equals(AgentConstant.CLIENT_METHOD_REQUEST)) {
            apiName = ((AgentRequest) args[0]).getApiName();
            request = (AgentRequest) args[0];
            type = AgentRequestType.REQUEST_MENTOD;
        } else {
            httpMethod = HttpMethod.GET;
            apiName = String.valueOf(args[0]);
            type = AgentRequestType.HTTP_METHOD;
        }

        if (StringUtils.isNotBlank(this.customEndpoint) && !this.customEndpoint.equals(this.overridedEndpoint)) {
            log.warn("Client.endpoint 配置的值与当前 endpoint 不一致: [{} -> {}]", this.customEndpoint, this.overridedEndpoint);
        }

        return new AgentRequestBuilder(this.getEndpoint(),
                                       httpMethod,
                                       this.buildApiName(apiName),
                                       this.serviceName,
                                       this.agentTemplate,
                                       this.agentRestProperties,
                                       request,
                                       type,
                                       tenantId,
                                       this.validater);
    }

    /**
     * 获取最终的 apiName。
     * SdkAnnotationAdvisor.CONTEXT.remove() 用于补偿在异常情况下不能执行 {@link SdkAnnotationAdvisor#afterReturning}.
     *
     * @param methodName method name
     * @return the api name prefix
     * @since 2.0.0
     */
    private String buildApiName(String methodName) {
        String apiNamePrefix = SdkAnnotationAdvisor.CONTEXT.get();

        if (StringUtils.isBlank(apiNamePrefix)) {
            return methodName;
        }

        SdkAnnotationAdvisor.CONTEXT.remove();
        return apiNamePrefix + "." + methodName;
    }
}
