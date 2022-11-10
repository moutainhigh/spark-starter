package info.spark.starter.mongo.autoconfigure.sync;

import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.04 19:10
 * @since 1.0.0
 */
@FunctionalInterface
public interface ConverterCustomizer {

    /**
     * 添加自定义转换器
     *
     * @param list list
     * @since 1.0.0
     */
    void customize(List<Converter<?, ?>> list);
}
