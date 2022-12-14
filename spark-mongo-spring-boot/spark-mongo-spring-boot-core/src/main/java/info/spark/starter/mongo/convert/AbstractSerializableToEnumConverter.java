package info.spark.starter.mongo.convert;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * <p>Description: 将 mongodb 的数据转换为枚举 </p>
 * source -> target
 *
 * @param <T> parameter target
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.04 18:38
 * @since 1.0.0
 */
public abstract class AbstractSerializableToEnumConverter<T extends Enum<?>> extends AbstractGenericToEnumConverter<T, Serializable> {


    /**
     * 建立映射关系 (V[value] <--> E[SerializeEnum])
     *
     * @param enumClass enum class
     * @since 1.0.0
     */
    public AbstractSerializableToEnumConverter(@NotNull Class<T> enumClass) {
        super(enumClass);
    }
}
