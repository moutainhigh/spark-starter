package info.spark.starter.mq.consumer;

import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.mq.entity.AbstractMessage;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 16:08
 * @since 1.5.0
 */
public abstract class AbstractMessageHandler<T extends AbstractMessage> {

    /**
     * 使用实体类处理业务
     *
     * @param content content
     * @since 1.5.0
     */
    public void handle(T content) {
        this.handle(JsonUtils.toJson(content));
    }

    /**
     * 使用 json 格式的字符串处理业务
     *
     * @param content content
     * @since 1.5.0
     */
    public abstract void handle(String content);

}
