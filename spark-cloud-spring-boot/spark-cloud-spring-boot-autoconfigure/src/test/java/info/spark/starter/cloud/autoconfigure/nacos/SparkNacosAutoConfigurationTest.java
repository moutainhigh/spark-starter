package info.spark.starter.cloud.autoconfigure.nacos;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.18 22:43
 * @since 1.0.0
 */
@Slf4j
class SparkNacosAutoConfigurationTest {

    /**
     * Test execute
     *
     * @since 1.0.0
     */
    @Test
    void test_SparkNacosProperties() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SparkNacosAutoConfiguration.class);
        context.start();
        SparkNacosProperties sparkNacosProperties = context.getBean(SparkNacosProperties.class);
        log.info("result: {}", sparkNacosProperties);
    }

}
