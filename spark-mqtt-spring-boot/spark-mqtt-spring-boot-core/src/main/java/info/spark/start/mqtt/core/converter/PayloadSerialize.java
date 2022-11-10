package info.spark.start.mqtt.core.converter;

import org.springframework.core.convert.converter.Converter;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.17 11:49
 * @since 2.1.0
 */
public interface PayloadSerialize extends Converter<Object, byte[]> {
}
