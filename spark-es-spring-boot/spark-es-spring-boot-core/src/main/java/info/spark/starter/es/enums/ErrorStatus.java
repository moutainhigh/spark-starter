package info.spark.starter.es.enums;

import info.spark.starter.common.enums.SerializeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.26 10:04
 * @since 1.8.0
 */
@Getter
@AllArgsConstructor
public enum ErrorStatus implements SerializeEnum<Integer> {

    /** 404 error status */
    _404(404, "404");

    /** 数据库存储的值 */
    private final Integer value;
    /** 枚举描述 */
    private final String desc;

}
