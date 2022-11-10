package info.spark.starter.doc.agent.schema;

import com.google.common.base.Function;

import info.spark.agent.annotation.ApiService;
import info.spark.agent.annotation.ApiServiceMethod;
import info.spark.agent.constant.AgentConstant;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.context.EarlySpringContext;
import info.spark.starter.core.util.DataTypeUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.doc.agent.constant.AgentDocConstant;
import info.spark.starter.doc.agent.hand.AgentRequestHandler;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import static springfox.documentation.spi.service.contexts.Orderings.byPatternsCondition;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.31 14:13
 * @since 1.7.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AgentHandlerPlugin implements RequestHandlerProvider {
    /** Agent service */
    private final Map<Method, Object> methodWithAgent = new ConcurrentHashMap<>();
    /** Method resolver */
    private final HandlerMethodResolver methodResolver;

    /**
     * Api agent handler plugin
     *
     * @param methodResolver method resolver
     * @since 1.7.0
     */
    public AgentHandlerPlugin(HandlerMethodResolver methodResolver) {
        this.scanAgent();
        this.methodResolver = methodResolver;
    }

    /**
     * Scan agent
     *
     * @since 1.7.0
     */
    private void scanAgent() {
        Map<String, Object> beansWithAnnotation = EarlySpringContext.getBeansWithAnnotation(ApiService.class);
        beansWithAnnotation.forEach((k, bean) -> {
            for (Method method : AopUtils.getTargetClass(bean).getDeclaredMethods()) {
                if (method.isAnnotationPresent(ApiServiceMethod.class) && method.isAnnotationPresent(ApiOperation.class)) {
                    this.methodWithAgent.put(method, bean);
                }
            }
        });
    }

    /**
     * Request handlers
     *
     * @return the list
     * @since 1.7.0
     */
    @Override
    public List<RequestHandler> requestHandlers() {
        return this.methodWithAgent.entrySet().stream()
            .map(toRequestHandler()).sorted(byPatternsCondition()).collect(Collectors.toList());
    }

    /**
     * To request handler
     *
     * @return the java . util . function . function
     * @since 1.7.0
     */
    private java.util.function.Function<Map.Entry<Method, Object>, @Nullable RequestHandler> toRequestHandler() {
        return (Function<Map.Entry<Method, Object>, RequestHandler>) entry -> {
            Method method = Objects.requireNonNull(entry).getKey();
            Class<?> targetClass = AopUtils.getTargetClass(Objects.requireNonNull(entry).getValue());

            String api = StringUtils.format("{}.{}",
                                            this.getApiName(targetClass),
                                            this.agentMethodPath(method));

            RequestMappingInfo requestMappingInfo =
                new RequestMappingInfo(new PatternsRequestCondition(String.format(AgentDocConstant.BASE_PATH, "", api)),
                                       new RequestMethodsRequestCondition(this.getHttpMethod(method.getParameters())),
                                       null,
                                       this.getDefaultHeads(api),
                                       // if application/json without ;charset=UTF-8, springfox don't show consume in api-doc
                                       // it's same like that springfox issues #3481
                                       new ConsumesRequestCondition(MediaType.ALL_VALUE),
                                       new ProducesRequestCondition(MediaType.APPLICATION_JSON_VALUE),
                                       null);
            return new AgentRequestHandler(methodResolver, requestMappingInfo, entry.getValue(), method);
        };
    }

    /**
     * Gets api name *
     *
     * @param targetClass target class
     * @return the api name
     * @since 1.8.0
     */
    @NotNull
    private String getApiName(Class<?> targetClass) {
        // 1. 先取 code
        String serviceName = targetClass.getAnnotation(ApiService.class).code();
        // 2. 没有 code 取 apiName
        if (StringUtils.isBlank(serviceName)) {
            serviceName = targetClass.getAnnotation(ApiService.class).apiName();
        }
        return serviceName;
    }

    /**
     * Agent method path
     *
     * @param method method
     * @return the string
     * @since 1.7.0
     */
    @NotNull
    private String agentMethodPath(Method method) {
        // 1. 先取 code
        String methodPath = method.getAnnotation(ApiServiceMethod.class).code();
        // 2. 没有 code 取 value
        if (StringUtils.isBlank(methodPath)) {
            methodPath = method.getAnnotation(ApiServiceMethod.class).value();
        }
        // 3. 兜底取的是 method name
        if (StringUtils.isBlank(methodPath)) {
            methodPath = method.getName();
        }
        return methodPath;
    }

    /**
     * Gets http method *
     *
     * @param params params
     * @return the http method
     * @since 1.7.0
     */
    private RequestMethod getHttpMethod(Parameter[] params) {
        if (params == null
            || 0 == params.length
            || DataTypeUtils.isPrimitive(params[0].getType())
            || DataTypeUtils.isPrimitive(DataTypeUtils.typeUnBoxing(params[0].getType()))
            || String.class.isAssignableFrom(params[0].getType())
            || String[].class.isAssignableFrom(params[0].getType())) {
            return RequestMethod.GET;
        }
        return RequestMethod.POST;
    }

    /**
     * Gets default heads *
     *
     * @param api api
     * @return the default heads
     * @since 1.7.0
     */
    private HeadersRequestCondition getDefaultHeads(String api) {
        return new HeadersRequestCondition(this.buildHeaderExpression(AgentConstant.X_AGENT_API, api),
                                           this.buildHeaderExpression(AgentConstant.X_AGENT_VERSION, AgentConstant.DEFAULT_API_VERSION),
                                           this.buildHeaderExpression(AgentConstant.X_AGENT_CHARSET, Charsets.UTF_8_NAME),
                                           this.buildHeaderExpression(AgentConstant.X_AGENT_APPID, StringPool.EMPTY),
                                           this.buildHeaderExpression(AgentConstant.X_AGENT_TENANTID, StringPool.EMPTY),
                                           this.buildHeaderExpression(AgentConstant.X_AGENT_NONCE, StringPool.EMPTY),
                                           this.buildHeaderExpression(AgentConstant.X_AGENT_SIGNATURE, StringPool.EMPTY),
                                           this.buildHeaderExpression(AgentConstant.X_AGENT_SIGNATURE_HEADERS, StringPool.EMPTY),
                                           this.buildHeaderExpression(AgentConstant.X_AGENT_TIMESTAMP, System.currentTimeMillis()),
                                           this.buildHeaderExpression(AgentDocConstant.X_AGENT_DOC_TEST, StringPool.TRUE));

    }

    /**
     * Build header expression
     *
     * @param key   key
     * @param value value
     * @return the string
     * @since 1.7.0
     */
    private String buildHeaderExpression(String key, Object value) {
        return key + StringPool.EQUALS + value;
    }
}

