package sample.pay.entity.form;

import info.spark.starter.common.base.BaseForm;

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
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.02 16:26
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CallbackForm extends BaseForm<Long> {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 支付交易ID */
    private String payNo;
    /** 第三方交易号 */
    private String transactionNo;
    /** 支付状态 */
    private PaymentStatus paymentStatus;
    /** 支付金额 */
    private BigDecimal payAmount;
}
