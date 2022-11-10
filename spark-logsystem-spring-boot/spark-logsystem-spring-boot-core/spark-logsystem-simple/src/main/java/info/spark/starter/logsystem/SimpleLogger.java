package info.spark.starter.logsystem;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

import java.io.PrintStream;
import java.util.Date;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 02:23
 * @since 1.0.0
 */
@SuppressWarnings("checkstyle:MethodLimit")
public class SimpleLogger extends MarkerIgnoringBase {

    /** serialVersionUID */
    private static final long serialVersionUID = -632788891211436180L;

    /** START_TIME */
    private static final long START_TIME = System.currentTimeMillis();

    /** LOG_LEVEL_TRACE */
    static final int LOG_LEVEL_TRACE = LocationAwareLogger.TRACE_INT;
    /** LOG_LEVEL_DEBUG */
    static final int LOG_LEVEL_DEBUG = LocationAwareLogger.DEBUG_INT;
    /** LOG_LEVEL_INFO */
    static final int LOG_LEVEL_INFO = LocationAwareLogger.INFO_INT;
    /** LOG_LEVEL_WARN */
    static final int LOG_LEVEL_WARN = LocationAwareLogger.WARN_INT;
    /** LOG_LEVEL_ERROR */
    static final int LOG_LEVEL_ERROR = LocationAwareLogger.ERROR_INT;
    /** LOG_LEVEL_OFF */
    static final int LOG_LEVEL_OFF = LOG_LEVEL_ERROR + 10;

    /** TRACE */
    static final String TRACE = "TRACE";
    /** DEBUG */
    static final String DEBUG = "DEBUG";
    /** INFO */
    static final String INFO = "INFO";
    /** WARN */
    static final String WARN = "WARN";
    /** ERROR */
    static final String ERROR = "ERROR";
    /** OFF */
    static final String OFF = "OFF";

    /** initialized */
    private static boolean initialized = false;
    /** Config params */
    private static SimpleLoggerConfiguration configParams = null;

    /**
     * Lazy init
     *
     * @since 1.0.0
     */
    static void lazyInit() {
        if (initialized) {
            return;
        }
        initialized = true;
        init();
    }

    /**
     * Init
     *
     * @since 1.0.0
     */
    public static void init() {
        configParams = new SimpleLoggerConfiguration();
    }

    /** The current log level */
    private int currentLogLevel = LOG_LEVEL_INFO;
    /** The short name of this simple log instance */
    private transient String shortLogName = null;

    /**
     * All system properties used by <code>SimpleLogger</code> start with this
     * prefix
     */
    private static final String SYSTEM_PREFIX = "org.slf4j.simpleLogger.";
    /** BANNER_CLASS */
    private static final String BANNER_CLASS = "info.spark.starter.launcher.banner.SparkBanner";
    /** CONFIGKIT_CLASS */
    private static final String CONFIGKIT_CLASS = "info.spark.starter.common.util.ConfigKit";
    /** SPRINGCONTEXT_CLASS */
    private static final String SPRINGCONTEXT_CLASS = "info.spark.starter.common.context.SpringContext";
    /** LOG_KEY_PREFIX */
    public static final String LOG_KEY_PREFIX = SimpleLogger.SYSTEM_PREFIX + "log.";
    /** CACHE_OUTPUT_STREAM_STRING_KEY */
    public static final String CACHE_OUTPUT_STREAM_STRING_KEY = SimpleLogger.SYSTEM_PREFIX + "cacheOutputStream";
    /** WARN_LEVEL_STRING_KEY */
    static final String WARN_LEVEL_STRING_KEY = SimpleLogger.SYSTEM_PREFIX + "warnLevelString";
    /** LEVEL_IN_BRACKETS_KEY */
    static final String LEVEL_IN_BRACKETS_KEY = SimpleLogger.SYSTEM_PREFIX + "levelInBrackets";
    /** LOG_FILE_KEY */
    public static final String LOG_FILE_KEY = SimpleLogger.SYSTEM_PREFIX + "logFile";
    /** SHOW_SHORT_LOG_NAME_KEY */
    static final String SHOW_SHORT_LOG_NAME_KEY = SimpleLogger.SYSTEM_PREFIX + "showShortLogName";
    /** SHOW_LOG_NAME_KEY */
    static final String SHOW_LOG_NAME_KEY = SimpleLogger.SYSTEM_PREFIX + "showLogName";
    /** SHOW_THREAD_NAME_KEY */
    static final String SHOW_THREAD_NAME_KEY = SimpleLogger.SYSTEM_PREFIX + "showThreadName";
    /** DATE_TIME_FORMAT_KEY */
    static final String DATE_TIME_FORMAT_KEY = SimpleLogger.SYSTEM_PREFIX + "dateTimeFormat";
    /** SHOW_DATE_TIME_KEY */
    static final String SHOW_DATE_TIME_KEY = SimpleLogger.SYSTEM_PREFIX + "showDateTime";
    /** DEFAULT_LOG_LEVEL_KEY */
    static final String DEFAULT_LOG_LEVEL_KEY = SimpleLogger.SYSTEM_PREFIX + "defaultLogLevel";

    /**
     * Package access allows only {@link SimpleLoggerFactory} to instantiate
     * SimpleLogger instances.
     *
     * @param name name
     * @since 1.0.0
     */
    public SimpleLogger(String name) {
        this.name = name;

        String levelString = this.recursivelyComputeLevelString();
        if (levelString != null) {
            this.currentLogLevel = SimpleLoggerConfiguration.stringToLevel(levelString);
        } else {
            this.currentLogLevel = configParams.defaultLogLevel;
        }
    }

    /**
     * Recursively compute level string string
     *
     * @return the string
     * @since 1.0.0
     */
    public String recursivelyComputeLevelString() {
        String tempName = this.name;
        String levelString = null;
        int indexOfLastDot = tempName.length();
        while ((levelString == null) && (indexOfLastDot > -1)) {
            tempName = tempName.substring(0, indexOfLastDot);
            levelString = configParams.getStringProperty(SimpleLogger.LOG_KEY_PREFIX + tempName, null);
            indexOfLastDot = tempName.lastIndexOf(".");
        }
        return levelString;
    }

    /**
     * 通过配置将日志进行简单的拼接
     *
     * @param level   One of the LOG_LEVEL_XXX constants defining the log level
     * @param message The message itself
     * @param t       The exception whose stack trace should be logged
     * @since 1.0.0
     */
    private void log(int level, String message, Throwable t) {
        if (!this.isLevelEnabled(level)) {
            return;
        }

        StringBuilder buf = new StringBuilder(32);

        // Append date-time if so configured
        if (configParams.showDateTime) {
            if (configParams.dateFormatter != null) {
                buf.append(this.getFormattedDate());
            } else {
                buf.append(System.currentTimeMillis() - START_TIME);
            }
            buf.append(' ');
        }

        // Append current thread name if so configured
        if (configParams.showThreadName) {
            buf.append('[');
            buf.append(Thread.currentThread().getName());
            buf.append("] ");
        }

        if (configParams.levelInBrackets) {
            buf.append('[');
        }

        // Append a readable representation of the log level
        String levelStr = this.renderLevel(level);
        buf.append(levelStr);
        if (configParams.levelInBrackets) {
            buf.append(']');
        }
        buf.append(' ');

        // Append the name of the log instance if so configured
        if (configParams.showShortLogName) {
            if (this.shortLogName == null) {
                this.shortLogName = this.computeShortName();
            }
            buf.append(this.shortLogName).append(" - ");
        } else if (configParams.showLogName) {
            buf.append(this.name).append(" - ");
        }

        // Append the message
        buf.append(message);

        if (BANNER_CLASS.equals(this.name)
            || CONFIGKIT_CLASS.equals(this.name)
            || SPRINGCONTEXT_CLASS.equals(this.name)) {

            buf.delete(0, buf.length());
            buf.append(message);
        }

        this.write(buf, t);

    }

    /**
     * Render level string
     *
     * @param level level
     * @return the string
     * @since 1.0.0
     */
    @Contract(pure = true)
    private String renderLevel(int level) {
        switch (level) {
            case LOG_LEVEL_TRACE:
                return TRACE;
            case LOG_LEVEL_DEBUG:
                return (DEBUG);
            case LOG_LEVEL_INFO:
                return INFO;
            case LOG_LEVEL_WARN:
                return configParams.warnLevelString;
            case LOG_LEVEL_ERROR:
                return ERROR;
            default:
        }
        throw new IllegalStateException("Unrecognized level [" + level + "]");
    }

    /**
     * Write *
     *
     * @param buf buf
     * @param t   t
     * @since 1.0.0
     */
    private void write(@NotNull StringBuilder buf, Throwable t) {
        PrintStream targetStream = configParams.outputChoice.getTargetPrintStream();

        targetStream.println(buf);
        this.writeThrowable(t, targetStream);
        targetStream.flush();
    }

    /**
     * Write throwable *
     *
     * @param t            t
     * @param targetStream target stream
     * @since 1.0.0
     */
    @SuppressWarnings("all")
    private void writeThrowable(Throwable t, PrintStream targetStream) {
        if (t != null) {
            t.printStackTrace(targetStream);
        }
    }

    /**
     * Gets formatted date *
     *
     * @return the formatted date
     * @since 1.0.0
     */
    private String getFormattedDate() {
        Date now = new Date();
        String dateText;
        synchronized (configParams.dateFormatter) {
            dateText = configParams.dateFormatter.format(now);
        }
        return dateText;
    }

    /**
     * Compute short name string
     *
     * @return the string
     * @since 1.0.0
     */
    @NotNull
    private String computeShortName() {
        return this.name.substring(this.name.lastIndexOf(".") + 1);
    }

    /**
     * For formatted messages, first substitute arguments and then log.
     *
     * @param level  level
     * @param format format
     * @param arg1   arg 1
     * @param arg2   arg 2
     * @since 1.0.0
     */
    private void formatAndLog(int level, String format, Object arg1, Object arg2) {
        if (!this.isLevelEnabled(level)) {
            return;
        }
        FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
        this.log(level, tp.getMessage(), tp.getThrowable());
    }

    /**
     * For formatted messages, first substitute arguments and then log.
     *
     * @param level     level
     * @param format    format
     * @param arguments a list of 3 ore more arguments
     * @since 1.0.0
     */
    private void formatAndLog(int level, String format, Object... arguments) {
        if (!this.isLevelEnabled(level)) {
            return;
        }
        FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
        this.log(level, tp.getMessage(), tp.getThrowable());
    }

    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     * @return the boolean
     * @since 1.0.0
     */
    @Contract(pure = true)
    private boolean isLevelEnabled(int logLevel) {
        // log level are numerically ordered so can use simple numeric
        // comparison
        return (logLevel >= this.currentLogLevel);
    }

    /**
     * Are {@code trace} messages currently enabled?
     *
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean isTraceEnabled() {
        return this.isLevelEnabled(LOG_LEVEL_TRACE);
    }

    /**
     * A simple implementation which logs messages of level TRACE according to
     * the format outlined above.
     *
     * @param msg msg
     * @since 1.0.0
     */
    @Override
    public void trace(String msg) {
        this.log(LOG_LEVEL_TRACE, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * TRACE according to the format outlined above.
     *
     * @param format format
     * @param param1 param 1
     * @since 1.0.0
     */
    @Override
    public void trace(String format, Object param1) {
        this.formatAndLog(LOG_LEVEL_TRACE, format, param1, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * TRACE according to the format outlined above.
     *
     * @param format format
     * @param param1 param 1
     * @param param2 param 2
     * @since 1.0.0
     */
    @Override
    public void trace(String format, Object param1, Object param2) {
        this.formatAndLog(LOG_LEVEL_TRACE, format, param1, param2);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * TRACE according to the format outlined above.
     *
     * @param format   format
     * @param argArray arg array
     * @since 1.0.0
     */
    @Override
    public void trace(String format, Object... argArray) {
        this.formatAndLog(LOG_LEVEL_TRACE, format, argArray);
    }

    /**
     * Log a message of level TRACE, including an exception.  @param msg msg
     *
     * @param msg msg
     * @param t   t
     * @since 1.0.0
     */
    @Override
    public void trace(String msg, Throwable t) {
        this.log(LOG_LEVEL_TRACE, msg, t);
    }

    /**
     * Are {@code debug} messages currently enabled?
     *
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean isDebugEnabled() {
        return this.isLevelEnabled(LOG_LEVEL_DEBUG);
    }

    /**
     * A simple implementation which logs messages of level DEBUG according to
     * the format outlined above.
     *
     * @param msg msg
     * @since 1.0.0
     */
    @Override
    public void debug(String msg) {
        this.log(LOG_LEVEL_DEBUG, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * DEBUG according to the format outlined above.
     *
     * @param format format
     * @param param1 param 1
     * @since 1.0.0
     */
    @Override
    public void debug(String format, Object param1) {
        this.formatAndLog(LOG_LEVEL_DEBUG, format, param1, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * DEBUG according to the format outlined above.
     *
     * @param format format
     * @param param1 param 1
     * @param param2 param 2
     * @since 1.0.0
     */
    @Override
    public void debug(String format, Object param1, Object param2) {
        this.formatAndLog(LOG_LEVEL_DEBUG, format, param1, param2);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * DEBUG according to the format outlined above.
     *
     * @param format   format
     * @param argArray arg array
     * @since 1.0.0
     */
    @Override
    public void debug(String format, Object... argArray) {
        this.formatAndLog(LOG_LEVEL_DEBUG, format, argArray);
    }

    /**
     * Log a message of level DEBUG, including an exception.  @param msg msg
     *
     * @param msg msg
     * @param t   t
     * @since 1.0.0
     */
    @Override
    public void debug(String msg, Throwable t) {
        this.log(LOG_LEVEL_DEBUG, msg, t);
    }

    /**
     * Are {@code info} messages currently enabled?
     *
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean isInfoEnabled() {
        return this.isLevelEnabled(LOG_LEVEL_INFO);
    }

    /**
     * A simple implementation which logs messages of level INFO according to
     * the format outlined above.
     *
     * @param msg msg
     * @since 1.0.0
     */
    @Override
    public void info(String msg) {
        this.log(LOG_LEVEL_INFO, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * INFO according to the format outlined above.
     *
     * @param format format
     * @param arg    arg
     * @since 1.0.0
     */
    @Override
    public void info(String format, Object arg) {
        this.formatAndLog(LOG_LEVEL_INFO, format, arg, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * INFO according to the format outlined above.
     *
     * @param format format
     * @param arg1   arg 1
     * @param arg2   arg 2
     * @since 1.0.0
     */
    @Override
    public void info(String format, Object arg1, Object arg2) {
        this.formatAndLog(LOG_LEVEL_INFO, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * INFO according to the format outlined above.
     *
     * @param format   format
     * @param argArray arg array
     * @since 1.0.0
     */
    @Override
    public void info(String format, Object... argArray) {
        this.formatAndLog(LOG_LEVEL_INFO, format, argArray);
    }

    /**
     * Log a message of level INFO, including an exception.  @param msg msg
     *
     * @param msg msg
     * @param t   t
     * @since 1.0.0
     */
    @Override
    public void info(String msg, Throwable t) {
        this.log(LOG_LEVEL_INFO, msg, t);
    }

    /**
     * Are {@code warn} messages currently enabled?
     *
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean isWarnEnabled() {
        return this.isLevelEnabled(LOG_LEVEL_WARN);
    }

    /**
     * A simple implementation which always logs messages of level WARN
     * according to the format outlined above.
     *
     * @param msg msg
     * @since 1.0.0
     */
    @Override
    public void warn(String msg) {
        this.log(LOG_LEVEL_WARN, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * WARN according to the format outlined above.
     *
     * @param format format
     * @param arg    arg
     * @since 1.0.0
     */
    @Override
    public void warn(String format, Object arg) {
        this.formatAndLog(LOG_LEVEL_WARN, format, arg, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * WARN according to the format outlined above.
     *
     * @param format format
     * @param arg1   arg 1
     * @param arg2   arg 2
     * @since 1.0.0
     */
    @Override
    public void warn(String format, Object arg1, Object arg2) {
        this.formatAndLog(LOG_LEVEL_WARN, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * WARN according to the format outlined above.
     *
     * @param format   format
     * @param argArray arg array
     * @since 1.0.0
     */
    @Override
    public void warn(String format, Object... argArray) {
        this.formatAndLog(LOG_LEVEL_WARN, format, argArray);
    }

    /**
     * Log a message of level WARN, including an exception.  @param msg msg
     *
     * @param msg msg
     * @param t   t
     * @since 1.0.0
     */
    @Override
    public void warn(String msg, Throwable t) {
        this.log(LOG_LEVEL_WARN, msg, t);
    }

    /**
     * Are {@code error} messages currently enabled?
     *
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean isErrorEnabled() {
        return this.isLevelEnabled(LOG_LEVEL_ERROR);
    }

    /**
     * A simple implementation which always logs messages of level ERROR
     * according to the format outlined above.
     *
     * @param msg msg
     * @since 1.0.0
     */
    @Override
    public void error(String msg) {
        this.log(LOG_LEVEL_ERROR, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * ERROR according to the format outlined above.
     *
     * @param format format
     * @param arg    arg
     * @since 1.0.0
     */
    @Override
    public void error(String format, Object arg) {
        this.formatAndLog(LOG_LEVEL_ERROR, format, arg, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * ERROR according to the format outlined above.
     *
     * @param format format
     * @param arg1   arg 1
     * @param arg2   arg 2
     * @since 1.0.0
     */
    @Override
    public void error(String format, Object arg1, Object arg2) {
        this.formatAndLog(LOG_LEVEL_ERROR, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * ERROR according to the format outlined above.
     *
     * @param format   format
     * @param argArray arg array
     * @since 1.0.0
     */
    @Override
    public void error(String format, Object... argArray) {
        this.formatAndLog(LOG_LEVEL_ERROR, format, argArray);
    }

    /**
     * Log a message of level ERROR, including an exception.  @param msg msg
     *
     * @param msg msg
     * @param t   t
     * @since 1.0.0
     */
    @Override
    public void error(String msg, Throwable t) {
        this.log(LOG_LEVEL_ERROR, msg, t);
    }

    /**
     * Log *
     *
     * @param event event
     * @since 1.0.0
     */
    public void log(@NotNull LoggingEvent event) {
        int levelInt = event.getLevel().toInt();

        if (!this.isLevelEnabled(levelInt)) {
            return;
        }
        FormattingTuple tp = MessageFormatter.arrayFormat(event.getMessage(), event.getArgumentArray(), event.getThrowable());
        this.log(levelInt, tp.getMessage(), event.getThrowable());
    }

}
