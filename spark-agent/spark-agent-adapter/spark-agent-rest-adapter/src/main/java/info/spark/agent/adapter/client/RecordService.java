package info.spark.agent.adapter.client;

import info.spark.agent.adapter.entity.AgentRecord;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.27 21:08
 * @since 1.7.0
 */
public interface RecordService {

    /**
     * Save
     *
     * @param record record
     * @since 1.6.0
     */
    default void save(AgentRecord record) {

    }
}
