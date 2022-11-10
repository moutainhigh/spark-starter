package info.spark.starter.websocket.support;

import info.spark.starter.websocket.annotation.OnBinary;

import org.springframework.core.MethodParameter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:01
 * @since 2022.1.1
 */
public class ByteMethodArgumentResolver implements MethodArgumentResolver {
    /**
     * Supports parameter
     *
     * @param parameter parameter
     * @return the boolean
     * @since 2022.1.1
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(OnBinary.class) && byte[].class.isAssignableFrom(parameter.getParameterType());
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
        BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) object;
        ByteBuf content = binaryWebSocketFrame.content();
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        return bytes;
    }
}
