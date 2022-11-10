package info.spark.starter.repository.impl;

import info.spark.starter.converter.TestServiceConverter;
import info.spark.starter.dao.TestDao;
import info.spark.starter.entity.dto.TestDTO;
import info.spark.starter.entity.po.Test;
import info.spark.starter.mybatis.service.impl.ExchangeServiceImpl;
import info.spark.starter.repository.TestRepositoryService;

import org.springframework.stereotype.Service;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 19:31
 * @since 1.8.0
 */
@Service
public class TestRepositoryServiceImpl extends ExchangeServiceImpl<TestDao, Test, TestDTO, TestServiceConverter>
    implements TestRepositoryService {
}
