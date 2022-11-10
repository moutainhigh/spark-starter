package info.spark.starter.websocket.support;

import info.spark.starter.websocket.annotation.RequestParam;
import info.spark.starter.websocket.pojo.PojoEndpointServer;

import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.core.MethodParameter;

import java.util.List;
import java.util.Map;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;


/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:03
 * @since 2022.1.1
 */
@SuppressWarnings("all")
public class RequestParamMethodArgumentResolver implements MethodArgumentResolver {

    /** Bean factory */
    private final AbstractBeanFactory beanFactory;

    /**
     * Request param method argument resolver
     *
     * @param beanFactory bean factory
     * @since 2022.1.1
     */
    public RequestParamMethodArgumentResolver(AbstractBeanFactory beanFactory) {
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
        return parameter.hasParameterAnnotation(RequestParam.class);
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
        RequestParam ann = parameter.getParameterAnnotation(RequestParam.class);
        String name = ann.name();
        if (name.isEmpty()) {
            name = parameter.getParameterName();
            if (name == null) {
                throw new IllegalArgumentException(
                    "Name for argument type [" + parameter.getNestedParameterType().getName() +
                    "] not available, and parameter name information not found in class file either.");
            }
        }

        if (!channel.hasAttr(PojoEndpointServer.REQUEST_PARAM)) {
            QueryStringDecoder decoder = new QueryStringDecoder(((FullHttpRequest) object).uri());
            channel.attr(PojoEndpointServer.REQUEST_PARAM).set(decoder.parameters());
        }

        Map<String, List<String>> requestParams = channel.attr(PojoEndpointServer.REQUEST_PARAM).get();
        List<String> arg = (requestParams != null ? requestParams.get(name) : null);
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        if (arg == null) {
            if ("\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n".equals(ann.defaultValue())) {
                return null;
            } else {
                return typeConverter.convertIfNecessary(ann.defaultValue(), parameter.getParameterType());
            }
        }
        if (List.class.isAssignableFrom(parameter.getParameterType())) {
            return typeConverter.convertIfNecessary(arg, parameter.getParameterType());
        } else {
            return typeConverter.convertIfNecessary(arg.get(0), parameter.getParameterType());
        }
    }
}
