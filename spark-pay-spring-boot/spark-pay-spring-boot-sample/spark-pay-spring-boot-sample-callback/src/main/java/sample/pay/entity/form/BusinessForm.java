package sample.pay.entity.form;

import info.spark.starter.common.base.BaseForm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: 其他业务表 视图实体 (根据业务需求添加字段) </p>
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
public class BusinessForm extends BaseForm<Long> {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** todo: [自动生成的字段, 避免此实体没有字段导致启动失败的问题, 可删除] */
    private String autoField;
}
