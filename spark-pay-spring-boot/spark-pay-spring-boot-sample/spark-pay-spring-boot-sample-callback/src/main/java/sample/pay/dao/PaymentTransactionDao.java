package sample.pay.dao;

import info.spark.starter.common.base.BaseDao;

import org.apache.ibatis.annotations.Mapper;

import sample.pay.entity.po.PaymentTransaction;

/**
 * <p>Description: 支付交易 Dao 接口  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:28
 * @since 1.0.0
 */
@Mapper
public interface PaymentTransactionDao extends BaseDao<PaymentTransaction> {

}
