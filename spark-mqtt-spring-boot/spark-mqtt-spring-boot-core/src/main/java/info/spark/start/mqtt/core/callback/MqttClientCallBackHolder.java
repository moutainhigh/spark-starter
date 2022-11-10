package info.spark.start.mqtt.core.callback;

import info.spark.start.mqtt.core.annotation.MqttClient;
import info.spark.start.mqtt.core.annotation.MqttSubscribe;
import info.spark.start.mqtt.core.common.TopicProcessor;
import info.spark.start.mqtt.core.common.TopicType;
import info.spark.start.mqtt.core.entity.MqttClientMessage;
import info.spark.start.mqtt.core.provider.ParamsHolder;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.util.StringUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 15:27
 * @since 2.1.0
 */
@Slf4j
public class MqttClientCallBackHolder implements EnvironmentAware {


    /** CALL_BACK_INTERFACE_MAP */
    public static final Map<String, MqttCallBackHandler> CALL_BACK_INTERFACE_MAP;

    /** METHOD_HOLDERS */
    public static final List<ParamsHolder> METHOD_HOLDERS;

    /** configurableEnvironment */
    public static ConfigurableEnvironment configurableEnvironment;

    static {
        CALL_BACK_INTERFACE_MAP = new ConcurrentHashMap<>();
        METHOD_HOLDERS = new CopyOnWriteArrayList<>();
    }

    /**
     * Message arrived
     *
     * @param clientId    client id
     * @param topic       topic
     * @param mqttMessage mqtt message
     * @since 2.1.0
     */
    public void messageArrived(String clientId, String topic, MqttMessage mqttMessage) {
        long count = METHOD_HOLDERS.stream()
            .filter(paramsHolder -> paramsHolder.match(clientId, topic))
            .peek(paramsHolder -> paramsHolder.invoke(clientId, topic, mqttMessage)).count();
        if (count <= 0) {
            try {
                MqttCallBackHandler mqttCallBackInterface =
                    Objects.requireNonNull(CALL_BACK_INTERFACE_MAP.get(clientId),
                                           String.format("mqtt" + " clientId：%s，处理类没有找到", clientId));
                MqttClientMessage mqttClientMessage = MqttClientMessage.builder()
                    .clientId(clientId)
                    .payload(new String(mqttMessage.getPayload()))
                    .qos(mqttMessage.getQos())
                    .topic(topic).build();
                mqttCallBackInterface.messageArrived(mqttClientMessage);
            } catch (Exception e) {
                log.error("消息处理失败：{}", e.getMessage());
            }
        }
    }

    /**
     * Add
     *
     * @param clientId            client id
     * @param mqttCallBackHandler mqtt call back handler
     * @since 2.1.0
     */
    public void add(String clientId,
                    MqttCallBackHandler mqttCallBackHandler) {
        Assert.notNull(clientId, "clientId not null");
        Assert.notNull(mqttCallBackHandler, "mqttCallBackHandler not null");
        CALL_BACK_INTERFACE_MAP.put(parseConfig(clientId), mqttCallBackHandler);
    }


    /**
     * Of params
     *
     * @param bean   bean
     * @param method method
     * @since 2.1.0
     */
    public void ofParams(Object bean, Method method) {
        MqttSubscribe annotation = method.getAnnotation(MqttSubscribe.class);
        String[] clients = annotation.clients();
        if (clients.length <= 0) {
            MqttClient mqttClient = bean.getClass().getAnnotation(MqttClient.class);
            clients = new String[] {parseConfig(mqttClient.value())};
        }
        int[] qos = annotation.qos();
        String[] topics = annotation.value();
        Assert.notEmpty(clients, "订阅客户端id不能为空");
        Assert.notEmpty(topics, "topic不能为空");
        ParamsHolder paramsHolder = ParamsHolder.builder()
            .targetBean(bean)
            .clients(Arrays.asList(clients))
            .topics(ParamsHolder.TopicPair.of(this, Arrays.asList(topics)))
            .method(method)
            .qos(qos)
            .paramModels(ParamsHolder.ParamModel.of(method))
            .build();
        TopicType topicType = annotation.topicType();
        List<ParamsHolder.TopicPair> paramsHolderTopics = paramsHolder.getTopics();
        paramsHolderTopics.forEach(topicPair -> {
            String realityTopic = topicPair.getRealityTopic();
            boolean empty = StringUtils.isEmpty(realityTopic);
            if (!empty) {
                //给topic拼接上斜杠
                topicPair.setRealityTopic(TopicProcessor.processor(realityTopic, topicType));
            } else {
                //给topic拼接上斜杠
                String topic = topicPair.getTopic();
                topicPair.setTopic(TopicProcessor.processor(topic, topicType));
            }
        });
        METHOD_HOLDERS.add(paramsHolder);
    }

    /**
     * Parse config
     *
     * @param config config
     * @return the string
     * @since 2.1.0
     */
    public String parseConfig(String config) {
        Assert.notNull(config, "配置不能为空");
        if (config.startsWith(StringPool.DOLLAR_LEFT_BRACE)) {
            config = configurableEnvironment.resolvePlaceholders(config);
        }
        return config;
    }

    /**
     * Sets environment *
     *
     * @param environment environment
     * @since 2.1.0
     */
    @Override
    public void setEnvironment(@NotNull Environment environment) {
        configurableEnvironment = (ConfigurableEnvironment) environment;
    }
}
