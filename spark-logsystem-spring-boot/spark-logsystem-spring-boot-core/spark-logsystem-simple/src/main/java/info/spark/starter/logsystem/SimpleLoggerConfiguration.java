package info.spark.starter.logsystem;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.helpers.Util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * This class holds configuration values for {@link SimpleLogger}. The
 * values are computed at runtime. See {@link SimpleLogger} documentation for
 * more information.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:48
 * @since 1.7.25
 */
@SuppressWarnings("all")
class SimpleLoggerConfiguration {

    /** CONFIGURATION_FILE */
    private static final String CONFIGURATION_FILE = "simplelogger.properties";

    /** Default log level default */
    private static final int DEFAULT_LOG_LEVEL_DEFAULT = SimpleLogger.LOG_LEVEL_INFO;
    /** Default log level */
    int defaultLogLevel = DEFAULT_LOG_LEVEL_DEFAULT;

    /** SHOW_DATE_TIME_DEFAULT */
    private static final boolean SHOW_DATE_TIME_DEFAULT = false;
    /** Show date time */
    boolean showDateTime = SHOW_DATE_TIME_DEFAULT;

    /** DATE_TIME_FORMAT_STR_DEFAULT */
    private static final String DATE_TIME_FORMAT_STR_DEFAULT = "yyyy-MM-dd HH:mm:ss:SSS";

    /** Date formatter */
    final DateFormat dateFormatter;

    /** SHOW_THREAD_NAME_DEFAULT */
    private static final boolean SHOW_THREAD_NAME_DEFAULT = true;
    /** Show thread name */
    boolean showThreadName = SHOW_THREAD_NAME_DEFAULT;

    /** SHOW_LOG_NAME_DEFAULT */
    private final static boolean SHOW_LOG_NAME_DEFAULT = true;
    /** Show log name */
    boolean showLogName = SHOW_LOG_NAME_DEFAULT;

    /** SHOW_SHORT_LOG_NAME_DEFAULT */
    private static final boolean SHOW_SHORT_LOG_NAME_DEFAULT = false;
    /** Show short log name */
    boolean showShortLogName = SHOW_SHORT_LOG_NAME_DEFAULT;

    /** LEVEL_IN_BRACKETS_DEFAULT */
    private static final boolean LEVEL_IN_BRACKETS_DEFAULT = false;
    /** Level in brackets */
    boolean levelInBrackets = LEVEL_IN_BRACKETS_DEFAULT;

    /** LOG_FILE_DEFAULT */
    private static final String LOG_FILE_DEFAULT = "System.err";
    /** Log file */
    private String logFile = LOG_FILE_DEFAULT;
    /** Output choice */
    OutputChoice outputChoice = null;

    /** CACHE_OUTPUT_STREAM_DEFAULT */
    private static final boolean CACHE_OUTPUT_STREAM_DEFAULT = false;

    /** WARN_LEVELS_STRING_DEFAULT */
    private static final String WARN_LEVELS_STRING_DEFAULT = SimpleLogger.WARN;
    /** Warn level string */
    String warnLevelString = WARN_LEVELS_STRING_DEFAULT;

    /** Properties */
    private final Properties properties = new Properties();

    /**
     * SimpleLoggerConfiguration
     *
     * @since 1.0.0
     */
    SimpleLoggerConfiguration() {
        this.loadProperties();

        String defaultLogLevelString = this.getStringProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, null);
        if (defaultLogLevelString != null) {
            this.defaultLogLevel = stringToLevel(defaultLogLevelString);
        }

        this.showLogName = this.getBooleanProperty(SimpleLogger.SHOW_LOG_NAME_KEY, SimpleLoggerConfiguration.SHOW_LOG_NAME_DEFAULT);
        this.showShortLogName = this.getBooleanProperty(SimpleLogger.SHOW_SHORT_LOG_NAME_KEY, SHOW_SHORT_LOG_NAME_DEFAULT);
        this.showDateTime = this.getBooleanProperty(SimpleLogger.SHOW_DATE_TIME_KEY, SHOW_DATE_TIME_DEFAULT);
        this.showThreadName = this.getBooleanProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, SHOW_THREAD_NAME_DEFAULT);
        // dateTimeFormatStr
        String dateTimeFormatStr = this.getStringProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, DATE_TIME_FORMAT_STR_DEFAULT);
        this.dateFormatter = new SimpleDateFormat(dateTimeFormatStr);
        this.levelInBrackets = this.getBooleanProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, LEVEL_IN_BRACKETS_DEFAULT);
        this.warnLevelString = this.getStringProperty(SimpleLogger.WARN_LEVEL_STRING_KEY, WARN_LEVELS_STRING_DEFAULT);

        this.logFile = this.getStringProperty(SimpleLogger.LOG_FILE_KEY, this.logFile);

        // Cache output stream
        boolean cacheOutputStream = this.getBooleanProperty(SimpleLogger.CACHE_OUTPUT_STREAM_STRING_KEY, CACHE_OUTPUT_STREAM_DEFAULT);
        this.outputChoice = computeOutputChoice(this.logFile, cacheOutputStream);

    }

    /**
     * Load properties
     *
     * @since 1.0.0
     */
    private void loadProperties() {
        // 如果自定义 simplelogger.properties, 则优先加载
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(CONFIGURATION_FILE);
        if (inputStream == null) {
            URL url = this.getClass().getClassLoader().getResource(CONFIGURATION_FILE);
            if (url != null) {
                try {
                    inputStream = url.openStream();
                } catch (IOException ignored) {
                }
            }
        }

        if (null != inputStream) {
            try {
                this.properties.load(inputStream);
            } catch (java.io.IOException e) {
                // ignored
            } finally {
                try {
                    inputStream.close();
                } catch (java.io.IOException ignored) {
                }
            }
        }
    }

    /**
     * Gets string property *
     *
     * @param name         name
     * @param defaultValue default value
     * @return the string property
     * @since 1.0.0
     */
    String getStringProperty(String name, String defaultValue) {
        String prop = this.getStringProperty(name);
        return (prop == null) ? defaultValue : prop;
    }

    /**
     * Gets boolean property *
     *
     * @param name         name
     * @param defaultValue default value
     * @return the boolean property
     * @since 1.0.0
     */
    private boolean getBooleanProperty(String name, boolean defaultValue) {
        String prop = this.getStringProperty(name);
        return (prop == null) ? defaultValue : "true".equalsIgnoreCase(prop);
    }

    /**
     * Gets string property *
     *
     * @param name name
     * @return the string property
     * @since 1.0.0
     */
    private String getStringProperty(String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (SecurityException e) {
            // Ignore
        }
        return (prop == null) ? this.properties.getProperty(name) : prop;
    }

    /**
     * String to level int
     *
     * @param levelStr level str
     * @return the int
     * @since 1.0.0
     */
    static int stringToLevel(String levelStr) {
        if (SimpleLogger.TRACE.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_TRACE;
        } else if (SimpleLogger.DEBUG.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_DEBUG;
        } else if (SimpleLogger.INFO.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_INFO;
        } else if (SimpleLogger.WARN.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_WARN;
        } else if (SimpleLogger.ERROR.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_ERROR;
        } else if (SimpleLogger.OFF.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_OFF;
        }
        // assume INFO by default
        return SimpleLogger.LOG_LEVEL_INFO;
    }

    /**
     * Compute output choice output choice
     *
     * @param logFile           log file
     * @param cacheOutputStream cache output stream
     * @return the output choice
     * @since 1.0.0
     */
    @NotNull
    @Contract("_, _ -> new")
    private static OutputChoice computeOutputChoice(String logFile, boolean cacheOutputStream) {
        if ("System.err".equalsIgnoreCase(logFile)) {
            if (cacheOutputStream) {
                return new OutputChoice(OutputChoice.OutputChoiceType.CACHED_SYS_ERR);
            } else {
                return new OutputChoice(OutputChoice.OutputChoiceType.SYS_ERR);
            }
        } else if ("System.out".equalsIgnoreCase(logFile)) {
            if (cacheOutputStream) {
                return new OutputChoice(OutputChoice.OutputChoiceType.CACHED_SYS_OUT);
            } else {
                return new OutputChoice(OutputChoice.OutputChoiceType.SYS_OUT);
            }
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(logFile);
                PrintStream printStream = new PrintStream(fos);
                return new OutputChoice(printStream);
            } catch (FileNotFoundException e) {
                Util.report("Could not open [" + logFile + "]. Defaulting to System.err", e);
                return new OutputChoice(OutputChoice.OutputChoiceType.SYS_ERR);
            }
        }
    }

}
