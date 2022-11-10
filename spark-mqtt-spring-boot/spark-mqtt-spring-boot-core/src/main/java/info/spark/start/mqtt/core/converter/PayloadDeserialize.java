package info.spark.start.mqtt.core.converter;

import org.springframework.core.convert.converter.ConverterFactory;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.17 11:50
 * @since 2.1.0
 */
public interface PayloadDeserialize extends ConverterFactory<byte[], Object> {
}
