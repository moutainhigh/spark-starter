package info.spark.starter.mongo.convert;

import info.spark.starter.common.enums.EnableEnum;

import org.springframework.data.convert.WritingConverter;

/**
 * <p>Description: 枚举转成 boolean 存储 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.04 18:29
 * @since 1.0.0
 */
@WritingConverter
public class EnumToBooleanConverter extends AbstractEnumToSerializableConverter<EnableEnum> {

}
