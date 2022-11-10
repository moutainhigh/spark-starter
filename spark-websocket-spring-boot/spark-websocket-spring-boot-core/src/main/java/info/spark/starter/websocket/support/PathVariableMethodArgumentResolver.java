package info.spark.starter.websocket.support;

import info.spark.starter.websocket.annotation.PathVariable;
import info.spark.starter.websocket.pojo.PojoEndpointServer;

import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.core.MethodParameter;

import java.util.Map;

import io.netty.channel.Channel;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:02
 * @since 2022.1.1
 */
@SuppressWarnings("all")
public class PathVariableMethodArgumentResolver implements MethodArgumentResolver {

    /** Bean factory */
    private final AbstractBeanFactory beanFactory;

    /**
     * Path variable method argument resolver
     *
     * @param beanFactory bean factory
     * @since 2022.1.1
     */
    public PathVariableMethodArgumentResolver(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Supports parameter
     *
     * @param parameter parameter
     * @return the boolean
     * @since 2022.1.1
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PathVariable.class);
    }

    /**
     * Resolve argument
     *
     * @param parameter parameter
     * @param channel   channel
     * @param object    object
     * @return the object
     * @throws Exception exception
     * @since 2022.1.1
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        PathVariable ann = parameter.getParameterAnnotation(PathVariable.class);
        String name = ann.name();
        if (name.isEmpty()) {
            name = parameter.getParameterName();
            if (name == null) {
                throw new IllegalArgumentException(
                    "Name for argument type [" + parameter.getNestedParameterType().getName() +
                    "] not available, and parameter name information not found in class file either.");
            }
        }
        Map<String, String> uriTemplateVars = channel.attr(PojoEndpointServer.URI_TEMPLATE).get();
        Object arg = (uriTemplateVars != null ? uriTemplateVars.get(name) : null);
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        return typeConverter.convertIfNecessary(arg, parameter.getParameterType());
    }
}
