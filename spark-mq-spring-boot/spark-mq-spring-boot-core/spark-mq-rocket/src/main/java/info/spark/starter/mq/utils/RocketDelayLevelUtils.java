package info.spark.starter.mq.utils;

import java.util.Arrays;

import lombok.experimental.UtilityClass;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.11 13:43
 * @since 1.7.0
 */
@UtilityClass
public class RocketDelayLevelUtils {

    /** rocket mq delay level */
    public static final Long[] DELAY_LEVEL = new Long[] {
        // 1s 5s 10s 30s
        1000L, 5 * 1000L, 10 * 1000L, 30 * 1000L,

        // 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m
        60000L, 2 * 60000L, 3 * 60000L, 4 * 60000L, 5 * 60000L, 6 * 60000L,
        7 * 60000L, 8 * 60000L, 9 * 60000L, 10 * 60000L, 20 * 60000L, 30 * 60000L,

        // 1h 2h 6h 12h
        60 * 60000L, 2 * 60 * 60000L, 6 * 60 * 60000L, 12 * 60 * 60000L
    };

    /**
     * 通过毫秒获取 rocket mq 相近延迟等级
     *
     * @param millisecond millisecond
     * @return the similar level
     * @since 1.7.0
     */
    public static int getSimilarLevel(Long millisecond) {
        // 通过毫秒获取最近的延迟等级
        int level = Arrays.binarySearch(DELAY_LEVEL, millisecond);
        if (level < 0) {
            if (level == -1) {
                level = 0;
            } else if (level == -(DELAY_LEVEL.length + 1)) {
                level = DELAY_LEVEL.length - 1;
            } else {
                int positive = Math.abs(level);
                level = (DELAY_LEVEL[positive - 1] - millisecond < millisecond - DELAY_LEVEL[positive - 2]) ? positive - 1 : positive - 2;
            }
        }
        return level;
    }
}
