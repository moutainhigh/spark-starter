package info.spark.mqtt.handler;

import info.spark.start.mqtt.core.annotation.MqttClient;
import info.spark.start.mqtt.core.annotation.MqttSubscribe;
import info.spark.start.mqtt.core.callback.MqttCallBackHandler;
import info.spark.start.mqtt.core.entity.MqttClientMessage;
import info.spark.start.mqtt.core.provider.MqttMessageProvider;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 16:41
 * @since 2.1.0
 */
@MqttClient(value = "${spark.mqtt.clients[0].clientId}")
@Slf4j
public class MqttClientHandler implements MqttCallBackHandler {

    @Resource
    private MqttMessageProvider mqttMsgSender;

    @Override
    public void messageArrived(MqttClientMessage m) {
        log.debug("处理mqtt消息：{}", m);
        this.mqttMsgSender.send("test2", m, 1);
    }

    @MqttSubscribe("test2")
    public void m(String msg) {
        log.info("接收：{}", msg);
    }

}
