package sample.pay.service;

import info.spark.starter.mybatis.service.BaseService;

import sample.pay.entity.po.PaymentTransactionLog;

/**
 * <p>Description: 支付交易日志 服务接口 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:03
 * @since 1.0.0
 */
public interface PaymentTransactionLogRepositoryService extends BaseService<PaymentTransactionLog> {

    /**
     * Gets log by pan no *
     *
     * @param payNo pay no
     * @return the log by pan no
     * @since 1.0.0
     */
    PaymentTransactionLog getLogByPanNo(String payNo);
}

