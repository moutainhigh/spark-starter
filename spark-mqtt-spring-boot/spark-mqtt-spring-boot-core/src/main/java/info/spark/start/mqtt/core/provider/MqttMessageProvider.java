package info.spark.start.mqtt.core.provider;

import info.spark.start.mqtt.core.connector.MqttConnector;
import info.spark.start.mqtt.core.converter.MqttConverterService;
import info.spark.starter.util.RandomUtils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.17 15:00
 * @since 2.1.0
 */
@Slf4j
public class MqttMessageProvider {

    /** Mqtt connector */
    @Resource
    private MqttConnector mqttConnector;

    /**
     * Send
     *
     * @param topic   topic
     * @param payload payload
     * @since 2.1.0
     */
    public void send(String topic,
                     Object payload) {
        send(null,
             topic,
             payload,
             this.mqttConnector.getDefaultPubQos(this.mqttConnector.getDefaultClientId()),
             null);
    }

    /**
     * Send
     *
     * @param topic   topic
     * @param payload payload
     * @param qos     qos
     * @since 2.1.0
     */
    public void send(String topic,
                     Object payload,
                     Integer qos) {
        send(null,
             topic,
             payload,
             qos,
             null);
    }

    /**
     * Send
     *
     * @param topic    topic
     * @param payload  payload
     * @param qos      qos
     * @param callBack call back
     * @since 2.1.0
     */
    public void send(String topic,
                     Object payload,
                     Integer qos,
                     IMqttActionListener callBack) {
        send(null,
             topic,
             payload,
             qos,
             callBack);
    }

    /**
     * Send
     *
     * @param clientId client id
     * @param topic    topic
     * @param payload  payload
     * @since 2.1.0
     */
    public void send(String clientId,
                     String topic,
                     Object payload) {
        send(clientId,
             topic,
             payload,
             this.mqttConnector.getDefaultPubQos(clientId),
             null);
    }

    /**
     * Send
     *
     * @param clientId client id
     * @param topic    topic
     * @param payload  payload
     * @param qos      qos
     * @since 2.1.0
     */
    public void send(String clientId,
                     String topic,
                     Object payload,
                     Integer qos) {
        send(clientId,
             topic,
             payload,
             qos,
             null);
    }

    /**
     * Send
     *
     * @param clientId client id
     * @param topic    topic
     * @param payload  payload
     * @param callback callback
     * @since 2.1.0
     */
    public void send(String clientId,
                     String topic,
                     Object payload,
                     IMqttActionListener callback) {
        send(clientId,
             topic,
             payload,
             this.mqttConnector.getDefaultPubQos(clientId),
             callback);
    }

    /**
     * Send
     *
     * @param clientId client id
     * @param topic    topic
     * @param payload  payload
     * @since 2.1.0
     */
    public void send(String clientId,
                     String[] topic,
                     Object payload) {
        for (int i = 0; i < topic.length; i++) {
            send(clientId,
                 topic[i],
                 payload,
                 this.mqttConnector.getDefaultPubQos(clientId),
                 null);
        }
    }

    /**
     * Send
     *
     * @param topic   topic
     * @param payload payload
     * @since 2.1.0
     */
    public void send(String[] topic,
                     Object payload) {
        for (int i = 0; i < topic.length; i++) {
            send(null,
                 topic[i],
                 payload,
                 this.mqttConnector.getDefaultPubQos(this.mqttConnector.getDefaultClientId()),
                 null);
        }
    }


    /**
     * Send
     *
     * @param clientId client id
     * @param topic    topic
     * @param payload  payload
     * @param qos      qos
     * @param callBack call back
     * @since 2.1.0
     */
    public void send(String clientId,
                     String topic,
                     Object payload,
                     int qos,
                     IMqttActionListener callBack) {
        IMqttAsyncClient mqttAsyncClient = Objects.requireNonNull(this.mqttConnector.getClientById(clientId),
                                                                  String.format("客户端：%s，不存在", clientId));
        byte[] msg = MqttConverterService.getSharedInstance().toBytes(payload);
        if (msg == null) {
            return;
        }
        MqttMessage message = toMsg(qos, msg);
        try {
            mqttAsyncClient.publish(topic, message, null, callBack);
        } catch (MqttException e) {
            log.error("消息发送失败：{}", e.getMessage());
        }
    }


    /**
     * Subscribe
     *
     * @param clientId client id
     * @param topic    topic
     * @param qos      qos
     * @since 2.1.0
     */
    public void subscribe(String clientId,
                          String topic,
                          int qos) {
        this.subscribes(clientId,
                        new String[] {topic},
                        new int[] {qos});
    }

    /**
     * Subscribe
     *
     * @param topic topic
     * @param qos   qos
     * @since 2.1.0
     */
    public void subscribe(String topic,
                          int qos) {
        this.subscribes(null,
                        new String[] {topic},
                        new int[] {qos});
    }


    /**
     * Subscribes
     *
     * @param topic topic
     * @param qos   qos
     * @since 2.1.0
     */
    public void subscribes(String[] topic,
                           int[] qos) {
        this.subscribes(null,
                        topic,
                        qos);
    }

    /**
     * Subscribes
     *
     * @param topic topic
     * @since 2.1.0
     */
    public void subscribes(String[] topic) {
        int[] qos = new int[topic.length];
        for (int i = 0; i < topic.length; i++) {
            qos[i] = this.mqttConnector.getDefaultSubQos(this.mqttConnector.getDefaultClientId());
        }
        this.subscribes(null,
                        topic,
                        qos);
    }

    /**
     * Subscribes
     *
     * @param clientId client id
     * @param topics   topics
     * @param qos      qos
     * @since 2.1.0
     */
    public void subscribes(String clientId, String[] topics, int[] qos) {
        IMqttAsyncClient mqttAsyncClient = Objects.requireNonNull(this.mqttConnector.getClientById(clientId),
                                                                   String.format("客户端：%s，不存在", clientId));
        try {
            mqttAsyncClient.subscribe(topics, qos);
        } catch (MqttException e) {
            log.error("订阅topic失败：{}", e.getMessage());
        }
    }

    /**
     * Un subscribe
     *
     * @param topic topic
     * @since 2.1.0
     */
    public void unSubscribe(String[] topic) {
        this.unSubscribe(null, topic);
    }

    /**
     * Un subscribe
     *
     * @param clientId client id
     * @param topic    topic
     * @since 2.1.0
     */
    public void unSubscribe(String clientId,
                            String[] topic) {
        IMqttAsyncClient iMqttAsyncClient = Objects.requireNonNull(this.mqttConnector.getClientById(clientId),
                                                                   String.format("客户端：%s，不存在", clientId));
        try {
            iMqttAsyncClient.unsubscribe(topic);
        } catch (MqttException e) {
            log.error("消息取消订阅失败：{}", e.getMessage());
        }
    }

    /**
     * Disconnected
     *
     * @since 2.1.0
     */
    public void disconnected() {
        this.disconnected(this.mqttConnector.getDefaultClientId());
    }

    /**
     * Disconnected
     *
     * @param clientId client id
     * @since 2.1.0
     */
    public void disconnected(String clientId) {
        IMqttAsyncClient mqttAsyncClient = Objects.requireNonNull(this.mqttConnector.getClientById(clientId),
                                                                  String.format("客户端：%s，不存在", clientId));
        try {
            mqttAsyncClient.disconnect();
        } catch (MqttException e) {
            log.error("mqtt客户端离线失败：{}", e.getMessage());
        }
    }

    /**
     * To msg
     *
     * @param qos qos
     * @param msg msg
     * @return the mqtt message
     * @since 2.1.0
     */
    private MqttMessage toMsg(int qos, byte[] msg) {
        MqttMessage message = new MqttMessage();
        message.setPayload(msg);
        message.setQos(qos);
        message.setId(RandomUtils.nextInt());
        return message;
    }

}
