package info.spark.starter.entity.form;

import info.spark.starter.common.base.BaseQuery;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.16 17:51
 * @since 1.8.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页查询条件")
public class TestQuery extends BaseQuery<Long> {
    /** serialVersionUID */
    private static final long serialVersionUID = -1136618735069307518L;
    /** todo: [自动生成的字段, 避免此实体没有字段导致启动失败的问题, 可删除] */
    private String autoField;
}
