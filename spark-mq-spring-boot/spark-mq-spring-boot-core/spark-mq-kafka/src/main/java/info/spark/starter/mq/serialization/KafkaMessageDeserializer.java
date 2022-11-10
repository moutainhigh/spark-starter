package info.spark.starter.mq.serialization;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

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
public class KafkaMessageDeserializer implements Deserializer<String> {
    /** Encoding */
    private String encoding = "UTF8";

    /**
     * Configure
     *
     * @param configs configs
     * @param isKey   is key
     * @since 1.5.0
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? "key.deserializer.encoding" : "value.deserializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) {
            encodingValue = configs.get("deserializer.encoding");
        }
        if (encodingValue instanceof String) {
            this.encoding = (String) encodingValue;
        }
    }

    /**
     * Deserialize
     *
     * @param topic topic
     * @param data  data
     * @return the string
     * @since 1.5.0
     */
    @Override
    public String deserialize(String topic, byte[] data) {
        try {
            return null == data ? null : new String(data, this.encoding);
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("Error when deserializing byte[] to string due to unsupported encoding " + this.encoding);
        }
    }
}
