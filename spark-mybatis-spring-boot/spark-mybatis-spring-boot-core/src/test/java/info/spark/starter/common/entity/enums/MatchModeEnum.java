package info.spark.starter.common.entity.enums;

import info.spark.starter.common.enums.SerializeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: 域名解析匹配模式</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:22
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum MatchModeEnum implements SerializeEnum<String> {
    /** Prefix match mode enum */
    PREFIX("prefix", "前缀匹配: 如 www. 匹配 www.sina.com.cn、www.163.com 等"),
    /** Suffix match mode enum */
    SUFFIX("suffix", "后缀匹配: 如.baidu.com 匹配 www.baidu.com、tieba.baidu.com 等"),
    /** Contains match mode enum */
    CONTAINS("contains", "包含: 只要解析的域名中包含指定的域名即可"),
    /** Equals match mode enum */
    EQUALS("equals", "完全匹配: 必须与查询的域名一致");

    /** 数据库存储的值 */
    private final String value;
    /** 枚举描述 */
    private final String desc;
}
