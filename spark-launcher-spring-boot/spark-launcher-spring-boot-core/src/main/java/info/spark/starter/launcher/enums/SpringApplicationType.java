package info.spark.starter.launcher.enums;

import info.spark.starter.common.exception.StarterException;

import org.springframework.util.ClassUtils;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.14 16:52
 * @since 1.0.0
 */
public enum SpringApplicationType {

    /** Boot spring application type */
    BOOT,
    /** Cloud spring application type */
    CLOUD;

    /** spring-cloud-context 中的 class */
    private static final String CLOUD_CLASS = "org.springframework.cloud.bootstrap.BootstrapImportSelectorConfiguration";
    /** spring-bbot 中的 class */
    private static final String BOOT_CLASS = "org.springframework.boot.SpringBootVersion";

    /**
     * Deduce from classpath spring application type
     *
     * @return the web application type
     * @since 1.0.0
     */
    public static SpringApplicationType deduceFromClasspath() {
        if (ClassUtils.isPresent(CLOUD_CLASS, null)) {
            return SpringApplicationType.CLOUD;
        }
        if (ClassUtils.isPresent(BOOT_CLASS, null)) {
            return SpringApplicationType.BOOT;
        }

        throw new StarterException("不是 Spring Boot/Cloud 应用.");
    }

}
