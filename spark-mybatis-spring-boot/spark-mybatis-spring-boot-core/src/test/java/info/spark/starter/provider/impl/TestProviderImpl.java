package info.spark.starter.provider.impl;

import info.spark.starter.common.base.CrudDelegateImpl;
import info.spark.starter.entity.dto.TestDTO;
import info.spark.starter.provider.TestProvider;
import info.spark.starter.repository.TestRepositoryService;

import org.springframework.stereotype.Component;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.16 09:48
 * @since 1.8.0
 */
@Component
public class TestProviderImpl extends CrudDelegateImpl<TestRepositoryService, TestDTO> implements TestProvider {

}
