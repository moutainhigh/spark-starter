package info.spark.starter.endpoint.initialization;

import info.spark.starter.common.enums.SerializeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:01
 * @since 2022.1.1
 */
@Getter
@AllArgsConstructor
public enum WarmUpEnum implements SerializeEnum<String> {
    /** Warm warm up enum */
    WARM("warm", ""),
    /** Me warm up enum */
    ME("me", ""),
    /** Up warm up enum */
    UP("up", "");

    /** Value */
    private final String value;
    /** Desc */
    private final String desc;
}
