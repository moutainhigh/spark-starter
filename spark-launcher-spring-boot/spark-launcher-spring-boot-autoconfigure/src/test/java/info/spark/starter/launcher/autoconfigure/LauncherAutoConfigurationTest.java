package info.spark.starter.launcher.autoconfigure;

import info.spark.starter.test.SparkTest;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.07 21:25
 * @since 1.0.0
 */
@Slf4j
@SparkTest(classes = LauncherAutoConfiguration.class)
class LauncherAutoConfigurationTest {

    /**
     * Test start
     *
     * @since 1.0.0
     */
    @Test
    void test_start() {
        log.info("");
    }
}
