package sample.pay.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import info.spark.starter.common.base.BaseExtendPO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>Description: 其他业务表 实体类  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:12
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("business")
public class Business extends BaseExtendPO<Long, Business> {

    /** SAY */
    public static final String SAY = "say";
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 其他业务操作 */
    private String say;

}
