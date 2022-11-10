package info.spark.starter.logsystem.factory;

import info.spark.starter.logsystem.entity.ApiLog;
import info.spark.starter.logsystem.entity.ErrorLog;
import info.spark.starter.logsystem.entity.SystemLog;
import info.spark.starter.logsystem.storage.ILogStorage;
import info.spark.starter.logsystem.storage.LogStorageService;

/**
 * <p>Description: 日志存储工厂接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 17:36
 * @since 1.0.0
 */
public abstract class LogStorageFactoryAdapter implements LogStorageFactory {

    /**
     * Gets log storage service *
     *
     * @return the log storage service
     * @since 1.0.0
     */
    @Override
    public LogStorageService getLogStorageService() {
        return null;
    }

    /**
     * Gets system log storage *
     *
     * @return the system log storage
     * @since 1.0.0
     */
    @Override
    public ILogStorage<SystemLog> getSystemLogStorage() {
        return null;
    }

    /**
     * Gets error log storage *
     *
     * @return the error log storage
     * @since 1.0.0
     */
    @Override
    public ILogStorage<ErrorLog> getErrorLogStorage() {
        return null;
    }

    /**
     * Gets api log storage *
     *
     * @return the api log storage
     * @since 1.0.0
     */
    @Override
    public ILogStorage<ApiLog> getApiLogStorage() {
        return null;
    }
}
