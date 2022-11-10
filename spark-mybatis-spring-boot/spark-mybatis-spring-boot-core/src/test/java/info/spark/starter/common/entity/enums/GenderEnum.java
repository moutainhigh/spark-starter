package info.spark.starter.common.entity.enums;

import info.spark.starter.common.enums.SerializeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email dongshijie @wl.com
 * @date 2020.01.27 18:23
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
