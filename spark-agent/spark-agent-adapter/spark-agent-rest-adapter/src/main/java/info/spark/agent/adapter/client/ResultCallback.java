package info.spark.agent.adapter.client;

import info.spark.agent.adapter.entity.AgentRecord;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.23 14:13
 * @since 1.7.0
 */
public interface ResultCallback {

    /**
     * 请求成功时回调
     *
     * @param record record
     * @since 1.0.0
     */
    default void successed(AgentRecord record) {

    }

    /**
     * 失败时回调
     *
     * @param record record
     * @since 1.0.0
     */
    default void failed(AgentRecord record) {

    }
}
