package info.spark.starter.launcher.listener;

import info.spark.starter.common.exception.PropertiesException;
import info.spark.starter.util.StringUtils;
import info.spark.starter.test.SparkTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.23 14:48
 * @since 1.0.0
 */
@Slf4j
@Import(RangeRandomPortListener.class)
@SparkTest(classes = RangeRandomPortListenerTest.TestAppConfiguration.class, properties = {
    "server.port=${range.random.int}",
    "server.port_1=${range.random.int(1024, 1024)}",
    "server.port_2=${range.random.int(1024, xxx)}",
    "server.port_3=${range.random.int(1024, xxx, yyy)}",
    "test.port_1=${range.random.int(1024,10000 )}",
    "test.port_2=${range.random.int( 1024 , 10000 )}",
    "test.port_3=${range.random.key( 64 )}",
    "test.port_4=${range.random.key(12 )}",
    "test.port_6=${range.random.key(xxxx )}",
    "test.port_7=${range.random.key}",
})
class RangeRandomPortListenerTest {

    /** Environment */
    @Resource
    private Environment environment;

    /**
     * 集成测试
     *
     * @since 1.0.0
     */
    @Test
    void test_() {
        Assertions.assertTrue(18080 > Integer.parseInt(Objects.requireNonNull(this.environment.getProperty("server.port")))
                              && Integer.parseInt(Objects.requireNonNull(this.environment.getProperty("server.port"))) > 8080);

        Assertions.assertEquals(1024, Integer.parseInt(Objects.requireNonNull(this.environment.getProperty("server.port_1"))));

        Assertions.assertThrows(PropertiesException.class, () -> {
            this.environment.getProperty("server.port_2");
        });

        Assertions.assertThrows(PropertiesException.class, () -> {
            this.environment.getProperty("server.port_3");
        });

        Assertions.assertEquals(12, Objects.requireNonNull(this.environment.getProperty("test.port_4")).length());

        Assertions.assertThrows(PropertiesException.class, () -> {
            this.environment.getProperty("test.port_6");
        });

        Assertions.assertEquals(64, Objects.requireNonNull(this.environment.getProperty("test.port_7")).length());
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.23 15:00
     * @since 1.0.0
     */
    @Configuration(proxyBeanMethods = false)
    static class TestAppConfiguration {
    }

    /**
         * <p>Description: 单元测试 </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.23 17:04
     * @since 1.0.0
     */
    static class UnitTest {
        /**
         * Test 1
         *
         * @since 1.0.0
         */
        @Test
        void test_1() {
            Assertions.assertTrue(this.checkPattern("[61000,61100]"));
            // 不能出现空格
            Assertions.assertFalse(this.checkPattern("[61000, 61100]"));
            Assertions.assertFalse(this.checkPattern("[-1,xxx]"));
            Assertions.assertFalse(this.checkPattern("[-1,-2]"));
        }

        /**
         * Check pattern boolean
         *
         * @param range range
         * @return the boolean
         * @since 1.0.0
         */
        private boolean checkPattern(String range) {
            if (StringUtils.isBlank(range)) {
                return false;
            }
            String pattern = "^(\\[\\d+),(\\d+])$";
            return Pattern.matches(pattern, range);
        }
    }
}
