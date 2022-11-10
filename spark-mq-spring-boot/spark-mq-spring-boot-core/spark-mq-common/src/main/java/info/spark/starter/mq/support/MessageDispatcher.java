package info.spark.starter.mq.support;

import info.spark.starter.mq.entity.AbstractMessage;
import info.spark.starter.mq.entity.Distribute;

import java.util.function.Consumer;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.10 14:14
 * @since 1.7.0
 */
public interface MessageDispatcher {

    /**
     * Dispatcher
     *
     * @param message message
     * @since 1.7.0
     */
    void dispatcher(String message);

    /**
     * Dispatcher
     *
     * @param distribute  distribute
     * @param callback    callback
     * @since 1.7.0
     */
    void dispatcher(Distribute distribute, Consumer<Boolean> callback);

    /**
     * Dispatcher
     *
     * @param <T>     parameter
     * @param message message
     * @since 1.7.0
     */
    <T extends AbstractMessage> void dispatcher(T message);
}
