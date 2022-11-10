package info.spark.starter.logsystem.storage;

import info.spark.starter.logsystem.entity.AbstractLog;

/**
 * <p>Description:  </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:41
 * @since 1.0.0
 */
public interface ILogStorage<T extends AbstractLog> {

    /**
     * Save boolean
     *
     * @param logging logging
     * @return the boolean
     * @since 1.0.0
     */
    boolean save(T logging);
}
