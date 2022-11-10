package info.spark.starter.launcher;

import info.spark.starter.launcher.app.ApplicationTest;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.07 21:24
 * @since 1.0.0
 */
@Slf4j
class SparkStarterTest {

    /**
     * 直接运行一个 @SpringBootApplication 应用
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    void test_run() throws Exception {
        SparkStarter.run(ApplicationTest.class);
    }

}
