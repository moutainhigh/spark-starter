package sample.pay.dao;

import info.spark.starter.common.base.BaseDao;

import org.apache.ibatis.annotations.Mapper;

import sample.pay.entity.po.Order;

/**
 * <p>Description: 订单表 Dao 接口  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:03
 * @since 1.0.0
 */
@Mapper
public interface OrderDao extends BaseDao<Order> {

}
