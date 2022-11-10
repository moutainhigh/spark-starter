package info.spark.starter.entity.dto;

import info.spark.starter.common.base.BaseDTO;

import java.util.Date;

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
 * @date 2021.03.15 19:32
 * @since 1.8.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TestDTO extends BaseDTO<Long> {

    /** serialVersionUID */
    private static final long serialVersionUID = 2274276174238764506L;
    /** Create time */
    private Date createTime;
    /** Update time */
    private Date updateTime;
    /** todo: [自动生成的字段, 避免此实体没有字段导致启动失败的问题, 可删除] */
    private String autoField;
}
