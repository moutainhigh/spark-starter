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
public enum UserType implements SerializeEnum<String> {
    /** Sa user type */
    SA("sa", "普通用户"),
    /** Admin user type */
    ADMIN("admin", "管理员");

    /** 数据库存储的值 */
    private final String value;
    /** 枚举描述 */
    private final String desc;
}


