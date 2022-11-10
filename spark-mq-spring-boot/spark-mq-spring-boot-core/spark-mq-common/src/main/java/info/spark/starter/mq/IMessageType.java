package info.spark.starter.mq;

/**
 * <p>Description: 消息类型接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 15:49
 * @since 1.5.0
 */
public interface IMessageType {

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.5.0
     */
    String getName();

    /**
     * Gets comment *
     *
     * @return the comment
     * @since 1.5.0
     */
    String getComment();
}
