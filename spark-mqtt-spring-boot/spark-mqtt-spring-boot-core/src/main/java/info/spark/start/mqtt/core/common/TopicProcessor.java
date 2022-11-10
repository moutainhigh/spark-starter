package info.spark.start.mqtt.core.common;

import info.spark.starter.basic.util.StringPool;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.23 11:50
 * @since 2.1.0
 */
@Slf4j
public class TopicProcessor {


    /**
     * Processor
     *
     * @param topic     topic
     * @param topicType topic type
     * @return the string
     * @since 2.1.0
     */
    public static String processor(String topic, TopicType topicType) {
        if (TopicType.SYS.equals(topicType)) {
            return sys(topic);
        } else {
            return topic;
        }
    }

    /**
     * Sys
     *
     * @param topic topic
     * @return the string
     * @since 2.1.0
     */
    private static String sys(String topic) {
        boolean slashStartsWith = topic.startsWith(StringPool.SLASH);
        if (!slashStartsWith) {
            topic = StringPool.SLASH + topic;
        }
        boolean sysStartsWith = topic.startsWith(MqttStartConstName.SYS_TOPIC);
        if (sysStartsWith) {
            return topic;
        }
        //给topic拼接上斜杠
        return String.format("%s%s", MqttStartConstName.SYS_TOPIC, topic);
    }

}
