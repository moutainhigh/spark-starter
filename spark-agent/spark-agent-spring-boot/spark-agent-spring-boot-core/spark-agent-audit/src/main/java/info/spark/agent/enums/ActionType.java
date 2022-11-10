package info.spark.agent.enums;

import info.spark.starter.common.enums.SerializeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作动作
 *
 * @author sujian
 * @version 1.0.0
 * @email "mailto:sujian@gmail.com"
 * @date 2020.05.26 11:39
 * @since 1.6.0
 */
@Getter
@AllArgsConstructor
public enum ActionType implements SerializeEnum<String> {
    /** Add operation action */
    ADD("INSERT", "新增"),
    /** Update operation action */
    UPDATE("UPDATE", "修改"),
    /** Delete operation action */
    DELETE("DELETE", "删除");

    /** Code */
    private final String value;
    /** Name */
    private final String desc;

}
