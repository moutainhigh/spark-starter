package info.spark.starter.mq.enums;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.12 15:24
 * @since 1.7.0
 */
@Getter
@AllArgsConstructor
public enum ConsumeModeEnum implements Serializable {
    /** 消费模式 有序 (单线程) 或者无序 (多线程) */
    CONSUME_MODE_CONCURRENTLY("CONCURRENTLY"),
    /** CONSUME_MODE_ORDERLY */
    CONSUME_MODE_ORDERLY("ORDERLY");
    /** Value */
    private final String value;
}
