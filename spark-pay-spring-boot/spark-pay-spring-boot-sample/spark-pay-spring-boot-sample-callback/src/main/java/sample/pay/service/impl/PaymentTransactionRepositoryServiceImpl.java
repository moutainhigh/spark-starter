package sample.pay.service.impl;

import info.spark.starter.mybatis.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sample.pay.dao.PaymentTransactionDao;
import sample.pay.entity.po.PaymentTransaction;
import sample.pay.service.PaymentTransactionRepositoryService;

/**
 * <p>Description: 支付交易 服务接口实现类 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:03
 * @since 1.0.0
 */
@Slf4j
@Service
public class PaymentTransactionRepositoryServiceImpl extends BaseServiceImpl<PaymentTransactionDao, PaymentTransaction> implements PaymentTransactionRepositoryService {

}
