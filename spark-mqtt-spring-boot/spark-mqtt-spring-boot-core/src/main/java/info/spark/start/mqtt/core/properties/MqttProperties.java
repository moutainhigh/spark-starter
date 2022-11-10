package info.spark.start.mqtt.core.properties;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 10:32
 * @since 2.1.0
 */
@Data
@Slf4j
public class MqttProperties {

    /** 默认开启 */
    private boolean enable = true;
    /** Clients */
    private Map<String, MqttConnectorProperties> clients;

    /**
     * Mqtt properties
     *
     * @param enable  enable
     * @param clients clients
     * @since 2.1.0
     */
    public MqttProperties(boolean enable, List<MqttConnectorProperties> clients) {
        this.clients = clients.stream().collect(Collectors.toMap(MqttConnectorProperties::getClientId,
                                                                 m -> m));
    }

    /**
     * For each
     *
     * @param optionsBiConsumer options bi consumer
     * @since 2.1.0
     */
    public void forEach(BiConsumer<String, MqttConnectOptions> optionsBiConsumer) {
        if (clients != null && clients.size() > 0) {
            clients.forEach((id, prop) -> {
                log.info("mqtt client 开始创建：{}", prop);
                MqttConnectOptions mqttConnectOptions = toOptions(id);
                optionsBiConsumer.accept(id, mqttConnectOptions);
            });
        }
    }

    /**
     * To options
     *
     * @param clientId client id
     * @return the mqtt connect options
     * @since 2.1.0
     */
    private MqttConnectOptions toOptions(String clientId) {
        MqttConnectorProperties mqttConnectorProperties = clients.get(clientId);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        if (mqttConnectorProperties != null) {
            mqttConnectOptions.setUserName(mqttConnectorProperties.username);
            mqttConnectOptions.setPassword(mqttConnectorProperties.password.toCharArray());
            mqttConnectOptions.setServerURIs(new String[] {mqttConnectorProperties.getUrl()});
            mqttConnectOptions.setConnectionTimeout(mqttConnectorProperties.connectionTimeout);
            mqttConnectOptions.setKeepAliveInterval(mqttConnectorProperties.keepAliveInterval);
            mqttConnectOptions.setCleanSession(mqttConnectorProperties.cleanSession);
            mqttConnectOptions.setAutomaticReconnect(mqttConnectOptions.isAutomaticReconnect());
            mqttConnectOptions.setExecutorServiceTimeout(mqttConnectOptions.getExecutorServiceTimeout());
            mqttConnectOptions.setMaxReconnectDelay(mqttConnectOptions.getMaxReconnectDelay());
            mqttConnectOptions.setMaxInflight(mqttConnectorProperties.getMaxInflight());
        }
        return mqttConnectOptions;
    }


    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.16 10:40
     * @since 2.1.0
     */
    @Data
    public static class MqttConnectorProperties {
        /** Url */
        private String url;
        /** Client id */
        private String clientId;
        /** 用户名 */
        private String username;
        /** 密码 */
        private String password;
        /** 连接超时时间 防止 Timed out as no activity */
        private int connectionTimeout = 0;
        /** 默认推送qos */
        private int defaultPublishQos = 0;
        /** 默认订阅qos */
        private int defaultSubscribeQos = 0;
        /** 最大重连等待时间 （秒） */
        private Integer maxReconnectDelay = 5;
        /** 最大重连次数 */
        private Integer maxReconnectCount = 5;
        /** 周期 */
        private int keepAliveInterval = MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT;
        /** 是否清除会话 */
        private boolean cleanSession = false;
        /** 是否自动重连 */
        private boolean automaticReconnect = true;
        /** 发送超时时间 */
        private int executorServiceTimeout = 1;
        /** 默认消息超时时间，超过当前时间的消息不在接收 （秒） 默认10分钟 */
        private Integer defaultMsgTimeout = 600;
        /** 数据是否加密 */
        private boolean encryption = false;
        /** 设置最大飞行窗口目前为 100 */
        private int maxInflight = 100;
    }

}
