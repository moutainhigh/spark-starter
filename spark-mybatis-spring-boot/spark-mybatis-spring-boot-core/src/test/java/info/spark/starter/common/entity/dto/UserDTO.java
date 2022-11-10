package info.spark.starter.common.entity.dto;

import info.spark.starter.common.base.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 用户信息表数据传输对象实体类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:22
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseDTO<Long> {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 前端查询参数使用 GenderEnum.value */
    private Integer gender;
    /** Enable */
    private Boolean enable;
    /** Deleted */
    private Boolean deleted;
}
