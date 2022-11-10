package info.spark.agent.plugin.impl;

import info.spark.agent.plugin.ApiServiceCodec;
import info.spark.starter.basic.util.JsonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: json 序列化与反序列化 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.31 02:55
 * @since 1.0.0
 */
@Slf4j
public class JacksonCodec implements ApiServiceCodec<Object, Object> {

    /**
     * Encode byte [ ]
     *
     * @param o o
     * @return the byte [ ]
     * @since 1.0.0
     */
    @Override
    public byte[] encode(Object o) {
        return JsonUtils.toJsonAsBytes(o);
    }

    /**
     * Decode object
     *
     * @param in  in
     * @param cls cls
     * @return the object
     * @since 1.0.0
     */
    @Override
    public Object decode(byte[] in, Class<?> cls) {
        return JsonUtils.parse(in, cls);
    }
}
