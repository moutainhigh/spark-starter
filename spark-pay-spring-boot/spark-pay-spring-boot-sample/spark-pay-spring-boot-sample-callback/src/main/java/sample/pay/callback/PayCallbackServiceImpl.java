package sample.pay.callback;

import info.spark.starter.pay.callback.CallbackService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import sample.pay.entity.dto.CallbackDTO;
import sample.pay.entity.po.PaymentTransactionLog;
import sample.pay.service.PaymentTransactionLogRepositoryService;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.01 22:19
 * @since 1.0.0
 */
@Slf4j
@Service("PayCallbackServiceImpl")
public class PayCallbackServiceImpl implements CallbackService {
    /** Payment transaction log repository service */
    @Resource
    private PaymentTransactionLogRepositoryService paymentTransactionLogRepositoryService;

    /**
     * 实现检查逻辑, 这里通过日志记录来判断是否支付事务是否提交, 如果事务未提交, 将查询不到数据.
     *
     * @param args args
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean check(Object... args) {
        log.info("回调业务检查: 回调参数: [{}]", args);
        CallbackDTO callbackParam = (CallbackDTO) args[0];

        PaymentTransactionLog paymentTransactionLog = this.paymentTransactionLogRepositoryService.getLogByPanNo(callbackParam.getPayNo());
        log.info("回调检查业务事务是否提交: [{}]", paymentTransactionLog);
        return paymentTransactionLog != null;
    }
}
