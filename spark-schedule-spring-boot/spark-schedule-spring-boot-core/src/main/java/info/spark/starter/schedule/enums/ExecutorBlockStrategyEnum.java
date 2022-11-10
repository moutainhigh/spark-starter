package info.spark.starter.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: 阻塞处理策略 </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.23 14:45
 * @since 1.4.0
 */
@Getter
@AllArgsConstructor
public enum ExecutorBlockStrategyEnum {
    /** 单机串行 */
    SERIAL_EXECUTION("Serial execution"),
    /** 丢弃后续调度 */
    DISCARD_LATER("Discard Later"),
    /** 覆盖之前调度 */
    COVER_EARLY("Cover Early");

    /** Title */
    private final String title;
}
