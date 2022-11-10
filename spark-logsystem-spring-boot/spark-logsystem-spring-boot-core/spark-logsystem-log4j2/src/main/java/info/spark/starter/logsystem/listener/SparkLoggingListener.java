package info.spark.starter.logsystem.listener;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.common.SparkApplicationListener;
import info.spark.starter.common.enums.SparkEnv;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.common.util.GsonUtils;
import info.spark.starter.logsystem.Constants;
import info.spark.starter.logsystem.handler.LogFileProcessor;
import info.spark.starter.util.ReflectionUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.logsystem.handler.AdditionalProcessor;
import info.spark.starter.logsystem.handler.PatternProcessor;
import info.spark.starter.processor.annotation.AutoListener;

import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggerGroup;
import org.springframework.boot.logging.LoggerGroups;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.log.LogMessage;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.boot.context.logging.LoggingApplicationListener.CONFIG_PROPERTY;

/**
 * <p>Description: 在日志系统加载之前, 将自定义配置注入到日志配置文件中, 完成日志自定义配置 </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.25 20:59
 * @since 1.0.0
 */
@Slf4j
@AutoListener
public class SparkLoggingListener implements SparkApplicationListener {
    /** 当前应用的日志系统 */
    @Getter
    private LoggingSystem loggingSystem;
    /** 应用环境, 配合是否为本地开发来进行日志配置 */
    private SparkEnv sparkEnv;
    /** 日志配置文件对象, 用于配置日志文件名和日志输出路径 */
    private LogFileProcessor logFileProcessor;
    /** 配置日志等级的分组对象 */
    @Getter
    public static LoggerGroups loggerGroups;
    /** 启动时是否输出调试信息 */
    private LogLevel sparkLogging;
    /** 日志等级配置 */
    private static final ConfigurationPropertyName LOGGING_LEVEL = ConfigurationPropertyName.of(ConfigKey.LogSystemConfigKey.LOG_LEVEL);
    /** 日志等级分组配置 */
    private static final ConfigurationPropertyName LOGGING_GROUP = ConfigurationPropertyName.of(ConfigKey.LogSystemConfigKey.LOG_GROUP);
    /** 用于绑定日志等级配置的 map */
    private static final Bindable<Map<String, LogLevel>> STRING_LOGLEVEL_MAP = Bindable.mapOf(String.class, LogLevel.class);
    /** 用于绑定日志等级分组配置的 map */
    private static final Bindable<Map<String, List<String>>> STRING_GROUP_MAP = Bindable
        .of(ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class).asMap());
    /** 系统默认的日志等级分组配置 */
    private static final Map<String, List<String>> DEFAULT_GROUP_LOGGERS;

    static {
        MultiValueMap<String, String> loggers = new LinkedMultiValueMap<>();
        loggers.add("project", "info.spark");
        loggers.add("starter", "info.spark.starter");
        loggers.add("captcha", "info.spark.starter.captcha");
        loggers.add("cache", "info.spark.starter.cache");
        loggers.add("dubbo", "info.spark.starter.cache");
        loggers.add("endpoint", "info.spark.starter.endpoint");
        loggers.add("feign", "info.spark.starter.feign");
        loggers.add("agent", "info.spark.agent");
        loggers.add("mybatis", "info.spark.starter.mybatis");
        loggers.add("rest", "info.spark.starter.rest");
        loggers.add("security", "info.spark.starter.security");
        loggers.add("test", "info.spark.starter.test");
        loggers.add("mongo", "info.spark.starter.mongo");
        DEFAULT_GROUP_LOGGERS = Collections.unmodifiableMap(loggers);
    }

    /** 系统默认的日志等级分组配置 */
    private static final Map<LogLevel, List<String>> STARTER_LOGGING_LOGGERS;

    static {
        MultiValueMap<LogLevel, String> loggers = new LinkedMultiValueMap<>();
        loggers.add(LogLevel.DEBUG, "project");
        loggers.add(LogLevel.DEBUG, "starter");
        loggers.add(LogLevel.DEBUG, "captcha");
        loggers.add(LogLevel.DEBUG, "cache");
        loggers.add(LogLevel.DEBUG, "dubbo");
        loggers.add(LogLevel.DEBUG, "endpoint");
        loggers.add(LogLevel.DEBUG, "feign");
        loggers.add(LogLevel.DEBUG, "agent");
        loggers.add(LogLevel.DEBUG, "mybatis");
        loggers.add(LogLevel.DEBUG, "rest");
        loggers.add(LogLevel.DEBUG, "security");
        loggers.add(LogLevel.DEBUG, "test");
        loggers.add(LogLevel.DEBUG, "mongo");
        STARTER_LOGGING_LOGGERS = Collections.unmodifiableMap(loggers);
    }

    /**
     * 定义 listener 的执行顺序, 必须保证在 {@link LoggingApplicationListener} 之前执行
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 19;
    }

    /**
     * 第一次初始化日志系统, 只能读取本地配置, 用于本地开发时设置日志文件保存路径等需求.
     *
     * @param event event
     * @since 1.5.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        ClassLoader classLoader = event.getSpringApplication().getClassLoader();

        SparkApplicationListener.Runner.executeAtLast(this.key(event, this.getClass()), () -> this.initLogSystem(environment, classLoader));
    }

    /**
     * 第二次初始化日志系统, 由于日志系统在 Nacos 配置加载之前初始化, 导致 Nacos 中的日志配置无效, 因此需要二次初始化日志系统,
     * 但是对日志路径配置不生效, 因为在第一次初始化已加载.
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    public void onApplicationContextInitializedEvent(@NotNull ApplicationContextInitializedEvent event) {
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
        ClassLoader classLoader = event.getSpringApplication().getClassLoader();

        SparkApplicationListener.Runner.executeAtLast(this.key(event, this.getClass()), () -> {
            // 1. 初始化 log file 相关配置
            this.initLoggingFileConfig(environment);
            this.initLogSystem(environment, classLoader);
        });

        this.printLogConfigInfo();
    }

    /**
     * Init log system
     *
     * @param environment environment
     * @param classLoader class loader
     * @since 1.5.0
     */
    private void initLogSystem(ConfigurableEnvironment environment, ClassLoader classLoader) {
        if (this.loggingSystem == null) {
            this.loggingSystem = LoggingSystem.get(classLoader);
        }

        String profile = ConfigKit.getProfile(environment);
        this.sparkEnv = SparkEnv.of(profile);

        this.initialize(environment);
    }

    /**
     * 初始化日志系统, 加载自定义配置
     *
     * @param environment environment
     * @since 1.0.0
     */
    private void initialize(ConfigurableEnvironment environment) {
        // 1. 初始化 log file 相关配置
        this.initLoggingFileConfig(environment);
        // 2. 初始化 log pattern 相关配置
        this.initLogsystemConfig(environment);
        // 3. 初始化主配置
        this.initLogsystem(environment);

    }

    /**
     * Init logging file properties
     *
     * @param environment environment
     * @since 1.4.0
     */
    private void initLoggingFileConfig(ConfigurableEnvironment environment) {
        this.logFileProcessor = new LogFileProcessor(environment);
    }

    /**
     * Init patter properties
     *
     * @param environment environment
     * @since 1.4.0
     */
    private void initLogsystemConfig(ConfigurableEnvironment environment) {
        this.logFileProcessor.apply();
        new PatternProcessor(environment).apply();
        new AdditionalProcessor(environment).apply();
    }

    /**
     * Init primary properties
     *
     * @param environment environment
     * @since 1.4.0
     */
    private void initLogsystem(ConfigurableEnvironment environment) {
        loggerGroups = new LoggerGroups(DEFAULT_GROUP_LOGGERS);
        this.initializeEarlyLoggingLevel(environment);
        this.initializeSystem(environment);
        this.initializeFinalLoggingLevels(environment, this.loggingSystem);
    }

    /**
     * 加载日志系统前配置调试日志
     *
     * @param environment environment
     * @since 1.0.0
     */
    private void initializeEarlyLoggingLevel(ConfigurableEnvironment environment) {
        if (this.sparkLogging == null) {
            if (this.isSet(environment, LogLevel.DEBUG.name().toLowerCase())) {
                this.sparkLogging = LogLevel.DEBUG;
            }
            if (this.isSet(environment, LogLevel.TRACE.name().toLowerCase())) {
                this.sparkLogging = LogLevel.TRACE;
            }
        }
    }

    /**
     * 若配置了 -Ddebug=true, -Dtrace=true 参数, 则会在启动时输出 {@link SparkLoggingListener#STARTER_LOGGING_LOGGERS} 中对应等级的日志
     *
     * @param environment environment
     * @param property    property
     * @return the boolean
     * @since 1.0.0
     */
    private boolean isSet(@NotNull ConfigurableEnvironment environment, String property) {
        String value = environment.getProperty(property);
        return (value != null && !value.equalsIgnoreCase(ConfigDefaultValue.FALSE_STRING));
    }

    /**
     * 初始化日志系统, 通过 spark.logging.config 指定日志系统加载的日志配置文件
     *
     * @param environment environment
     * @since 1.0.0
     */
    private void initializeSystem(ConfigurableEnvironment environment) {
        LoggingInitializationContext initializationContext = new LoggingInitializationContext(environment);
        String logConfig = environment.getProperty(CONFIG_PROPERTY);
        try {
            ResourceUtils.getURL(Objects.requireNonNull(logConfig)).openStream().close();
            this.loggingSystem.initialize(initializationContext, logConfig, this.buildLogFile(this.logFileProcessor));
        } catch (Exception ex) {
            // NOTE: We can't use the logger here to report the problem
            System.err.println("==========================================================================================");
            System.err.println("Logging system failed to initialize using configuration from '" + logConfig + "'");
            System.err.println("==========================================================================================");
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 反射创建一个 {@link LogFile} 实例, 用于日志系统的初始化
     *
     * @param logFileProcessor spark log file
     * @return the log file
     * @since 1.0.0
     */
    @NotNull
    @SneakyThrows
    private LogFile buildLogFile(@NotNull LogFileProcessor logFileProcessor) {
        Class<?> logFileClass = Class.forName("org.springframework.boot.logging.LogFile");

        Constructor<?> constructor = ReflectionUtils.accessibleConstructor(logFileClass, String.class, String.class);
        return (LogFile) constructor.newInstance(logFileProcessor.getName(), logFileProcessor.getPath());
    }

    /**
     * 设置日志等级
     *
     * @param environment environment
     * @param system      system
     * @since 1.0.0
     */
    private void initializeFinalLoggingLevels(ConfigurableEnvironment environment, LoggingSystem system) {
        this.bindLoggerGroups(environment);
        if (this.sparkLogging != null) {
            this.initializeSparkLogging(system, this.sparkLogging);
        }
        this.setLogLevels(system, environment);
    }

    /**
     * 将自定义 group 和默认的 group 合并
     *
     * @param environment environment
     * @since 1.0.0
     */
    private void bindLoggerGroups(ConfigurableEnvironment environment) {
        if (loggerGroups != null) {
            Binder binder = Binder.get(environment);
            binder.bind(LOGGING_GROUP, STRING_GROUP_MAP).ifBound(loggerGroups::putAll);
        }
    }

    /**
     * 设置日志等级为 sparkLogging 的所有内置日志配置
     *
     * @param system     system
     * @param sparkLogging spark logging
     * @since 1.0.0
     */
    private void initializeSparkLogging(LoggingSystem system, LogLevel sparkLogging) {
        BiConsumer<String, LogLevel> configurer = this.getLogLevelConfigurer(system);
        STARTER_LOGGING_LOGGERS.getOrDefault(sparkLogging, Collections.emptyList())
            .forEach((name) -> this.configureLogLevel(name, sparkLogging, configurer));
    }

    /**
     * 将自定义日志等级和内置的日志等级配置设置到日志系统
     *
     * @param system      system
     * @param environment environment
     * @since 1.0.0
     */
    private void setLogLevels(LoggingSystem system, ConfigurableEnvironment environment) {
        BiConsumer<String, LogLevel> customizer = this.getLogLevelConfigurer(system);
        Binder binder = Binder.get(environment);
        Map<String, LogLevel> levels = binder.bind(LOGGING_LEVEL, STRING_LOGLEVEL_MAP).orElseGet(Collections::emptyMap);
        levels.forEach((name, level) -> this.configureLogLevel(name, level, customizer));
    }

    /**
     * 设置 group 对应的 name 的日志等级
     *
     * @param name       name
     * @param level      level
     * @param configurer configurer
     * @since 1.0.0
     */
    private void configureLogLevel(String name, LogLevel level, BiConsumer<String, LogLevel> configurer) {
        if (loggerGroups != null) {
            LoggerGroup group = loggerGroups.get(name);
            if (group != null && group.hasMembers()) {
                group.configureLogLevel(level, configurer);
                return;
            }
        }
        configurer.accept(name, level);
    }

    /**
     * 将 name 对应的日志等级设置到日志系统中
     *
     * @param system system
     * @return the log level configurer
     * @since 1.0.0
     */
    @NotNull
    @Contract(pure = true)
    private BiConsumer<String, LogLevel> getLogLevelConfigurer(LoggingSystem system) {
        return (name, level) -> {
            try {
                name = name.equalsIgnoreCase(LoggingSystem.ROOT_LOGGER_NAME) ? null : name;
                system.setLogLevel(name, level);
            } catch (RuntimeException ex) {
                LogFactory.getLog(this.getClass()).error(LogMessage.format("Cannot set level '%s' for '%s'",
                                                                           level,
                                                                           StringUtils.isBlank(name)
                                                                           ? LoggingSystem.ROOT_LOGGER_NAME
                                                                           : name));
            }
        };
    }

    /**
     * Print log config info *
     *
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:Regexp")
    private void printLogConfigInfo() {

        if (ConfigKit.isDebugModel()) {

            if (this.loggingSystem != null) {
                List<LoggerConfiguration> loggerConfigurations = this.loggingSystem.getLoggerConfigurations();
                List<Object> list = new ArrayList<>();

                list.add(StrFormatter.format("当前启动环境 sparkEnv: {}", this.sparkEnv.getName()));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_CONFIG,
                                             System.getProperty(LoggingApplicationListener.CONFIG_PROPERTY)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_FILE_PATH, this.logFileProcessor.getPath()));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_FILE_NAME, this.logFileProcessor.getName()));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_APP_NAME,
                                             System.getProperty(Constants.APP_NAME)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.SHOW_LOG_LOCATION,
                                             System.getProperty(Constants.SHOW_LOG_LOCATION)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_PATTERN_CONSOLE,
                                             System.getProperty(Constants.CONSOLE_LOG_PATTERN)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_PATTERN_FILE,
                                             System.getProperty(Constants.FILE_LOG_PATTERN)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_PATTERN_LEVEL,
                                             System.getProperty(Constants.LOG_LEVEL_PATTERN)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_PATTERN_DATEFORMAT,
                                             System.getProperty(Constants.LOG_DATEFORMAT_PATTERN)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.ROLLING_FILE_NAME,
                                             System.getProperty(Constants.ROLLING_FILE_NAME_PATTERN)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.MARKER_PATTERN,
                                             System.getProperty(Constants.MARKER_PATTERN)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_FILE_CLEAN_HISTORY,
                                             System.getProperty(Constants.FILE_CLEAN_HISTORY_ON_START)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_FILE_MAX_HISTORY,
                                             System.getProperty(Constants.FILE_MAX_HISTORY)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_FILE_MAX_SIZE,
                                             System.getProperty(Constants.FILE_MAX_SIZE)));
                list.add(StrFormatter.format("{}: {}", ConfigKey.LogSystemConfigKey.LOG_FILE_TOTAL_SIZE_CAP,
                                             System.getProperty(Constants.FILE_TOTAL_SIZE_CAP)));
                System.out.println("==================== 日志配置 ====================");
                loggerConfigurations.forEach(log -> list.add(StrFormatter.format("{}: {}", log.getName(), log.getEffectiveLevel())));
                System.out.println(GsonUtils.toJson(list, true));
                System.out.println("==================== 日志配置 ====================");
                list.clear();
            }

        }
    }
}
