package info.spark.starter.mq.support;

import info.spark.starter.mq.consumer.AbstractMessageHandler;
import info.spark.starter.mq.consumer.AbstractMessageNotifyHandler;
import info.spark.starter.mq.entity.AbstractMessage;
import info.spark.starter.mq.entity.Distribute;

import java.util.Map;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.10 11:43
 * @since 1.7.0
 */
public interface MessageLoader {

    /**
     * Load message notify handler
     *
     * @param paramMap    param map
     * @param distribute  distribute
     * @return the abstract message notify handler
     * @since 1.7.0
     */
    AbstractMessageNotifyHandler<?> loadMessageNotifyHandler(Map<String, Object> paramMap, Distribute distribute);

    /**
     * Load message handler
     *
     * @param name name
     * @return the abstract message handler
     * @since 1.7.0
     */
    AbstractMessageHandler<? extends AbstractMessage> loadMessageHandler(String name);

}
