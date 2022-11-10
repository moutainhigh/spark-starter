package info.spark.starter.common.entity.enums;

import info.spark.starter.common.enums.SerializeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:22
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum implements SerializeEnum<Integer> {
    /** Not check user status enum */
    NOT_CHECK(0, "未审核"),
    /** Checking user status enum */
    CHECKING(1, "审核中"),
    /** Check failed user status enum */
    CHECK_FAILED(2, "审核未通过"),
    /** Checked user status enum */
    CHECKED(3, "已锁定"),
    /** Normal user status enum */
    NORMAL(4, "正常状态");

    /** 数据库存储的值 */
    private final Integer value;
    /** 枚举描述 */
    private final String desc;
}
