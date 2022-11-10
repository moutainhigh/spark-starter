package info.spark.agent.storage;

import info.spark.agent.entity.AgentAuditLog;

/**
 * <p>Description: 日志存储适配器 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 21:17
 * @since 1.6.0
 */
public abstract class AbstractLogStorageService implements LogStorageService {

    /**
     * Save usual log boolean
     *
     * @param log log
     * @return the boolean
     * @since 1.6.0
     */
    @Override
    public Boolean save(AgentAuditLog log) {
        return true;
    }

}
