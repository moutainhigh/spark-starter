package sample.pay.entity.dto;

import info.spark.starter.common.base.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: 其他业务表 数据传输实体 (根据业务需求添加字段) </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:12
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BusinessDTO extends BaseDTO<Long> {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 其他业务操作 */
    private String say;
}
