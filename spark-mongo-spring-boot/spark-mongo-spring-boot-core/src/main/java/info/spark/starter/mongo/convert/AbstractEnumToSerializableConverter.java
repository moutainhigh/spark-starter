package info.spark.starter.mongo.convert;

import java.io.Serializable;

/**
 * <p>Description:  </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.04 19:00
 * @since 1.0.0
 */
public abstract class AbstractEnumToSerializableConverter<T extends Enum<?>> extends AbstractEnumToGenericConverter<T, Serializable> {

}
