package info.spark.starter.template.autoconfigure;

import info.spark.starter.test.SparkTest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

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
@SparkTest(classes = TemplateAutoConfiguration.class)
class TemplateAutoConfigurationTest {

    /** Properties */
    @Resource
    private TemplateProperties properties;

    /**
     * Test
     *
     * @since 1.5.0
     */
    @Test
    void test() {
        log.info("key = {}", this.properties.getKey());
        log.info("humpKey = {}", this.properties.getHumpKey());
        log.info("enable = {}", this.properties.isEnable());
        log.info("minWeight = {}", this.properties.getMinWeight());
        log.info("maxWeight = {}", this.properties.getMaxWeight());
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.08 13:45
     * @since 1.0.0
     */
    @TestPropertySource(properties = {
        "spark.template.hump-key=hump-key"
    })
    static class PropertiesTest1 extends TemplateAutoConfigurationTest {
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.08 13:45
     * @since 1.0.0
     */
    @TestPropertySource(properties = {
        "spark.template.hump_key=hump_key"
    })
    static class PropertiesTest2 extends TemplateAutoConfigurationTest {
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.08 13:45
     * @since 1.0.0
     */
    @TestPropertySource(properties = {
        "spark.template.humpKey=humpKey"
    })
    static class PropertiesTest3 extends TemplateAutoConfigurationTest {
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.08 13:45
     * @since 1.0.0
     */
    @TestPropertySource(properties = {
        "spark.template.humpkey=humpkey"
    })
    static class PropertiesTest4 extends TemplateAutoConfigurationTest {
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.08 13:45
     * @since 1.0.0
     */
    @TestPropertySource(properties = {
        "spark.template.HUMP_KEY=HUMP_KEY"
    })
    static class PropertiesTest5 extends TemplateAutoConfigurationTest {
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.08 13:45
     * @since 1.0.0
     */
    @TestPropertySource(properties = {
        "spark.template.enable=foo"
    })
    static class PropertiesTest6 extends TemplateAutoConfigurationTest {
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.08 13:45
     * @since 1.0.0
     */
    @TestPropertySource(properties = {
        "spark.template.key="
    })
    static class PropertiesTest7 extends TemplateAutoConfigurationTest {
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.08 13:45
     * @since 1.0.0
     */
    @TestPropertySource(properties = {
        "spark.template.min-weight=5kg",
        "spark.template.max-weight=15kg"
    })
    static class PropertiesTest8 extends TemplateAutoConfigurationTest {
    }

}
