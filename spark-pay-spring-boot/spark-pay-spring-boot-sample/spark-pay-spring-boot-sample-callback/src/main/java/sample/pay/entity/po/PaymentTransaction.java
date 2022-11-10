package sample.pay.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import info.spark.starter.common.base.BaseExtendPO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import sample.pay.enums.PaymentStatus;

/**
 * <p>Description: 支付交易 实体类  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:28
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("payment_transaction")
public class PaymentTransaction extends BaseExtendPO<Long, PaymentTransaction> {

    /** PAY_NO */
    public static final String PAY_NO = "pay_no";
    /** PAY_AMOUNT */
    public static final String PAY_AMOUNT = "pay_amount";
    /** PAYMENT_STATUS */
    public static final String PAYMENT_STATUS = "payment_status";
    /** ORDER_ID */
    public static final String ORDER_ID = "order_id";
    /** REVISION */
    public static final String REVISION = "revision";
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 交易编号 */
    private String payNo;
    /** 支付金额 */
    private BigDecimal payAmount;
    /** 支付状态: 0: 待支付, 1: 已经支付, 2: 支付超时, 3:支付失败 */
    private PaymentStatus paymentStatus;
    /** 订单号码 */
    private Long orderId;
    /** 乐观锁 */
    private Integer revision;

}
