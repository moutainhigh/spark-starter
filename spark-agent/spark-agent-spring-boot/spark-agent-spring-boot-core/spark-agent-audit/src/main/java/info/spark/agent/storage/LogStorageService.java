package info.spark.agent.storage;

import info.spark.agent.entity.AgentAuditLog;

/**
 * <p>Description: 日志存储接口, 由各应用自己实现 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 17:19
 * @since 1.6.0
 */
public interface LogStorageService {

    /**
     * 保存系统日志, 一般用于后台系统.
     *
     * @param log 日志实体
     * @return boolean boolean
     * @since 1.6.0
     */
    Boolean save(AgentAuditLog log);

}
