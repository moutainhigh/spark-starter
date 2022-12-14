package info.spark.starter.cache.support;

import com.alicp.jetcache.anno.SerialPolicy;

import org.jetbrains.annotations.Contract;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.function.Function;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.11 21:37
 * @since 1.0.0
 */
public class JacksonValueSerialPolicy implements SerialPolicy, RedisSerializer<Object> {
    /** JACKSON */
    public static final String JACKSON = "JACKSON";
    /** Jackson value decoder */
    private final JacksonValueDecoder jacksonValueDecoder;
    /** Jackson value encoder */
    private final JacksonValueEncoder jacksonValueEncoder;

    /**
     * Jackson value serial policy
     *
     * @param jacksonValueDecoder jackson value decoder
     * @param jacksonValueEncoder jackson value encoder
     * @since 1.0.0
     */
    @Contract(pure = true)
    public JacksonValueSerialPolicy(JacksonValueDecoder jacksonValueDecoder,
                                    JacksonValueEncoder jacksonValueEncoder) {
        this.jacksonValueDecoder = jacksonValueDecoder;
        this.jacksonValueEncoder = jacksonValueEncoder;

    }

    /**
     * Encoder function
     *
     * @return the function
     * @since 1.0.0
     */
    @Override
    public Function<Object, byte[]> encoder() {
        return this.jacksonValueEncoder;
    }

    /**
     * Decoder function
     *
     * @return the function
     * @since 1.0.0
     */
    @Override
    public Function<byte[], Object> decoder() {
        return this.jacksonValueDecoder;
    }

    /**
     * Serialize byte [ ]
     *
     * @param o o
     * @return the byte [ ]
     * @throws SerializationException serialization exception
     * @since 1.0.0
     */
    @Override
    public byte[] serialize(Object o) throws SerializationException {
        return this.jacksonValueEncoder.apply(o);
    }

    /**
     * Deserialize object
     *
     * @param bytes bytes
     * @return the object
     * @throws SerializationException serialization exception
     * @since 1.0.0
     */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return this.jacksonValueDecoder.doApply(bytes);
    }
}
