package info.spark.starter.zookeeper.autoconfigure;

import info.spark.starter.test.SparkTest;
import info.spark.starter.util.ThreadUtils;
import info.spark.starter.zookeeper.ZookeeperService;

import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.29 17:08
 * @since 1.8.0
 */
@Slf4j
@SparkTest(classes = ZookeeperAutoConfiguration.class)
class ZookeeperAutoConfigurationTest {
    /** Zookeeper service */
    @Resource
    private ZookeeperService zookeeperService;

    /**
     * Test init
     *
     * @since 1.0.0
     */
    @Test
    void test_init() {
        this.zookeeperService.isExistNode("/");

        ThreadUtils.join();
    }
}
