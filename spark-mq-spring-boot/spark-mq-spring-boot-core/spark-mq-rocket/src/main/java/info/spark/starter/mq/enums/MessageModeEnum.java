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
public enum MessageModeEnum implements Serializable {
    /** 消息模式 集群 */
    MESSAGE_MODE_CLUSTERING("CLUSTERING"),
    /** 消息模式 广播 */
    MESSAGE_MODE_BROADCASTING("BROADCASTING");
    /** Value */
    private final String value;
}
