package info.spark.starter.openness.autoconfigure;

import info.spark.starter.test.SparkTest;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.06 11:29
 * @since 1.0.0
 */
@Slf4j
@SparkTest(classes = OpennessAutoConfiguration.class)
class OpennessAutoConfigurationTest {

    /** Properties */
    @Resource
    private OpennessProperties properties;

}
