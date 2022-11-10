package info.spark.starter.websocket.support;

import info.spark.starter.websocket.annotation.OnEvent;

import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.core.MethodParameter;

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
public class EventMethodArgumentResolver implements MethodArgumentResolver {

    /** Bean factory */
    private final AbstractBeanFactory beanFactory;

    /**
     * Event method argument resolver
     *
     * @param beanFactory bean factory
     * @since 2022.1.1
     */
    public EventMethodArgumentResolver(AbstractBeanFactory beanFactory) {
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
        return parameter.getMethod().isAnnotationPresent(OnEvent.class);
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
        if (object == null) {
            return null;
        }
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        return typeConverter.convertIfNecessary(object, parameter.getParameterType());
    }
}
