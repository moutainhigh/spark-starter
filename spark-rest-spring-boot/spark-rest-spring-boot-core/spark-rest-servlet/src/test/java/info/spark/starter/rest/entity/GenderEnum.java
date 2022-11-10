package info.spark.starter.rest.entity;

import info.spark.starter.common.enums.SerializeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 15:17
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum GenderEnum implements SerializeEnum<Integer> {
    /** Women gender enum */
    WOMEN(0, "女"),
    /** Man gender enum */
    MAN(1, "男"),
    /** Unknown gender enum */
    UNKNOWN(2, "未知");

    /** 数据库存储的值 */
    private final Integer value;
    /** 枚举描述 */
    private final String desc;

}
