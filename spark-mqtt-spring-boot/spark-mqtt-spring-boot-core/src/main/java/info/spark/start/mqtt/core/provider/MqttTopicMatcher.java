package info.spark.start.mqtt.core.provider;

import info.spark.starter.basic.util.StringPool;
import info.spark.starter.util.StringUtils;

import org.springframework.util.Assert;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.17 10:45
 * @since 2.1.0
 */
public class MqttTopicMatcher {


    /**
     * Match
     *
     * @param mqttTopic mqtt topic
     * @param topicPair topic pair
     * @return the boolean
     * @since 2.1.0
     */
    public static boolean match(String mqttTopic, ParamsHolder.TopicPair topicPair) {
        String clientTopic = StringUtils.isEmpty(topicPair.getRealityTopic()) ? topicPair.getTopic() : topicPair.getRealityTopic();
        mqttTopic = validate(mqttTopic);
        clientTopic = validate(clientTopic);
        int mqttTopicPos = 0;
        int clientTopicPos = 0;

        int mqttTopicLength = mqttTopic.length();
        int clientTopicLength = clientTopic.length();

        while (mqttTopicPos < mqttTopicLength && clientTopicPos < clientTopicLength) {
            char clientChar = clientTopic.charAt(clientTopicPos);
            char mqttChar = mqttTopic.charAt(mqttTopicPos);
            //如果客户端订阅的 #号 直接返回
            if (StringPool.HASH.charAt(0) == clientChar) {
                mqttTopicPos = mqttTopicLength;
                clientTopicPos = clientTopicLength;
                break;
            }
            //如果不是 / 开头直接返回
            if (StringPool.SLASH.charAt(0) == clientChar && StringPool.SLASH.charAt(0) != mqttChar) {
                break;
            }
            //如果不是通配符的，并且字符还不相等的
            if (StringPool.PLUS.charAt(0) != clientChar && clientChar != mqttChar) {
                break;
            }
            //匹配+号
            if (StringPool.PLUS.charAt(0) == clientChar) {
                while (StringPool.SLASH.charAt(0) != mqttChar) {
                    ++mqttTopicPos;
                    mqttChar = mqttTopic.charAt(mqttTopicPos);
                }
                clientTopicPos++;
            }
            mqttTopicPos++;
            clientTopicPos++;
        }
        return clientTopicPos == clientTopicLength && mqttTopicPos == mqttTopicLength;
    }

    /**
     * Validate
     *
     * @param topic topic
     * @return the string
     * @since 2.1.0
     */
    public static String validate(String topic) {
        Assert.notNull(topic, "mqtt topic not null");
        if (!topic.endsWith(StringPool.SLASH)) {
            topic += StringPool.SLASH;
        }
        return topic;
    }

}
