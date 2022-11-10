package info.spark.starter.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import info.spark.starter.common.base.BaseExtendPO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 19:32
 * @since 1.8.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("test")
public class Test extends BaseExtendPO<Long, Test> {
    private static final long serialVersionUID = 1532355638594913285L;
    /** todo: [自动生成的字段, 避免此实体没有字段导致启动失败的问题, 可删除] */
    private String autoField;
}
