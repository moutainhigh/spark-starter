package info.spark.starter.logsystem;

import java.io.*;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:53
 * @since 1.0.0
 */
public class SilentPrintStream extends PrintStream {

    /** Other */
    private final PrintStream other;

    /**
     * Silent print stream
     *
     * @param ps ps
     * @since 1.0.0
     */
    SilentPrintStream(PrintStream ps) {
        super(ps);
        this.other = ps;
    }

    /**
     * Print *
     *
     * @param s s
     * @since 1.0.0
     */
    @Override
    public void print(String s) {
    }

    /**
     * Println *
     *
     * @param s s
     * @since 1.0.0
     */
    @Override
    public void println(String s) {
    }

    /**
     * Println *
     *
     * @param x x
     * @since 1.0.0
     */
    @Override
    public void println(Object x) {
    }
}
