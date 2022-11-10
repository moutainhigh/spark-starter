package sample.pay.enums;

import info.spark.starter.common.enums.SerializeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.01 21:56
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum PaymentStatus implements SerializeEnum<Integer> {
    /** Unpay payment status */
    UNPAY(0, "未支付"),
    /** Pay payment status */
    PAY(1, "已支付"),
    /** Pay tomeout payment status */
    PAY_TOMEOUT(2, "支付超时"),
    /** Pay failed payment status */
    PAY_FAILED(3, "支付失败");

    /** Value */
    private final Integer value;
    /** Desc */
    private final String desc;
}
