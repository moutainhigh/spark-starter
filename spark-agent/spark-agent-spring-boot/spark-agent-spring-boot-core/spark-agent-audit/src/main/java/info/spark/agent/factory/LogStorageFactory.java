package info.spark.agent.factory;

import info.spark.agent.entity.AgentAuditLog;
import info.spark.agent.storage.ILogStorage;
import info.spark.agent.storage.LogStorageService;

/**
 * <p>Description: 日志存储工厂接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 17:36
 * @since 1.6.0
 */
public interface LogStorageFactory {

    /**
     * Gets log storage service *
     *
     * @return the log storage service
     * @since 1.6.0
     */
    LogStorageService getLogStorageService();

    /**
     * Gets log service *
     *
     * @return the log service
     * @since 1.6.0
     */
    ILogStorage<AgentAuditLog> getAgentAuditLogStorage();
}
