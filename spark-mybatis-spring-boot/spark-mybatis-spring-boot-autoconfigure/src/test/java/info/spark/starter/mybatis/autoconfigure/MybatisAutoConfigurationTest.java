package info.spark.starter.mybatis.autoconfigure;

import info.spark.starter.common.constant.App;
import info.spark.starter.common.context.SpringContext;
import info.spark.starter.test.SparkTest;

import org.junit.jupiter.api.Test;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.06 13:58
 * @since 1.0.0
 */
@SparkTest(classes = MybatisAutoConfiguration.class)
class MybatisAutoConfigurationTest {

    /**
     * Test antocinfiguration
     *
     * @since 1.0.0
     */
    @Test
    void test_antocinfiguration() {
        System.setProperty(App.DEBUG_MODEL, "true");
        SpringContext.showDebugInfo();
    }
}
