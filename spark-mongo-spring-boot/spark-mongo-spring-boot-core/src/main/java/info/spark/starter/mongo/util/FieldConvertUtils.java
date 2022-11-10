package info.spark.starter.mongo.util;

import info.spark.starter.util.StringUtils;
import info.spark.starter.mongo.convert.CustomMongoMappingContext;
import info.spark.starter.mongo.enums.FieldConvert;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 字段名处理工具</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.03 11:50
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class FieldConvertUtils {

    /**
     * 字段转换
     *
     * @param fields the fields
     * @return the string [ ]
     * @since 1.0.0
     */
    @NotNull
    public static String[] convert(@NotNull String[] fields) {
        return Arrays.stream(fields).map(FieldConvertUtils::convert).toArray(String[]::new);
    }

    /**
     * Field convert string.
     *
     * @param field the field
     * @return the string
     * @since 1.0.0
     */
    public static String convert(String field) {
        return CustomMongoMappingContext.getFieldConvert() == FieldConvert.UNDERSCORE
               ? StringUtils.humpToUnderline(field)
               : field;
    }

}
