package info.spark.starter.cases.service;

import info.spark.starter.repository.TestRepositoryService;
import info.spark.starter.test.SparkTest;

import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import lombok.SneakyThrows;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.16 18:06
 * @since 1.8.0
 */
@SparkTest
public class TestRepositoryServiceTest {

    /** Test repository service */
    @Resource
    private TestRepositoryService testRepositoryService;

    /**
     * Test 1
     *
     * @since 1.8.0
     */
    @SneakyThrows
    @Test
    void test_1() {
        this.testRepositoryService.delete(1L);
    }

}
