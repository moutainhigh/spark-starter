package info.spark.agent;

/**
 * <p>Description:  </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.21 17:49
 * @since 1.6.0
 */
public interface CommonCacheService<T> {

    /**
     * Gets secret *
     *
     * @param key client id
     * @return the secret
     * @since 1.6.0
     */
    T get(String key);

    /**
     * Set
     *
     * @param key   client id
     * @param value secret
     * @since 1.6.0
     */
    void set(String key, T value);

}
