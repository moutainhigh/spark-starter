package info.spark.starter.mongo.convert;

import info.spark.starter.common.enums.SerializeEnum;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

import java.io.Serializable;

/**
 * <p>Description:  </p>
 *
 * @param <E> parameter
 * @param <V> parameter
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.04 19:00
 * @since 1.0.0
 */
public abstract class AbstractEnumToGenericConverter<E extends Enum<?>, V extends Serializable> implements Converter<E, V> {

    /**
     * 如果实现了 {@link SerializeEnum} 接口, 则存储 value, 没有则存储 name()
     *
     * @param source source
     * @return the t
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public V convert(@NotNull E source) {
        if (SerializeEnum.class.isAssignableFrom(source.getClass())) {
            //noinspection rawtypes
            return (V) ((SerializeEnum) source).getValue();
        } else {
            return (V) source.name();
        }
    }

}
