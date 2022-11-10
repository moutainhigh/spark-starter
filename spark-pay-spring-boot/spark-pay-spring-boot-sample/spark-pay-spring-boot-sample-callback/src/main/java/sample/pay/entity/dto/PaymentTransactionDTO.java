package sample.pay.entity.dto;

import info.spark.starter.common.base.BaseDTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import sample.pay.enums.PaymentStatus;

/**
 * <p>Description: 支付交易 数据传输实体 (根据业务需求添加字段) </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:28
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PaymentTransactionDTO extends BaseDTO<Long> {
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
