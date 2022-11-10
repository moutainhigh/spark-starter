package info.spark.starter.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.23 14:52
 * @since 1.4.0
 */
@Getter
@AllArgsConstructor
public enum ExecutorRouteStrategyEnum {

    /** First executor route strategy enum */
    FIRST("第一个"),
    /** Last executor route strategy enum */
    LAST("最后一个"),
    /** Round executor route strategy enum */
    ROUND("轮询"),
    /** Random executor route strategy enum */
    RANDOM("随机"),
    /** Consistent hash executor route strategy enum */
    CONSISTENT_HASH("一致性HASH"),
    /** Least frequently used executor route strategy enum */
    LEAST_FREQUENTLY_USED("最不经常使用"),
    /** Least recently used executor route strategy enum */
    LEAST_RECENTLY_USED("最近最久未使用"),
    /** Failover executor route strategy enum */
    FAILOVER("故障转移"),
    /** Busyover executor route strategy enum */
    BUSYOVER("忙碌转移"),
    /** Sharding broadcast executor route strategy enum */
    SHARDING_BROADCAST("分片广播");

    /** Title */
    private final String title;
}
