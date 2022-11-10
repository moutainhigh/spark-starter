package info.spark.starter.common.entity.enums;

import info.spark.starter.common.enums.SerializeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: 应答 IP 分发模式 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:23
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DispatchModeEnum implements SerializeEnum<String> {
    /** Round robin dispatch mode enum */
    ROUND_ROBIN("round-robin", "轮循: 依次应答"),
    /** Ip hash */
    IP_HASH("iphash", "IP Hash: 与请求来源 IP 绑定"),
    /** Random dispatch mode enum */
    RANDOM("random", "随机");

    /** 数据库存储的值 */
    private final String value;
    /** 枚举描述 */
    private final String desc;
}

