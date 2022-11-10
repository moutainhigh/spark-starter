package sample.pay.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import info.spark.starter.common.base.BaseExtendPO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>Description: 支付交易日志 实体类  </p>
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
@TableName("payment_transaction_log")
public class PaymentTransactionLog extends BaseExtendPO<Long, PaymentTransactionLog> {

    /** SYNCH_LOG */
    public static final String SYNCH_LOG = "synch_log";
    /** ASYNC_LOG */
    public static final String ASYNC_LOG = "async_log";
    /** TRANSACTION_ID */
    public static final String TRANSACTION_ID = "pay_id";
    /** TRANSACTION_NO */
    public static final String TRANSACTION_NO = "transaction_no";
    /** REVISION */
    public static final String REVISION = "revision";
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
