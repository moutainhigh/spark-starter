package info.spark.starter.logsystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test whether invoking the SLF4J API causes problems or not.
 *
 * @author Ceki Gulcu
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 12:56
 * @since 1.0.0
 */
class InvocationTest {

    /** Old */
    private final PrintStream old = System.err;

    /**
     * Sets up *
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @BeforeEach
    void setUp() throws Exception {
        System.setErr(new SilentPrintStream(this.old));
    }

    /**
     * Tear down *
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @AfterEach
    void tearDown() throws Exception {

        System.setErr(this.old);
    }

    /**
     * Test 1
     *
     * @since 1.0.0
     */
    @Test
    void test1() {
        Logger logger = LoggerFactory.getLogger("test1");
        logger.debug("Hello world.");
    }

    /**
     * Test 2
     *
     * @since 1.0.0
     */
    @Test
    void test2() {
        Integer i1 = 1;
        Integer i2 = 2;
        Integer i3 = 3;
        Exception e = new Exception("This is a test exception.");
        Logger logger = LoggerFactory.getLogger("test2");

        logger.debug("Hello world 1.");
        logger.debug("Hello world {}", i1);
        logger.debug("val={} val={}", i1, i2);
        logger.debug("val={} val={} val={}", i1, i2, i3);

        logger.debug("Hello world 2", e);
        logger.info("Hello world 2.");

        logger.warn("Hello world 3.");
        logger.warn("Hello world 3", e);

        logger.error("Hello world 4.");
        logger.error("Hello world {}", 3);
        logger.error("Hello world 4.", e);
    }

    /**
     * Test null parameter bug 78
     *
     * @since 1.0.0
     */
    @Test
    void testNullParameter_BUG78() {
        Logger logger = LoggerFactory.getLogger("testNullParameter_BUG78");
        String msg = "hello {}";
        logger.info(msg, (Object[]) null);
    }

    /**
     * Test null
     *
     * @since 1.0.0
     */
    @Test
    void testNull() {
        Logger logger = LoggerFactory.getLogger("testNull");
        logger.debug(null);
        logger.info(null);
        logger.warn(null);
        logger.error(null);

        Exception e = new Exception("This is a test exception.");
        logger.debug(null, e);
        logger.info(null, e);
        logger.warn(null, e);
        logger.error(null, e);
    }

    /**
     * Test marker
     *
     * @since 1.0.0
     */
    @Test
    void testMarker() {
        Logger logger = LoggerFactory.getLogger("testMarker");
        Marker blue = MarkerFactory.getMarker("BLUE");
        logger.debug(blue, "hello");
        logger.info(blue, "hello");
        logger.warn(blue, "hello");
        logger.error(blue, "hello");

        logger.debug(blue, "hello {}", "world");
        logger.info(blue, "hello {}", "world");
        logger.warn(blue, "hello {}", "world");
        logger.error(blue, "hello {}", "world");

        logger.debug(blue, "hello {} and {} ", "world", "universe");
        logger.info(blue, "hello {} and {} ", "world", "universe");
        logger.warn(blue, "hello {} and {} ", "world", "universe");
        logger.error(blue, "hello {} and {} ", "world", "universe");
    }

    /**
     * Test mdc
     *
     * @since 1.0.0
     */
    @Test
    void testMDC() {
        MDC.put("k", "v");
        assertNull(MDC.get("k"));
        MDC.remove("k");
        assertNull(MDC.get("k"));
        MDC.clear();
    }
}
