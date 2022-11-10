package info.spark.starter.mongo.convert;

import info.spark.starter.common.enums.EnableEnum;

import org.springframework.data.convert.ReadingConverter;

/**
 * <p>Description: 查询时将 boolean 转为枚举(EnableEnum) </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.04 18:29
 * @since 1.0.0
 */
@ReadingConverter
public class BooleanToEnumConverter extends AbstractSerializableToEnumConverter<EnableEnum> {

    /**
     * Boolean to enum converter
     *
     * @since 1.0.0
     */
    public BooleanToEnumConverter() {
        super(EnableEnum.class);
    }
}
