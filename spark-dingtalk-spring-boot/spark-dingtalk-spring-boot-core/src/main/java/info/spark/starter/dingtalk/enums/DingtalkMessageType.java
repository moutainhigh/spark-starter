package info.spark.starter.dingtalk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.05 00:27
 * @since 2.1.0
 */
@Getter
@AllArgsConstructor
public enum DingtalkMessageType {
    /** Text dingtalk message type */
    TEXT(0, "text"),
    /** Link dingtalk message type */
    LINK(1, "link"),
    /** Markdown dingtalk message type */
    MARKDOWN(2, "markdown"),
    /** Action card dingtalk message type */
    ACTION_CARD(3, "actionCard"),
    /** Feed card dingtalk message type */
    FEED_CARD(4, "feedCard");

    /** Value */
    Integer value;
    /** Desc */
    String desc;
}
