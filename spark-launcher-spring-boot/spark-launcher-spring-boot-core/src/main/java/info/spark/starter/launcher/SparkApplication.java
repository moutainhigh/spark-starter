package info.spark.starter.launcher;

import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.env.DefaultEnvironment;
import info.spark.starter.common.exception.InstanceException;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.common.util.GsonUtils;
import info.spark.starter.common.util.StartUtils;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.ObjectUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.util.Tools;
import info.spark.starter.launcher.enums.ApplicationType;
import info.spark.starter.launcher.enums.SpringApplicationType;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * <p>Description: 启动类封装</p>
 * 1. 加载默认配置
 * 2. SPI 加载其他包的处理类
 * 读取 appName 的优先级
 * 1. JVM 环境变量 --> 2. 配置文件 --> 3. run() 指定 --> 4. 应用所在目录名
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 19:23
 * @see ConfigFileApplicationListener
 * @since 1.0.0
 */
@Slf4j
public final class SparkApplication {
    /** 保存最主要的配置 */
    private static final Properties MAIN_PROPERTIES;

    static {
        MAIN_PROPERTIES = loadMainProperties();
    }

    /**
     * Spark application
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    private SparkApplication() {
        throw new InstanceException("不可实例化");
    }

    /**
     * Run configurable application context
     *
     * @param source source
     * @param args   args
     * @return the configurable application context
     * @throws Exception exception
     * @since 1.0.0
     */
    public static ConfigurableApplicationContext run(Class<?> source,
                                                     String... args) throws Exception {
        return run(source, ApplicationType.deduceFromClasspath(), args);
    }

    /**
     * Run configurable application context
     *
     * @param appName app name
     * @param source  source
     * @param args    args
     * @return the configurable application context
     * @throws Exception exception
     * @since 1.0.0
     */
    public static ConfigurableApplicationContext run(String appName,
                                                     Class<?> source,
                                                     String... args) throws Exception {
        return run(appName, source, ApplicationType.deduceFromClasspath(), args);
    }

    /**
     * 应用名处理, 如果未显式设置则使用启动目录名作为应用名
     *
     * @param source          the source
     * @param applicationType application type
     * @param args            the args
     * @return the configurable application context
     * @throws Exception exception
     * @since 1.0.0
     */
    public static ConfigurableApplicationContext run(Class<?> source, ApplicationType applicationType, String... args)
        throws Exception {
        // 没有设置 application name 时, 使用默认应用名
        return run(MAIN_PROPERTIES.getProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME), source, applicationType, args);
    }

    /**
     * Create an application context
     * java -jar app.jar --spring.profiles.active=prod --server.port=2333
     *
     * @param appName         application name
     * @param source          The sources
     * @param applicationType application type
     * @param args            the args
     * @return an application context created from the current state
     * @since 1.0.0
     */
    public static ConfigurableApplicationContext run(String appName,
                                                     Class<?> source,
                                                     ApplicationType applicationType,
                                                     String... args) {
        // 设置是否使用 SparkApplication 启动标识
        System.setProperty(App.START_SPARK_APPLICATION, App.START_SPARK_APPLICATION);
        ConfigurableApplicationContext context;
        // 优先使用启动类中设置的 application name
        MAIN_PROPERTIES.setProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME, appName);
        SpringApplicationBuilder builder = createSpringApplicationBuilder(appName, source, applicationType, args);
        builder.registerShutdownHook(true);
        context = builder.run(args);
        return context;
    }

    /**
     * 设置 默认配置和 profiles, 用过 SPI 加载其他包的组件
     *
     * @param appName         the app name
     * @param source          the source
     * @param applicationType application type
     * @param args            the args
     * @return the spring application builder
     * @see ConfigFileApplicationListener
     * @since 1.0.0
     */
    @NotNull
    private static SpringApplicationBuilder createSpringApplicationBuilder(String appName,
                                                                           Class<?> source,
                                                                           @NotNull ApplicationType applicationType,
                                                                           String... args) {
        Assertions.notBlank(appName, "[appName] 服务名不能为空");
        StartUtils.setFrameworkVersion();

        // 生成默认的配置
        Properties defaultProperties = buildDefaultProperties(appName);

        // 读取环境变量,使用 spring boot 的规则 (获取系统参数和 JVM 参数)
        ConfigurableEnvironment environment = new DefaultEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new SimpleCommandLinePropertySource(args));

        // 加载自定义 SPI 组件
        ServiceLoader<LauncherInitiation> loader = ServiceLoader.load(LauncherInitiation.class);
        CollectionUtils.toList(loader).stream().sorted(Comparator.comparingInt(LauncherInitiation::getOrder))
            .forEach(launcherService -> launcherService.launcherWrapper(environment,
                                                                        defaultProperties,
                                                                        appName,
                                                                        ConfigKit.isLocalLaunch()));

        log.debug("应用类型: ApplicationType = {}", applicationType.name());
        ConfigKit.setSystemProperties(App.APPLICATION_TYPE, applicationType.name());

        // 转换类型
        if (applicationType == ApplicationType.SERVICE) {
            applicationType = ApplicationType.NONE;
        }

        SpringApplicationBuilder builder = new SpringApplicationBuilder(source)
            .web(Tools.convert(applicationType.name(), org.springframework.boot.WebApplicationType.class))
            .main(source);

        builder.properties(defaultProperties);

        if (ConfigKit.isDebugModel()) {
            log.debug("全部的默认配置:\n{}", GsonUtils.toJson(defaultProperties, true));
        }

        propertySources.addLast(new MapPropertySource(DefaultEnvironment.DEFAULT_PROPERTIES_PROPERTY_SOURCE_NAME,
                                                      Tools.getMapFromProperties(defaultProperties)));

        return builder;
    }

    /**
     * Build default properties properties.
     *
     * @param appName the app name
     * @return the properties
     * @since 1.0.0
     */
    @NotNull
    private static Properties buildDefaultProperties(String appName) {
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty(ConfigKey.POM_INFO_VERSION,
                                      MAIN_PROPERTIES.getProperty("version",
                                                                  System.getProperty(ConfigKey.SERVICE_VERSION)));
        defaultProperties.setProperty(ConfigKey.POM_INFO_GROUPID,
                                      MAIN_PROPERTIES.getProperty("groupId", App.BASE_PACKAGES));
        defaultProperties.setProperty(ConfigKey.POM_INFO_ARTIFACTID,
                                      appName);
        defaultProperties.setProperty(ConfigKey.SERVICE_VERSION,
                                      MAIN_PROPERTIES.getProperty("version",
                                                                  System.getProperty(ConfigKey.SERVICE_VERSION)));

        // 设置默认应用名, 可以通过环境变量修改或者是配置文件修改
        defaultProperties.setProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME, appName);
        // 设置 package.name, 用于替换配置文件中的 ${package.name}
        defaultProperties.setProperty(ConfigKey.SpringConfigKey.PACKAGE_NAME, appName);
        // todo-dong4j :(2019 年09月11日 11:07) [设置 spark-admin 默认配置(定位日志位置)]
        defaultProperties.putAll(MAIN_PROPERTIES);
        return defaultProperties;
    }

    /**
     * 获取应用名
     * 1. 默认通过 jar 启动应用, 从 pom.properties 中获取 artifactId 的值
     * 2. 为空则解析 classpath 路径
     * 注意: applicationName 规定使用 maven 中的 artifactId, 日志文件保存路径也会使用到 artifactId
     * maven.artifactId --> spring.application.name --> 日志路径
     *
     * @return the properties
     * @since 1.0.0
     */
    @NotNull
    private static Properties loadMainProperties() {
        log.info("App NameSpace: [{}], 如果不正确请设置 JVM 变量/系统环境变量: 「user.namespace」或: [SPARK_NAME_SPACE]", App.SPARK_NAME_SPACE);
        log.info("本地开发时, 默认为 local 环境, 如果需要连接 dev 或 test 环境, 请修改 [spark-plugin/profile/spring.profiles.active] 配置");
        // 获取配置文件路径, 有多种情况 (本地运行, junit 运行, jar 运行)
        String configFilePath = ConfigKit.getConfigPath();
        String startType = System.getProperty(App.START_TYPE);
        String applicationName;
        String version = StringPool.NULL_STRING;
        Properties properties = new Properties();
        // shell 脚本启动, 优先从 jar 的 MANIFEST.MF 读取
        if (StringUtils.isNotBlank(startType) && startType.equals(App.START_SHELL)) {
            // 优先解析 jar 文件中的 MANIFEST.MF 文件, jar.file 环境变量通过 server.sh 启动脚本设置
            try (JarFile jarFile = new JarFile(System.getProperty("jar.file"))) {
                Manifest manifest = jarFile.getManifest();
                log.info("MANIFEST.MF Info:");
                manifest.getMainAttributes().forEach((k, v) -> log.info("[{}:{}]", k, v));
                applicationName = manifest.getMainAttributes().getValue("Project-Name");
                version = manifest.getMainAttributes().getValue("Implementation-Version");
            } catch (Exception e) {
                // 如果在 IDE 中指定 start.type=shell 时(本地模拟部署时使用), 则会抛出 NPE 异常, 这里就会解析 build-info.properties 文件
                try (val fileInputStream = new FileInputStream(configFilePath + App.APP_BULID_INFO_FILE_NAME)) {
                    properties.load(fileInputStream);
                    applicationName = properties.getProperty("build.project.name");
                    version = properties.getProperty("build.version");
                } catch (Exception ex) {
                    log.warn("Launcher the app via shell, but not find build-info.properties. Analysis from jar");
                    // 抛异常是因为没有 build-info.properties 文件, 这时则通过解析 jar 来获取默认应用名
                    String startPath = Objects.requireNonNull(SparkApplication.class.getResource(StringPool.SLASH))
                        .getPath()
                        .split("!")[0];
                    // 非 jar 启动
                    File file = new File(startPath);
                    applicationName = file.getParentFile().getParentFile().getName();
                }
            }
        } else {
            Object name = null;
            try {
                // 检查配置文件是否配置相关属性
                PropertySource<?> propertySource;
                SpringApplicationType type = SpringApplicationType.deduceFromClasspath();
                if (type == SpringApplicationType.CLOUD) {
                    // 如果是 cloud 应用, 解析 bootstrap.yml
                    propertySource = ConfigKit.getPropertySource(ConfigKit.CLOUD_CONFIG_FILE_NAME);
                } else {
                    // 如果是 boot 应用, 解析 application.yml
                    propertySource = ConfigKit.getPropertySource(ConfigKit.BOOT_CONFIG_FILE_NAME);
                }
                name = propertySource.getProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME);

                deprecatedPropertiesCheck(propertySource);
            } catch (Exception ignored) {
                // nothing to do.
            }

            if (name != null && !name.equals(StringPool.DOLLAR_LEFT_BRACE
                                             + ConfigKey.SpringConfigKey.PACKAGE_NAME
                                             + StringPool.RIGHT_BRACE)) {
                applicationName = name.toString();
            } else {
                File file = new File(configFilePath);
                // 直接解析文件目录, 使用当前目录名作为应用名 (target 上一级目录)
                applicationName = file.getParentFile().getParentFile().getName();
                log.warn("未显式设置 application name 或者未正确解析 ${package.name} (可能需要重新编译项目), 读取当前模块名作为应用名: [{}]",
                         applicationName);
                if (StringUtils.isBlank(startType)) {
                    // 如果 startType 为 null, 则是从 IDE 中启动
                    System.setProperty(App.START_TYPE, App.START_IDEA);
                }

                File buildInfoFile = new File(file, "META-INF/" + App.APP_BULID_INFO_FILE_NAME);
                if (buildInfoFile.exists()) {
                    try (val fileInputStream = new FileInputStream(buildInfoFile)) {
                        properties.load(fileInputStream);
                        version = properties.getProperty("build.version");
                    } catch (Exception ignore) {
                        // nothing to do.
                    }
                }
            }
        }

        // 环境变量是最高级别
        applicationName = System.getProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME, applicationName);
        properties.put(ConfigKey.SpringConfigKey.APPLICATION_NAME, applicationName);
        // 设置当前的应用版本, 在 banner 中输出
        System.setProperty(ConfigKey.SERVICE_VERSION, App.FRAMEWORK_VERSION_PREFIX + version);
        return properties;
    }

    /**
     * 检查是否存在过时的配置项
     *
     * @param propertySource property source
     * @since 1.4.0
     */
    private static void deprecatedPropertiesCheck(@NotNull PropertySource<?> propertySource) {
        Object property = propertySource.getProperty(ConfigKey.SpringConfigKey.PROFILE_ACTIVE);
        if (ObjectUtils.isNotNull(property)) {
            log.error("1.4.0+ 开始不需要在配置文件 (bootstrap.yml/application.yml) 中配置 spring.profiles.active 属性, 请删除此配置以修复错误.");
        }
    }
}
