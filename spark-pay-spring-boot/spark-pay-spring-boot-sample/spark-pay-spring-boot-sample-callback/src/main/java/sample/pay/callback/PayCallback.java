package sample.pay.callback;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.util.ThreadUtils;
import info.spark.starter.pay.callback.Callback;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import sample.pay.entity.dto.CallbackDTO;
import sample.pay.entity.po.PaymentTransaction;
import sample.pay.entity.po.PaymentTransactionLog;
import sample.pay.service.PaymentTransactionLogRepositoryService;
import sample.pay.service.PaymentTransactionRepositoryService;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.01 22:14
 * @since 1.0.0
 */
@Slf4j
@Service
public class PayCallback {
    /** Payment transaction repository service */
    @Resource
    private PaymentTransactionRepositoryService paymentTransactionRepositoryService;
    /** Payment transaction log repository service */
    @Resource
    private PaymentTransactionLogRepositoryService paymentTransactionLogRepositoryService;

    /**
     * 业务回调不要在一个大方法上开事务
     *
     * @param callback callback
     * @return the object
     * @since 1.0.0
     */
    @Callback(value = PayCallbackServiceImpl.class, timeout = 30, retries = 10)
    public Object callback(@NotNull CallbackDTO callback) {
        log.info("开始执行回调业务逻辑");

        ThreadUtils.sleep(5, TimeUnit.SECONDS);
        // 更新支付状态
        PaymentTransaction one = this.paymentTransactionRepositoryService
            .getOne(new QueryWrapper<PaymentTransaction>()
                        .eq(PaymentTransaction.PAY_NO, callback.getPayNo()));
        one.setPaymentStatus(callback.getPaymentStatus());

        this.paymentTransactionRepositoryService.updateById(one);

        // 保存第三方交易单号
        PaymentTransactionLog transactionLog = this.paymentTransactionLogRepositoryService
            .getOne(new QueryWrapper<PaymentTransactionLog>()
                        .eq(PaymentTransactionLog.TRANSACTION_ID, one.getId()));
        transactionLog.setTransactionNo(callback.getTransactionNo());

        transactionLog.setAsyncLog("异步回调日志: " + JsonUtils.toJson(callback));
        this.paymentTransactionLogRepositoryService.updateById(transactionLog);

        log.info("回调业务逻辑执行完成");
        return callback;
    }
}
