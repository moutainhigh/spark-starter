package info.spark.start.mqtt.core.callback;

import info.spark.start.mqtt.core.entity.MqttClientMessage;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 15:10
 * @since 2.1.0
 */
public interface MqttCallBackHandler {

    /**
     * Message arrived
     *
     * @param mqttClientMessage mqtt client message
     * @since 2.1.0
     */
    void messageArrived(MqttClientMessage mqttClientMessage);

}
