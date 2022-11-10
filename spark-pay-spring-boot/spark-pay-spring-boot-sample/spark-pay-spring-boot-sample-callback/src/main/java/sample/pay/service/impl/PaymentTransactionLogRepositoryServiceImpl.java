package sample.pay.service.impl;

import info.spark.starter.mybatis.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sample.pay.dao.PaymentTransactionLogDao;
import sample.pay.entity.po.PaymentTransactionLog;
import sample.pay.service.PaymentTransactionLogRepositoryService;

/**
 * <p>Description: 支付交易日志 服务接口实现类 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:03
 * @since 1.0.0
 */
@Slf4j
@Service
public class PaymentTransactionLogRepositoryServiceImpl extends BaseServiceImpl<PaymentTransactionLogDao, PaymentTransactionLog> implements PaymentTransactionLogRepositoryService {

    /**
     * Gets log by pan no *
     *
     * @param payNo pay no
     * @return the log by pan no
     * @since 1.0.0
     */
    @Override
    public PaymentTransactionLog getLogByPanNo(String payNo) {

        return this.baseMapper.getLogByPanNo(payNo);
    }
}
