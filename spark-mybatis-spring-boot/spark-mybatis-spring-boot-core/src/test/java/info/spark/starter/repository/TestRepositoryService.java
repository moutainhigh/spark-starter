package info.spark.starter.repository;

import info.spark.starter.common.base.IRepositoryService;
import info.spark.starter.entity.dto.TestDTO;
import info.spark.starter.entity.po.Test;
import info.spark.starter.mybatis.service.IExchangeService;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 19:31
 * @since 1.8.0
 */
public interface TestRepositoryService extends IRepositoryService<TestDTO>, IExchangeService<Test, TestDTO> {
}
