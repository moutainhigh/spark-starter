package info.spark.start.mqtt.core.common;

import info.spark.starter.common.enums.SerializeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: 主题类型 </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.23 10:45
 * @since 2.1.0
 */
@AllArgsConstructor
@Getter
public enum TopicType implements SerializeEnum<Integer> {

    /** Sys topic type */
    NORMAL(1, "普通主题"),
    /** Sys topic type */
    SYS(2, "系统主题");

    /** Value */
    private final Integer value;

    /** Desc */
    private final String desc;

}
