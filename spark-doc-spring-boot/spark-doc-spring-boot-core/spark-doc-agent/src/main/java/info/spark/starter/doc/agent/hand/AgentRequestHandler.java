package info.spark.starter.doc.agent.hand;

import info.spark.starter.basic.util.StringPool;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;

import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.31 16:06
 * @since 1.7.0
 */
public class AgentRequestHandler extends WebMvcRequestHandler {

    /**
     * Agent request handler
     *
     * @param methodResolver     method resolver
     * @param requestMappingInfo requestMappingInfo
     * @param bean               bean
     * @param method             method
     * @since 1.0.0
     */
    public AgentRequestHandler(@NotNull HandlerMethodResolver methodResolver,
                               RequestMappingInfo requestMappingInfo,
                               @NotNull Object bean,
                               @NotNull Method method) {
        super(StringPool.SLASH, methodResolver, requestMappingInfo, new HandlerMethod(bean, method));

    }

    /**
     * Agent request handler
     *
     * @param methodResolver method resolver
     * @param requestMapping request mapping
     * @param handlerMethod  handler method
     * @since 1.0.0
     */
    public AgentRequestHandler(HandlerMethodResolver methodResolver, RequestMappingInfo requestMapping, HandlerMethod handlerMethod) {
        super(StringPool.SLASH, methodResolver, requestMapping, handlerMethod);
    }

}
