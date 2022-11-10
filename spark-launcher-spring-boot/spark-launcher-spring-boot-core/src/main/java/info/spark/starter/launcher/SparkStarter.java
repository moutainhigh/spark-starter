package info.spark.starter.launcher;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.common.exception.StarterException;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.launcher.annotation.RunningType;
import info.spark.starter.launcher.enums.ApplicationType;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import cn.hutool.core.util.ClassLoaderUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 此类在 Spring Boot 启动之前初始化
 * 注意: 不能直接运行, 必须通过子类运行 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.27 01:18
 * @since 1.0.0
 */
@Slf4j
public abstract class SparkStarter implements CommandLineRunner {
    /** applicationClassName */
    private static String applicationClassName;
    /** applicationClass */
    private static Class<?> applicationClass;
    /** started */
    private static boolean started = false;
    /** SPRING_BOOT_APPLICATION */
    private static final String SPRING_BOOT_APPLICATION = "org.springframework.boot.autoconfigure.SpringBootApplication";
    /** ENABLE_AUTOCONFIGURATION */
    private static final String ENABLE_AUTOCONFIGURATION = "org.springframework.boot.autoconfigure.EnableAutoConfiguration";
    /** LOCK */
    private static final ReentrantLock LOCK = new ReentrantLock();
    /** STOP */
    private static final Condition STOP = LOCK.newCondition();
    /** applicationType */
    private static ApplicationType applicationType = ApplicationType.deduceFromClasspath();
    /** START_CLASS_ARGS */
    public static final String START_CLASS_ARGS = "--start.class=";

    /**
     * 从参数中获取启动类名, 此方法主要用于处理使用 shell 脚本启动时的增强处理
     *
     * @param args args
     * @since 1.0.0
     */
    private static void processorArgs(String @NotNull [] args) {
        for (String arg : args) {
            if (arg.startsWith(START_CLASS_ARGS)) {
                applicationClassName = arg.substring(START_CLASS_ARGS.length());
                break;
            }
        }
    }

    /**
     * 检查启动类: 一个应用只允许存在一个被 @SpringBootApplication 标识的主类.
     *
     * @since 1.0.0
     */
    private static void check() {
        if (StringUtils.isBlank(applicationClassName)) {
            ConfigurationBuilder build = ConfigurationBuilder.build(ConfigDefaultValue.BASE_PACKAGES,
                                                                    new SubTypesScanner(false));
            build.setExpandSuperTypes(false);
            Reflections reflections = new Reflections(build);

            Set<Class<? extends SparkStarter>> subTypesOf = reflections.getSubTypesOf(SparkStarter.class);
            if (CollectionUtils.isEmpty(subTypesOf)) {
                throw new IllegalStateException("错误原因: \n"
                                                + "\n"
                                                + "没有找到 SparkStarter 的子类: 不能直接通过 SparkStarter.main() 启动, 必须通过子类启动, 写法如下: \n"
                                                + "\n"
                                                + "@SpringBootApplication\n"
                                                + "public class DemoApplication extends SparkStarter {\n"
                                                + "    // 不需要写 main()\n"
                                                + "}");
            }

            if (subTypesOf.size() > 1) {
                throw new IllegalStateException("一个应用只允许存在一个启动类!");
            }
            applicationClassName = subTypesOf.stream().findFirst().orElseThrow(RuntimeException::new).getName();
        }

        verificationStartClass(applicationClassName);
    }

    /**
     * Verification start class *
     *
     * @param startClassName start class name
     * @since 1.0.0
     */
    private static void verificationStartClass(String startClassName) {
        try {
            Class<?> mainClass = ClassLoaderUtil.getClassLoader().loadClass(startClassName);
            boolean matched = Arrays.stream(mainClass.getAnnotations())
                .anyMatch(m -> m.annotationType().getName()
                                   .matches(SPRING_BOOT_APPLICATION) ||
                               m.annotationType().getName()
                                   .matches(ENABLE_AUTOCONFIGURATION));
            if (!matched) {
                throw new StarterException("启动类必须使用 @SpringBootApplication 或者 @EnableAutoConfiguration 注解");
            }
            applicationClass = mainClass;
        } catch (Exception e) {
            throw new StarterException(e.getMessage());
        }
    }

    /**
     * 判断应用类型
     *
     * @since 1.0.0
     */
    private static void processApplicationType() {
        RunningType runningType = AnnotationUtils.findAnnotation(applicationClass, RunningType.class);
        if (runningType != null) {
            applicationType = runningType.value();
        }
        processRunningType();
    }

    /**
     * Process running type  @param configurableApplicationContext configurable application context
     *
     * @since 1.0.0
     */
    private static void processRunningType() {
        ApplicationType automaticDeterminationType = ApplicationType.deduceFromClasspath();
        switch (applicationType) {
            case NONE:
                if (automaticDeterminationType == ApplicationType.SERVLET
                    || automaticDeterminationType == ApplicationType.REACTIVE) {
                    log.warn("当前应用已设置为非 WEB 应用, 但是存在 WEB 相关依赖, 请删除以减少部署包体积.");
                }
                break;
            case SERVICE:
                if (automaticDeterminationType == ApplicationType.SERVLET
                    || automaticDeterminationType == ApplicationType.REACTIVE) {
                    log.warn("当前应用已设置非 WEB 应用, 但是存在 WEB 相关依赖, 请删除以减少部署包体积.");
                }
                break;
            case SERVLET:
                if (automaticDeterminationType == ApplicationType.NONE) {
                    throw new StarterException("当前应用已设置 WEB 应用, 但是不存在 WEB(Servlet) 相关依赖.");
                }
                break;
            case REACTIVE:
                if (automaticDeterminationType == ApplicationType.NONE) {
                    throw new StarterException("当前应用已设置 WEB 应用, 但是不存在 WEB(Web.flux) 相关依赖.");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理主类上的启动标识, 如果是 {@link ApplicationType#SERVICE} 则启动一个守护线程, 防止主线程退出
     *
     * @param applicationContext application context
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:Regexp")
    private static void addHook(ConfigurableApplicationContext applicationContext) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                applicationContext.stop();
            } catch (Exception e) {
                log.error("SparkStarter stop exception ", e);
            }

            LOCK.lock();
            try {
                STOP.signal();
            } finally {
                LOCK.unlock();
            }
        }, "Shutdown-Hook"));

        // 主线程阻塞等待, 守护线程释放锁后退出
        LOCK.lock();
        try {
            STOP.await();
        } catch (InterruptedException e) {
            log.warn("service stopped, interrupted by other thread!", e);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * Start *
     *
     * @param args args
     * @return the configurable application context
     * @throws Exception exception
     * @since 1.0.0
     */
    private ConfigurableApplicationContext start(String[] args) throws Exception {
        ConfigurableApplicationContext context = runing(args);
        this.publishEvent(context);

        this.callRunners(context);
        return context;
    }

    /**
     * 启动完成后执行逻辑
     *
     * @param context context
     * @since 1.5.0
     */
    private void callRunners(@NotNull ApplicationContext context) {
        Collection<SparkStarter> values = context.getBeansOfType(SparkStarter.class).values();
        values.forEach(SparkStarter::after);
    }

    /**
     * 内部使用
     *
     * @param args args
     * @return the configurable application context
     * @throws Exception exception
     * @since 1.0.0
     */
    private static ConfigurableApplicationContext runing(String[] args) throws Exception {
        return run(applicationClass, args);
    }

    /**
     * 在 spring 容器启动之前执行自定义逻辑.
     *
     * @since 1.0.0
     */
    protected void before() {

    }

    /**
     * 在容器刷新完成后执行逻辑, {@link CommandLineRunner#run(java.lang.String...)}
     *
     * @param args args
     * @since 1.0.0
     */
    @Override
    public void run(String... args) {

    }

    /**
     * 在启动完成后且在 {@link SparkStarter#after()} 之前发送事件.
     *
     * @param context the context
     * @since 1.0.0
     */
    protected void publishEvent(ConfigurableApplicationContext context) {

    }

    /**
     * 在 spring 容器启动完成后执行自定义逻辑.
     *
     * @since 1.0.0
     */
    protected void after() {

    }

    /**
     * 单元测试时使用, 作为内嵌应用.
     *
     * @param source source
     * @param args   args
     * @return the configurable application context
     * @throws Exception exception
     * @since 1.0.0
     */
    public static ConfigurableApplicationContext run(Class<?> source, String... args) throws Exception {
        return SparkApplication.run(source, applicationType, args);
    }

    /**
     * 启动入口
     * org.apache.logging.log4j.core.util.DefaultShutdownCallbackRegistry
     *
     * @param args the input arguments
     * @see AbstractApplicationContext#registerShutdownHook()
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:UncommentedMain")
    public static void main(String[] args) {
        System.setProperty("log4j2.isThreadContextMapInheritable", Boolean.TRUE.toString());
        try {
            if (!started) {
                // 处理启动参数
                processorArgs(args);
                // 启动类检查
                check();
                // 处理应用类型
                processApplicationType();
                // 反射实例化子类来启动 Spring Boot
                Constructor<?> constructor = applicationClass.getConstructor();
                SparkStarter starter = (SparkStarter) constructor.newInstance();

                starter.before();
                ConfigurableApplicationContext configurableApplicationContext;
                configurableApplicationContext = starter.start(args);

                started = true;

                if (applicationType == ApplicationType.SERVICE) {
                    addHook(configurableApplicationContext);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("启动失败: ", e);
        }
    }
}
