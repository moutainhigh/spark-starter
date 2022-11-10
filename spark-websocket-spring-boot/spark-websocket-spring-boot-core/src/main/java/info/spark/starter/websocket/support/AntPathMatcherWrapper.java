package info.spark.starter.websocket.support;

import info.spark.starter.websocket.pojo.PojoEndpointServer;

import org.springframework.util.AntPathMatcher;

import java.util.LinkedHashMap;
import java.util.Map;

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
public class AntPathMatcherWrapper extends AntPathMatcher implements WsPathMatcher {

    /** Pattern */
    private final String pattern;

    /**
     * Ant path matcher wrapper
     *
     * @param pattern pattern
     * @since 2022.1.1
     */
    public AntPathMatcherWrapper(String pattern) {
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
        Map<String, String> variables = new LinkedHashMap<>();
        boolean result = doMatch(pattern, decoder.path(), true, variables);
        if (result) {
            channel.attr(PojoEndpointServer.URI_TEMPLATE).set(variables);
            return true;
        }
        return false;
    }
}
