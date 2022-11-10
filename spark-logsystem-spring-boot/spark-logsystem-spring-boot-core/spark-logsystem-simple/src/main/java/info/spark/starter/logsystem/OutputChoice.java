package info.spark.starter.logsystem;

import org.jetbrains.annotations.Contract;

import java.io.*;

/**
 * This class encapsulates the user's choice of output target.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:50
 * @since 1.0.0
 */
class OutputChoice {

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.08 11:50
     * @since 1.0.0
     */
    enum OutputChoiceType {
        /** Sys out output choice type */
        SYS_OUT,
        /** Cached sys out output choice type */
        CACHED_SYS_OUT,
        /** Sys err output choice type */
        SYS_ERR,
        /** Cached sys err output choice type */
        CACHED_SYS_ERR,
        /** File output choice type */
        FILE
    }

    /** Output choice type */
    private final OutputChoiceType outputChoiceType;
    /** Target print stream */
    private final PrintStream targetPrintStream;

    /**
     * Output choice
     *
     * @param outputChoiceType output choice type
     * @since 1.0.0
     */
    @Contract(pure = true)
    OutputChoice(OutputChoiceType outputChoiceType) {
        if (outputChoiceType == OutputChoiceType.FILE) {
            throw new IllegalArgumentException();
        }
        this.outputChoiceType = outputChoiceType;
        if (outputChoiceType == OutputChoiceType.CACHED_SYS_OUT) {
            this.targetPrintStream = System.out;
        } else if (outputChoiceType == OutputChoiceType.CACHED_SYS_ERR) {
            this.targetPrintStream = System.err;
        } else {
            this.targetPrintStream = null;
        }
    }

    /**
     * Output choice
     *
     * @param printStream print stream
     * @since 1.0.0
     */
    @Contract(pure = true)
    OutputChoice(PrintStream printStream) {
        this.outputChoiceType = OutputChoiceType.FILE;
        this.targetPrintStream = printStream;
    }

    /**
     * Gets target print stream *
     *
     * @return the target print stream
     * @since 1.0.0
     */
    PrintStream getTargetPrintStream() {
        switch (this.outputChoiceType) {
            case SYS_OUT:
                return System.out;
            case SYS_ERR:
                return System.err;
            case CACHED_SYS_ERR:
            case CACHED_SYS_OUT:
            case FILE:
                return this.targetPrintStream;
            default:
                throw new IllegalArgumentException();
        }

    }

}
