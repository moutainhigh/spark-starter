package info.spark.starter.service.impl;

import info.spark.starter.common.base.CrudDelegateImpl;
import info.spark.starter.entity.dto.TestDTO;
import info.spark.starter.repository.TestRepositoryService;
import info.spark.starter.service.TestService;

import org.springframework.stereotype.Service;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.16 22:58
 * @since 1.8.0
 */
@Service
public class TestServiceImpl extends CrudDelegateImpl<TestRepositoryService, TestDTO> implements TestService {
}
