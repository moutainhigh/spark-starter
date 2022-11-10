package info.spark.starter.logsystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Tests that detecting logger name mismatches works and doesn't cause problems
 * or trigger if disabled.
 * <p>
 * This test can't live inside slf4j-api because the NOP Logger doesn't
 * remember its name.
 *
 * @author Alexander Dorokhine
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 12:57
 * @since 1.0.0
 */
class DetectLoggerNameMismatchTest {

    /** MISMATCH_STRING */
    private static final String MISMATCH_STRING = "Detected logger name mismatch";

    /** Byte array output stream */
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    /** Old err */
    private final PrintStream oldErr = System.err;

    /**
     * Sets up
     */
    @BeforeEach
    void setUp() {
        System.setErr(new PrintStream(this.byteArrayOutputStream));
    }

    /**
     * Tear down
     *
     * @since 1.0.0
     */
    @AfterEach
    void tearDown() {
        setTrialEnabled(false);
        System.setErr(this.oldErr);
    }

    /**
     * Test no trigger without property
     *
     * @since 1.0.0
     */
    /*
     * Pass in the wrong class to the Logger with the check disabled, and make sure there are no errors.
     */
    @Test
    void testNoTriggerWithoutProperty() {
        setTrialEnabled(false);
        Logger logger = LoggerFactory.getLogger(String.class);
        assertEquals("java.lang.String", logger.getName());
        this.assertMismatchDetected(false);
    }

    /**
     * Test trigger with property
     *
     * @since 1.0.0
     */
    /*
     * Pass in the wrong class to the Logger with the check enabled, and make sure there ARE errors.
     */
    @Test
    void testTriggerWithProperty() {
        setTrialEnabled(true);
        LoggerFactory.getLogger(String.class);
        String s = String.valueOf(this.byteArrayOutputStream);
        this.assertMismatchDetected(true);
    }

    /**
     * Test trigger whole message
     *
     * @since 1.0.0
     */
    /*
     * Checks the whole error message to ensure all the names show up correctly.
     */
    @Test
    void testTriggerWholeMessage() {
        setTrialEnabled(true);
        LoggerFactory.getLogger(String.class);
        boolean success = String.valueOf(this.byteArrayOutputStream).contains(
            "Detected logger name mismatch. Given name: \"java.lang.String\"; " + "computed name: \"org.slf4j.DetectLoggerNameMismatchTest\".");
        assertTrue("Actual value of byteArrayOutputStream: " + String.valueOf(this.byteArrayOutputStream), success);
    }

    /**
     * Test pass if match
     *
     * @since 1.0.0
     */
    /*
     * Checks that there are no errors with the check enabled if the class matches.
     */
    @Test
    void testPassIfMatch() {
        setTrialEnabled(true);
        Logger logger = LoggerFactory.getLogger(DetectLoggerNameMismatchTest.class);
        assertEquals("org.slf4j.DetectLoggerNameMismatchTest", logger.getName());
        this.assertMismatchDetected(false);
    }

    /**
     * Assert mismatch detected *
     *
     * @param mismatchDetected mismatch detected
     * @since 1.0.0
     */
    private void assertMismatchDetected(boolean mismatchDetected) {
        assertEquals(mismatchDetected, String.valueOf(this.byteArrayOutputStream).contains(MISMATCH_STRING));
    }

    /**
     * Verify logger defined in base with overriden get class method
     *
     * @since 1.0.0
     */
    @Test
    void verifyLoggerDefinedInBaseWithOverridenGetClassMethod() {
        setTrialEnabled(true);
        Square square = new Square();
        assertEquals("org.slf4j.Square", square.logger.getName());
        this.assertMismatchDetected(false);
    }

    /**
     * Sets trial enabled *
     *
     * @param enabled enabled
     * @since 1.0.0
     */
    private static void setTrialEnabled(boolean enabled) {
        // The system property is read into a static variable at initialization time
        // so we cannot just reset the system property to test this feature.
        // Therefore we set the variable directly.
        // LoggerFactory.DETECT_LOGGER_NAME_MISMATCH = enabled;
    }
}

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 12:57
 * @since 1.0.0
 */
class ShapeBase {
    /** Logger */
    Logger logger = LoggerFactory.getLogger(this.getClass());
}

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 12:57
 * @since 1.0.0
 */
class Square extends ShapeBase {
}
