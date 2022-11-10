package info.spark.starter.logsystem.helpers;

import info.spark.starter.logsystem.LoggerFactoryFriend;
import info.spark.starter.logsystem.SimpleLogger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.helpers.MultithreadedInitializationTest;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 12:56
 * @since 1.0.0
 */
public class SimpleLoggerMultithreadedInitializationTest extends MultithreadedInitializationTest {
    /** NUM_LINES_IN_SLF4J_REPLAY_WARNING */
    // final static int THREAD_COUNT = 4 + Runtime.getRuntime().availableProcessors() * 2;
    // private final List<Logger> createdLoggers = Collections.synchronizedList(new ArrayList<Logger>());
    // private final AtomicLong eventCount = new AtomicLong(0);
    //
    // private final CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT + 1);
    //
    // final int diff = new Random().nextInt(10000);
    private static final int NUM_LINES_IN_SLF4J_REPLAY_WARNING = 3;
    /** Old err */
    private final PrintStream oldErr = System.err;
    /** Logger name */
    final String loggerName = this.getClass().getName();
    /** Sps */
    private final StringPrintStream sps = new StringPrintStream(this.oldErr, true);

    /**
     * Sets
     */
    @BeforeEach
    public void setup() {
        System.out.println("THREAD_COUNT=" + THREAD_COUNT);
        System.setErr(this.sps);
        System.setProperty(SimpleLogger.LOG_FILE_KEY, "System.err");
        LoggerFactoryFriend.reset();
    }

    /**
     * Tear down *
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @AfterEach
    public void tearDown() throws Exception {
        LoggerFactoryFriend.reset();
        System.clearProperty(SimpleLogger.LOG_FILE_KEY);
        System.setErr(this.oldErr);
    }

    /**
     * Gets recorded event count *
     *
     * @return the recorded event count
     * @since 1.0.0
     */
    @Override
    protected long getRecordedEventCount() {
        return this.sps.stringList.size();
    }

    /**
     * Extra log events int
     *
     * @return the int
     * @since 1.0.0
     */
    @Override
    protected int extraLogEvents() {
        return NUM_LINES_IN_SLF4J_REPLAY_WARNING;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.08 12:56
     * @since 1.0.0
     */
    static class StringPrintStream extends PrintStream {

        /** LINE_SEP */
        public static final String LINE_SEP = System.getProperty("line.separator");
        /** Other */
        PrintStream other;
        /** Duplicate */
        boolean duplicate = false;

        /** String list */
        List<String> stringList = Collections.synchronizedList(new ArrayList<String>());

        /**
         * String print stream
         *
         * @param ps        ps
         * @param duplicate duplicate
         * @since 1.0.0
         */
        StringPrintStream(PrintStream ps, boolean duplicate) {
            super(ps);
            this.other = ps;
            this.duplicate = duplicate;
        }

        /**
         * String print stream
         *
         * @param ps ps
         * @since 1.0.0
         */
        public StringPrintStream(PrintStream ps) {
            this(ps, false);
        }

        /**
         * Print *
         *
         * @param s s
         * @since 1.0.0
         */
        @Override
        public void print(String s) {
            if (this.duplicate) {
                this.other.print(s);
            }
            this.stringList.add(s);
        }

        /**
         * Println *
         *
         * @param s s
         * @since 1.0.0
         */
        @Override
        public void println(String s) {
            if (this.duplicate) {
                this.other.println(s);
            }
            this.stringList.add(s);
        }

        /**
         * Println *
         *
         * @param o o
         * @since 1.0.0
         */
        @Override
        public void println(Object o) {
            if (this.duplicate) {
                this.other.println(o);
            }
            this.stringList.add(o.toString());
        }
    }

}
