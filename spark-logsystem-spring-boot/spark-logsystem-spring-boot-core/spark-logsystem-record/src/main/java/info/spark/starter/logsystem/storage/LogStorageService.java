package info.spark.starter.logsystem.storage;


import info.spark.starter.logsystem.entity.ApiLog;
import info.spark.starter.logsystem.entity.ErrorLog;
import info.spark.starter.logsystem.entity.SystemLog;

/**
 * <p>Description: 日志存储接口, 由各应用自己实现 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 17:19
 * @since 1.0.0
 */
public interface LogStorageService {

    /**
     * 保存系统日志, 一般用于后台系统.
     *
     * @param log 日志实体
     * @return boolean boolean
     * @since 1.0.0
     */
    Boolean save(SystemLog log);

    /**
     * 保存接口日志, 适用于所有的 web 服务.
     *
     * @param log 日志实体
     * @return boolean boolean
     * @since 1.0.0
     */
    Boolean save(ApiLog log);

    /**
     * 保存错误日志, 一般存储在 ES.
     *
     * @param log 日志实体
     * @return boolean boolean
     * @since 1.0.0
     */
    Boolean save(ErrorLog log);
}
