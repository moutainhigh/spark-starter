package info.spark.starter.websocket.support;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;


/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:01
 * @since 2022.1.1
 */
public interface WsPathMatcher {

    /**
     * Gets pattern *
     *
     * @return the pattern
     * @since 2022.1.1
     */
    String getPattern();

    /**
     * Match and extract
     *
     * @param decoder decoder
     * @param channel channel
     * @return the boolean
     * @since 2022.1.1
     */
    boolean matchAndExtract(QueryStringDecoder decoder, Channel channel);
}
