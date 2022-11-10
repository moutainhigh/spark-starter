package sample.pay.entity.dto;

import info.spark.starter.common.base.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: 支付交易日志 数据传输实体 (根据业务需求添加字段) </p>
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
public class PaymentTransactionLogDTO extends BaseDTO<Long> {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 同步回调日志 */
    private String synchLog;
    /** 异步回调日志 */
    private String asyncLog;
    /** 支付交易ID */
    private Long payId;
    /** 第三方交易号 */
    private String transactionNo;
    /** 乐观锁 */
    private Integer revision;
}
