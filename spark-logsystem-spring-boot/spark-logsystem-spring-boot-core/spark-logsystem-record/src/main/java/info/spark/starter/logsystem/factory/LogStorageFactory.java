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
public interface LogStorageFactory {

    /**
     * Gets log storage service *
     *
     * @return the log storage service
     * @since 1.0.0
     */
    LogStorageService getLogStorageService();

    /**
     * Gets log service *
     *
     * @return the log service
     * @since 1.0.0
     */
    ILogStorage<SystemLog> getSystemLogStorage();

    /**
     * Gets error log storage *
     *
     * @return the error log storage
     * @since 1.0.0
     */
    ILogStorage<ErrorLog> getErrorLogStorage();

    /**
     * Gets api log storage *
     *
     * @return the api log storage
     * @since 1.0.0
     */
    ILogStorage<ApiLog> getApiLogStorage();
}
