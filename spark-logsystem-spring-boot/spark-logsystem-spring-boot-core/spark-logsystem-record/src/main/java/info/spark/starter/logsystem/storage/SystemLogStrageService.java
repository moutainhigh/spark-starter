package info.spark.starter.logsystem.storage;

import info.spark.starter.logsystem.entity.AbstractLog;
import info.spark.starter.logsystem.entity.SystemLog;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:39
 * @since 1.0.0
 */
public class SystemLogStrageService implements ILogStorage {

    /** Log storage service */
    private LogStorageService logStorageService;

    /**
     * Save boolean
     *
     * @param log log
     * @return the boolean
     * @since 1.0.0
     */
    public boolean save(SystemLog log) {
        return this.logStorageService.save(log);
    }

    /**
     * Save boolean
     *
     * @param log log
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean save(AbstractLog log) {
        return this.logStorageService.save((SystemLog) log);
    }
}
