package info.spark.starter.websocket.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

import io.netty.channel.Channel;


/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:01
 * @since 2022.1.1
 */
public interface MethodArgumentResolver {

    /**
     * Whether the given {@linkplain MethodParameter method parameter} is
     * supported by this resolver.
     *
     * @param parameter the method parameter to check
     * @return {@code true} if this resolver supports the supplied parameter; {@code false} otherwise
     * @since 2022.1.1
     */
    boolean supportsParameter(MethodParameter parameter);


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
    @Nullable
    Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception;

}
