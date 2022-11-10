package info.spark.starter.launcher.spi;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.enums.SparkEnv;
import info.spark.starter.common.enums.SerializeEnum;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.util.FileUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.launcher.constant.Launcher;
import info.spark.starter.processor.annotation.AutoService;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.27 12:23
 * @since 1.0.0
 */
@Slf4j
@AutoService(LauncherInitiation.class)
public class SubLauncherInitiation implements LauncherInitiation {
    /** SPRING_PROFILE_ACTIVE_FILE */
    private static final String SPRING_PROFILE_ACTIVE_FILE = "spring.profiles.active";

    /**
     * 检查枚举的 value 是否重复
     *
     * @param appName app name
     * @since 2022.1.1
     */
    @SneakyThrows
    @Override
    @SuppressWarnings(value = {"rawtypes", "unchecked"})
    public void advance(String appName) {
        // 查找 SerializeEnum 的实现类
        ConfigurationBuilder build = ConfigurationBuilder.build(ConfigDefaultValue.BASE_PACKAGES,
                                                                new SubTypesScanner(false));
        build.setExpandSuperTypes(false);
        Reflections reflections = new Reflections(build);

        Set<Class<? extends SerializeEnum>> subTypesOf = reflections.getSubTypesOf(SerializeEnum.class);
        if (subTypesOf != null) {
            for (Class<? extends SerializeEnum> klass : subTypesOf) {
                if (klass.isInterface() || !klass.isEnum()) {
                    continue;
                }
                Class<Enum> aClass = (Class<Enum>) Class.forName(klass.getName());
                // 获取所有枚举实例
                Enum[] enumConstants = aClass.getEnumConstants();
                // 根据方法名获取方法
                Method getValue = aClass.getMethod(SerializeEnum.VALUE_METHOD_NAME);

                if (enumConstants.length <= 1) {
                    return;
                }

                for (int i = 0; i < enumConstants.length - 1; i++) {
                    for (int j = 1; j < enumConstants.length; j++) {
                        if (i == j) {
                            continue;
                        }
                        if (getValue.invoke(enumConstants[i]).equals(getValue.invoke(enumConstants[j]))) {
                            throw new IllegalArgumentException(
                                StringUtils.format("存在相同的枚举 value: [{}: {}.value = {}.value]",
                                                   aClass.getName(),
                                                   enumConstants[i],
                                                   enumConstants[j]));
                        }
                    }
                }
            }
        }

    }

    /**
     * Launcher map
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> launcher(ConfigurableEnvironment env, String appName, boolean isLocalLaunch) {
        ChainMap chainMap = ChainMap.build(8)
            // Spring Boot 2.1 需要设定, 存在相同的 bean name 时, 后一个覆盖前一个, 主要用于覆写默认 bean
            .put(ConfigKey.SpringConfigKey.MAIN_ALLOW_BEAN_DEFINITION_OVERRIDING, ConfigDefaultValue.TRUE)
            // 启动后新增一个 app.pid 文本文件, 写入当前应用的 PID
            .put(ConfigKey.SpringConfigKey.PID_FILE, ConfigDefaultValue.PROP_PID_FILE)
            // 配置加密密钥
            .put(ConfigKey.JASYPT_ENCRYPTOR_PASSWORD, ConfigDefaultValue.DEFAULT_ENCRYPTOR_PASSWORD);

        // 本地开发时, 读取 spark-plugin/profile/spring.profiles.active
        if (isLocalLaunch) {
            String targetPath = getTargetDir();
            String finalActiveFilePath = FileUtils.appendPath(targetPath, "spark-plugin", "profile", SPRING_PROFILE_ACTIVE_FILE);

            String currentProfileActive = SparkEnv.LOCAL.getName();
            try {
                String active = StringUtils.trimAllWhitespace(FileUtils.readToString(new File(finalActiveFilePath)));
                if (StringUtils.isBlank(active)) {
                    active = SparkEnv.LOCAL.getName();
                }
                currentProfileActive = active;
            } catch (Exception e) {
                log.debug("未监测到 target/spark-plugin/profile/spring.profiles.active 文件, 将自动设置为 [local].");
                System.setProperty("profile.active", currentProfileActive);
            }

            System.setProperty(ConfigKey.SpringConfigKey.PROFILE_ACTIVE, currentProfileActive);
        }

        return chainMap;
    }

    /**
     * 本地开发时获取 target 目录
     *
     * @return the string
     * @since 1.0.0
     */
    private static @NotNull String getTargetDir() {
        File classPath = new File(ConfigKit.getConfigPath());
        return classPath.getParentFile().getPath();
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return Launcher.MODULE_NAME;
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
