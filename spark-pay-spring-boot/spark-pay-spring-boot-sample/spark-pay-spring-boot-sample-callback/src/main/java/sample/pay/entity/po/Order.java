package sample.pay.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import info.spark.starter.common.base.BaseExtendPO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>Description: 订单表 实体类  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:03
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("order")
public class Order extends BaseExtendPO<Long, Order> {

    /** ORDER_NO */
    public static final String ORDER_NO = "order_no";
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 订单号 */
    private String orderNo;

}
