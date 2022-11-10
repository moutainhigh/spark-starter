package info.spark.starter.logsystem.impl;

import info.spark.starter.logsystem.SimpleLogger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 12:56
 * @since 1.0.0
 */
class SimpleLoggerTest {

    /** A key */
    private final String A_KEY = SimpleLogger.LOG_KEY_PREFIX + "a";
    /** Original */
    private final PrintStream original = System.out;
    /** Bout */
    private final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    /** Replacement */
    private final PrintStream replacement = new PrintStream(this.bout);

    /**
     * Before
     *
     * @since 1.0.0
     */
    @BeforeEach
    void before() {
        System.setProperty(this.A_KEY, "info");
    }

    /**
     * After
     *
     * @since 1.0.0
     */
    @AfterEach
    void after() {
        System.clearProperty(this.A_KEY);
        System.clearProperty(SimpleLogger.CACHE_OUTPUT_STREAM_STRING_KEY);
        System.setErr(this.original);
    }

    /**
     * Empty logger name
     *
     * @since 1.0.0
     */
    @Test
    void emptyLoggerName() {
        SimpleLogger simpleLogger = new SimpleLogger("a");
        assertEquals("info", simpleLogger.recursivelyComputeLevelString());
    }

    /**
     * Off level
     *
     * @since 1.0.0
     */
    @Test
    void offLevel() {
        System.setProperty(this.A_KEY, "off");
        SimpleLogger.init();
        SimpleLogger simpleLogger = new SimpleLogger("a");
        assertEquals("off", simpleLogger.recursivelyComputeLevelString());
        assertFalse(simpleLogger.isErrorEnabled());
    }

    /**
     * Logger name with no dots with level
     *
     * @since 1.0.0
     */
    @Test
    void loggerNameWithNoDots_WithLevel() {
        SimpleLogger.init();
        SimpleLogger simpleLogger = new SimpleLogger("a");

        assertEquals("info", simpleLogger.recursivelyComputeLevelString());
    }

    /**
     * Logger name with one dot should inherit from parent
     *
     * @since 1.0.0
     */
    @Test
    void loggerNameWithOneDotShouldInheritFromParent() {
        SimpleLogger simpleLogger = new SimpleLogger("a.b");
        assertEquals("info", simpleLogger.recursivelyComputeLevelString());
    }

    /**
     * Logger name with no dots with no set level
     *
     * @since 1.0.0
     */
    @Test
    void loggerNameWithNoDots_WithNoSetLevel() {
        SimpleLogger simpleLogger = new SimpleLogger("x");
        assertNull(simpleLogger.recursivelyComputeLevelString());
    }

    /**
     * Logger name with one dot no set level
     *
     * @since 1.0.0
     */
    @Test
    void loggerNameWithOneDot_NoSetLevel() {
        SimpleLogger simpleLogger = new SimpleLogger("x.y");
        assertNull(simpleLogger.recursivelyComputeLevelString());
    }

    /**
     * Check use of last system stream reference
     *
     * @since 1.0.0
     */
    @Test
    void checkUseOfLastSystemStreamReference() {
        SimpleLogger.init();
        SimpleLogger simpleLogger = new SimpleLogger(this.getClass().getName());

        System.setErr(this.replacement);
        simpleLogger.info("hello");
        this.replacement.flush();
        assertTrue(this.bout.toString().contains("INFO org.slf4j.impl.SimpleLoggerTest - hello"));
    }

    /**
     * Check use of cached output stream
     *
     * @since 1.0.0
     */
    @Test
    void checkUseOfCachedOutputStream() {
        System.setErr(this.replacement);
        System.setProperty(SimpleLogger.CACHE_OUTPUT_STREAM_STRING_KEY, "true");
        SimpleLogger.init();
        SimpleLogger simpleLogger = new SimpleLogger(this.getClass().getName());
        // change reference to original before logging
        System.setErr(this.original);

        simpleLogger.info("hello");
        this.replacement.flush();
        assertTrue(this.bout.toString().contains("INFO org.slf4j.impl.SimpleLoggerTest - hello"));
    }
}
