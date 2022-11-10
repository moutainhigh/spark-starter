package info.spark.starter.common.entity.vo;

import info.spark.starter.common.base.BaseVO;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 用户信息表视图实体类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.22 20:33
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UserVO对象", description = "用户信息表")
public class UserVO extends BaseVO<Long> {
    /** serialVersionUID */
    private static final long serialVersionUID = 3808259558627950290L;
    /** Gender */
    private String gender;
    /** Enable */
    private String enable;
    /** Deleted */
    private String deleted;

}
