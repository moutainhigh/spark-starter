package info.spark.starter.cloud.autoconfigure.nacos;

import java.util.Objects;

/**
 * <p>Description:  </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.19 22:57
 * @since 1.7.1
 */
abstract class AbstractNacosManager<T> {
    /** service */
    protected T service;

    /** Nacos config properties */
    protected final SparkNacosProperties sparkNacosProperties;

    /**
     * Nacos config manager
     *
     * @param sparkNacosProperties spark nacos properties
     * @since 1.0.0
     */
    AbstractNacosManager(SparkNacosProperties sparkNacosProperties) {
        this.sparkNacosProperties = sparkNacosProperties;
        this.service = this.createService(sparkNacosProperties);
    }

    /**
     * Create service
     *
     * @param sparkNacosProperties spark nacos properties
     * @return the t
     * @since 1.7.1
     */
    public abstract T createService(SparkNacosProperties sparkNacosProperties);

    /**
     * Gets config service *
     *
     * @return the config service
     * @since 1.0.0
     */
    public T getService() {
        if (Objects.isNull(this.service)) {
            this.createService(this.sparkNacosProperties);
        }
        return this.service;
    }

    /**
     * Properties
     *
     * @return the spark nacos properties
     * @since 1.7.1
     */
    public SparkNacosProperties getProperties() {
        return this.sparkNacosProperties;
    }
}
