package info.spark.starter.es.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.25 17:40
 * @since 1.8.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ESMappingType {

    /**
     * 全文搜索。
     */
    text("text"),

    /**
     * keyword类型适用于索引结构化(排序、过滤、聚合)，只能通过精确值搜索到。
     */
    keyword("keyword"),

    /**
     * -128~127 在满足需求的情况下，尽可能选择范围小的数据类型。
     */
    _byte("byte"),

    /**
     * -32768~32767
     */
    _short("short"),

    /**
     * -2^31~2^31-1
     */
    _integer("integer"),

    /**
     * -2^63~2^63-1
     */
    _long("long"),

    /**
     * 64位双精度IEEE 754浮点类型
     */
    _double("double"),

    /**
     * 32位单精度IEEE 754浮点类型
     */
    _float("float"),

    /**
     * 16位半精度IEEE 754浮点类型
     */
    half_float("half_float"),

    /**
     * 缩放类型的的浮点数
     */
    scaled_float("scaled_float"),

    /**
     * 时间类型
     */
    date("date"),

    /** Boolean es mapping type */
    _boolean("boolean"),

    /**
     * 范围类型
     */
    range("range"),

    /**
     * 嵌套类型
     */
    nested("nested"),

    /**
     * 地理坐标
     */
    geo_point("geo_point"),

    /**
     * 地理地图
     */
    geo_shape("geo_shape"),

    /**
     * 二进制类型
     */
    binary("binary"),

    /**
     * ip 192.168.1.2
     */
    ip("ip");

    /** Value */
    private String value;

}
