package info.spark.starter.mq.serialization;

import info.spark.starter.basic.util.JsonUtils;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.jetbrains.annotations.Nullable;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.15 10:50
 * @since 1.5.0
 */
public class KafkaMessageSerializer implements Serializer<Object> {
    /** Encoding */
    private String encoding = "UTF8";

    /** Json serializer */
    private JsonSerializer jsonSerializer;

    /**
     * Configure
     *
     * @param configs configs
     * @param isKey   is key
     * @since 1.5.0
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? "key.serializer.encoding" : "value.serializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) {
            encodingValue = configs.get("serializer.encoding");
        }
        if (encodingValue instanceof String) {
            this.encoding = (String) encodingValue;
        }
    }

    /**
     * Convert {@code data} into a byte array.
     *
     * @param topic topic associated with data
     * @param data  typed data
     * @return serialized bytes
     * @since 1.5.0
     */
    @Override
    public byte[] serialize(String topic, Object data) {
        if (data instanceof String) {
            return this.serialize((String) data);
        }

        return JsonUtils.toJsonAsBytes(data);
    }

    /**
     * Convert {@code data} into a byte array.
     *
     * @param topic   topic associated with data
     * @param headers headers associated with the record
     * @param data    typed data
     * @return serialized bytes
     * @since 1.5.0
     */
    @Override
    public byte[] serialize(String topic, Headers headers, Object data) {
        return this.serialize(topic, data);
    }

    /**
     * Serialize
     *
     * @param data data
     * @return the byte @ nullable [ ]
     * @since 1.5.0
     */
    private byte @Nullable [] serialize(String data) {
        try {
            if (data == null) {
                return null;
            } else {
                return data.getBytes(this.encoding);
            }
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding " + this.encoding);
        }
    }
}
