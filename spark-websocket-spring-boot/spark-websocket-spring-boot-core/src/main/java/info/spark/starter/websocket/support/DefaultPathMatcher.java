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
public class DefaultPathMatcher implements WsPathMatcher {

    /** Pattern */
    private final String pattern;

    /**
     * Default path matcher
     *
     * @param pattern pattern
     * @since 2022.1.1
     */
    public DefaultPathMatcher(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Gets pattern *
     *
     * @return the pattern
     * @since 2022.1.1
     */
    @Override
    public String getPattern() {
        return this.pattern;
    }

    /**
     * Match and extract
     *
     * @param decoder decoder
     * @param channel channel
     * @return the boolean
     * @since 2022.1.1
     */
    @Override
    public boolean matchAndExtract(QueryStringDecoder decoder, Channel channel) {
        return pattern.equals(decoder.path());
    }
}
