package info.spark.starter.dao;

import info.spark.starter.common.base.BaseDao;
import info.spark.starter.entity.po.Test;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Description: sku Dao 接口  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.11 21:33
 * @since 1.0.0
 */
@Mapper
public interface TestDao extends BaseDao<Test> {

}
