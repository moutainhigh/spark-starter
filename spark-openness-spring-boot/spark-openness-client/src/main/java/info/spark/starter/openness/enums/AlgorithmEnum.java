package info.spark.starter.openness.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author zhubo
 * @version 1.6.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.13 15:11
 * @since 1.6.0
 */
@Getter
@AllArgsConstructor
public enum  AlgorithmEnum {
    /** Md 5 algorithm enum */
    MD5("MD5"),
    /** Aes algorithm enum */
    AES("AES");
    /** name */
    private final String name;
}
