package info.spark.start.mqtt.core.connector;

import info.spark.start.mqtt.core.callback.MqttClientCallBackHolder;
import info.spark.start.mqtt.core.properties.MqttProperties;
import info.spark.start.mqtt.core.provider.MqttClientContext;
import info.spark.start.mqtt.core.provider.ParamsHolder;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 10:49
 * @since 2.1.0
 */
@Data
@Slf4j
public class MqttConnector implements DisposableBean, Connector {

    /** 保存mqtt客户端 */
    private Map<String, IMqttAsyncClient> mqttClientMap;

    /** defaultClientId */
    private Supplier<IMqttAsyncClient> defaultClient;

    /** 默认发布qos */
    private Map<String, Integer> mqttClientPubQos;

    /** 默认订阅qos */
    private Map<String, Integer> mqttClientSubQos;

    /** Mqtt properties */
    private MqttProperties mqttProperties;

    /** Mqtt start configurer */
    private MqttStartConfigurer mqttStartConfigurer;

    /** Mqtt client call back holder */
    private MqttClientCallBackHolder mqttClientCallBackHolder;

    /**
     * Gets client by id *
     *
     * @param clientId client id
     * @return the client by id
     * @since 2.1.0
     */
    public IMqttAsyncClient getClientById(String clientId) {
        if (StringUtils.isEmpty(clientId)) {
            return this.defaultClient.get();
        }
        return Optional.of(this.mqttClientMap.get(clientId)).orElseThrow(NullPointerException::new);
    }

    /**
     * 获取默认客户端id
     *
     * @return the default client id
     * @since 2.1.0
     */
    public String getDefaultClientId() {
        return Objects.requireNonNull(this.defaultClient.get()).getClientId();
    }

    /**
     * 获取默认的QOS
     *
     * @param clientId client id
     * @return the default qos
     * @since 2.1.0
     */
    public Integer getDefaultPubQos(String clientId) {
        return this.mqttClientPubQos.getOrDefault(clientId, 0);
    }

    /**
     * 订阅qos
     *
     * @param clientId client id
     * @return the default qos
     * @since 2.1.0
     */
    public Integer getDefaultSubQos(String clientId) {
        return this.mqttClientSubQos.getOrDefault(clientId, 0);
    }

    /**
     * Mqtt connector
     *
     * @param mqttProperties           mqtt properties
     * @param mqttStartConfigurer      mqtt start configurer
     * @param mqttClientCallBackHolder mqtt client call back holder
     * @since 2.1.0
     */
    public MqttConnector(MqttProperties mqttProperties,
                         MqttStartConfigurer mqttStartConfigurer,
                         MqttClientCallBackHolder mqttClientCallBackHolder) {
        log.info("[MQTT] 初始化: [{}]", MqttConnector.class);
        this.mqttProperties = mqttProperties;
        this.mqttStartConfigurer = mqttStartConfigurer;
        this.mqttClientCallBackHolder = mqttClientCallBackHolder;
        this.mqttClientMap = new ConcurrentHashMap<>(mqttProperties.getClients().size());
        this.mqttClientPubQos = new ConcurrentHashMap<>(mqttProperties.getClients().size());
        this.mqttClientSubQos = new ConcurrentHashMap<>(mqttProperties.getClients().size());
        this.init();
    }

    /**
     * Init
     *
     * @since 2.1.0
     */
    private void init() {
        this.defaultClient = () -> this.mqttClientMap.values().iterator().next();
    }

    /**
     * Start
     *
     * @since 2.1.0
     */
    @Override
    public void start() {
        MqttProperties mqttProperties = this.getMqttProperties();
        if (mqttProperties.isEnable()) {
            mqttProperties.forEach((id, option) -> {
                this.mqttStartConfigurer.before(id, option);
                try {
                    IMqttAsyncClient mqttClient = mqttStartConfigurer.createMqttClient(id, option);
                    this.mqttClientMap.put(id, mqttClient);
                    this.mqttClientPubQos.put(id, mqttProperties.getClients().get(id).getDefaultPublishQos());
                    this.mqttClientSubQos.put(id, mqttProperties.getClients().get(id).getDefaultSubscribeQos());
                    ConnectorTaskExecutor.submitConnect(() -> connect(mqttClient, option));
                } catch (MqttException e) {
                    log.error("mqtt创建失败：{}", e.getMessage());
                }
            });
        }
    }

    /**
     * 执行mqtt连接
     *
     * @param mqttAsyncClient    mqtt async client
     * @param mqttConnectOptions mqtt connect options
     * @since 2.1.0
     */
    public void connect(IMqttAsyncClient mqttAsyncClient, MqttConnectOptions mqttConnectOptions) {
        final String clientId = mqttAsyncClient.getClientId();
        MqttProperties.MqttConnectorProperties mqttConnectorProperties = mqttProperties.getClients().get(clientId);
        Integer maxReconnectCount = mqttConnectorProperties.getMaxReconnectCount();
        Integer maxReconnectDelay = mqttConnectorProperties.getMaxReconnectDelay();
        try {
            mqttAsyncClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectionLost(Throwable cause) {
                    if (!mqttAsyncClient.isConnected()) {
                        log.debug("mqtt client：{}，断开连接，尝试重新连接！", clientId);
                        ConnectorTaskExecutor.submitReconnect(clientId,
                                                              () -> connect(mqttAsyncClient, mqttConnectOptions),
                                                              maxReconnectCount,
                                                              maxReconnectDelay);
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    MqttClientContext.set(mqttAsyncClient);
                    log.debug("clientId：{}，接收到topic：{}，消息执行处理逻辑。。", clientId, topic);
                    MqttConnector.this.mqttClientCallBackHolder.messageArrived(clientId, topic, message);
                    MqttClientContext.remove();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.debug("消息是否接收完成：{}，消息id：{}", token.isComplete(), token.getMessageId());
                }

                @Override
                public void connectComplete(boolean reconnect, String serverUri) {
                    if (reconnect) {
                        ConnectorTaskExecutor.removeReconnect(clientId);
                        try {
                            MqttConnector.this.subscribe(mqttAsyncClient);
                            MqttConnector.this.mqttStartConfigurer.after(mqttAsyncClient);
                        } catch (Exception e) {
                            log.error("mqtt拦截器或订阅topic异常：{}", e.getMessage());
                        }
                    }
                }
            });

            mqttAsyncClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    log.debug("mqtt client:{}，connect success.....", clientId);
                    //连接成功后移除重连线程
                    ConnectorTaskExecutor.removeReconnect(clientId);
                    try {
                        MqttConnector.this.subscribe(mqttAsyncClient);
                        MqttConnector.this.mqttStartConfigurer.after(mqttAsyncClient);
                    } catch (Exception e) {
                        log.error("mqtt拦截器或订阅topic异常：{}", e.getMessage());
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (!mqttAsyncClient.isConnected()) {
                        //连接失败发起重连任务
                        ConnectorTaskExecutor.submitReconnect(clientId,
                                                              () -> connect(mqttAsyncClient, mqttConnectOptions),
                                                              maxReconnectCount,
                                                              maxReconnectDelay);
                    }
                }
            });

        } catch (MqttException e) {
            log.error("mqtt connect error：{}", e.getMessage());
        }
    }


    /**
     * Subscribe
     *
     * @param mqttAsyncClient mqtt async client
     * @since 2.1.0
     */
    public void subscribe(IMqttAsyncClient mqttAsyncClient) {
        String clientId = mqttAsyncClient.getClientId();
        try {
            for (ParamsHolder paramsHolder : MqttClientCallBackHolder.METHOD_HOLDERS) {
                List<String> clients = paramsHolder.getClients();
                boolean match = clients.contains(clientId);
                if (match) {
                    //过滤掉不合理的topic，否则在进行订阅时topic不合理会导致客户端反复重连
                    String[] topics = paramsHolder
                        .getTopics()
                        .stream()
                        .map(topicPair -> StringUtils.isEmpty(topicPair.getRealityTopic()) ? topicPair.getTopic()
                                                                                           : topicPair.getRealityTopic())
                        .filter(topic -> {
                            try {
                                MqttTopic.validate(topic, true);
                            } catch (IllegalArgumentException e) {
                                log.error("topic不合理：{}", e.getMessage());
                                return false;
                            }
                            return true;
                        })
                        .toArray(String[]::new);
                    if (topics.length <= 0) {
                        return;
                    }
                    int[] qos = new int[topics.length];
                    int index = 0;
                    for (String topic : topics) {
                        List<ParamsHolder.TopicPair> paramsHolderTopics = paramsHolder.getTopics();
                        ParamsHolder.TopicPair topicPair =
                            paramsHolderTopics.stream().filter(t -> {
                                String realityTopic = t.getRealityTopic();
                                String tTopic = t.getTopic();
                                return (StringUtils.isEmpty(realityTopic) ? tTopic : realityTopic).equals(topic);
                            }).findFirst().orElse(null);
                        if (topicPair == null) {
                            continue;
                        }
                        int topicQosIndex = paramsHolderTopics.indexOf(topicPair);
                        int qo = paramsHolder.getQos().length - 1 >= topicQosIndex ? paramsHolder.getQos()[topicQosIndex] : 0;
                        qos[index++] = qo;
                    }
                    mqttAsyncClient.subscribe(topics, qos);
                }
            }
        } catch (MqttException e) {
            log.error("订阅topic异常：{}", e.getMessage());
        }
    }

    /**
     * Destroy
     *
     * @throws Exception exception
     * @since 2.1.0
     */
    @Override
    public void destroy() throws Exception {
        this.mqttClientMap.forEach((id, client) -> {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
                client.close();
            } catch (MqttException e) {
                log.error("mqtt close error : {}", e.getMessage());
            }
        });
    }

}
