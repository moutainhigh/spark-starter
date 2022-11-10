package info.spark.start.mqtt.core.provider;

import info.spark.start.mqtt.core.annotation.Payload;
import info.spark.start.mqtt.core.callback.MqttClientCallBackHolder;
import info.spark.start.mqtt.core.common.MqttStartConstName;
import info.spark.start.mqtt.core.converter.MqttConverterService;
import info.spark.start.mqtt.core.entity.MqttClientMessage;
import info.spark.starter.basic.util.StringPool;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.17 09:40
 * @since 2.1.0
 */
@Data
@Builder
@Slf4j
public class ParamsHolder {

    /** Target bean */
    private Object targetBean;

    /** Topics */
    private List<TopicPair> topics;

    /** Clients */
    private List<String> clients;

    /** Method */
    private Method method;

    /** Param models */
    private List<ParamModel> paramModels;

    /** Qos */
    private int[] qos;


    /**
     * Match
     *
     * @param clientId client id
     * @param topic    topic
     * @return the boolean
     * @since 2.1.0
     */
    public boolean match(String clientId, String topic) {
        boolean matchClient = this.clients.contains(clientId);
        if (matchClient) {
            return this.topics.stream().anyMatch(clientTopic -> MqttTopicMatcher.match(topic, clientTopic));
        }
        return false;
    }

    /**
     * Invoke
     *
     * @param clientId    client id
     * @param topic       topic
     * @param mqttMessage mqtt message
     * @since 2.1.0
     */
    public void invoke(String clientId,
                       String topic,
                       MqttMessage mqttMessage) {
        try {
            this.method.invoke(targetBean, transformParams(clientId, topic, mqttMessage));
        } catch (InvocationTargetException e) {
            log.error("topic：{}，消息处理失败：{}", topic, e.getTargetException().getMessage());
        } catch (Exception e) {
            log.error("topic监听方法执行异常：", e);
        }
    }

    /**
     * Transform params
     *
     * @param clientId    client id
     * @param topic       topic
     * @param mqttMessage mqtt message
     * @return the object [ ]
     * @since 2.1.0
     */
    private Object[] transformParams(String clientId,
                                         String topic,
                                         MqttMessage mqttMessage) {
        return this.paramModels.stream().map(paramModel -> {
            Class<?> type = paramModel.getType();
            //处理paylod中数据
            Object value = null;
            List<Converter<Object, Object>> converters = paramModel.getConverters();
            if (type == MqttMessage.class) {
                value = mqttMessage;
            } else if (type == MqttClientMessage.class) {
                MqttClientMessage message = new MqttClientMessage();
                message.setTopic(topic);
                message.setPayload(new String(mqttMessage.getPayload()));
                message.setClientId(clientId);
                message.setQos(mqttMessage.getQos());
                value = message;
            } else if (paramModel.sign && mqttMessage != null) {
                value = MqttConverterService.getSharedInstance().fromBytes(mqttMessage.getPayload(), type, converters);
            } else if (!paramModel.sign && MqttStartConstName.TOPIC.equals(paramModel.name)) {
                value = topic;
            } else if (!paramModel.sign && MqttStartConstName.CLIENT_ID.equals(paramModel.name)) {
                value = clientId;
            }
            if (value == null) {
                if (paramModel.isRequire) {
                    throw new NullPointerException();
                }
                value = paramModel.defaultValue;
            }
            return value;
        }).toArray(Object[]::new);
    }


    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 10:09
     * @since 2.1.0
     */
    @Data
    @Builder
    @Slf4j
    public static class ParamModel {

        /** Sign */
        private boolean sign; //标记当前参数是否为消息内容
        /** Is require */
        private boolean isRequire;
        /** Default value */
        private Object defaultValue;
        /** Name */
        private String name;
        /** Type */
        private Class<?> type;
        /** Converters */
        private LinkedList<Converter<Object, Object>> converters;

        /**
         * 读取方法中的参数，并且返回参数的模型，包括设置默认值等
         *
         * @param method method
         * @return the linked list
         * @since 2.1.0
         */
        public static LinkedList<ParamModel> of(Method method) {
            LinkedList<ParamModel> paramModels = new LinkedList<>();
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                ParamModelBuilder modelBuilder = ParamModel.builder();
                Class<?> type = parameter.getType();
                Object defaultValue = defaultValue(type);
                Payload payload = parameter.getAnnotation(Payload.class);
                if (!Objects.isNull(payload)) {
                    modelBuilder.sign = true;
                    modelBuilder.isRequire = payload.require();
                    modelBuilder.converters = toConverters(payload.converter());
                }
                modelBuilder.name = parameter.getName();
                modelBuilder.type = type;
                modelBuilder.defaultValue = defaultValue;
                paramModels.add(modelBuilder.build());
            }
            return paramModels;
        }

        /**
         * 获取到注解中的转换器
         *
         * @param classes classes
         * @return the linked list
         * @since 2.1.0
         */
        public static LinkedList<Converter<Object, Object>> toConverters(Class<? extends Converter<?, ?>>[] classes) {
            if (classes == null || classes.length <= 0) {
                return null;
            } else {
                LinkedList<Converter<Object, Object>> converters = new LinkedList<>();
                for (Class<? extends Converter<?, ?>> converterClass : classes) {
                    try {
                        converters.add((Converter<Object, Object>) converterClass.getDeclaredConstructor().newInstance());
                    } catch (Exception e) {
                        log.error("converter 实例化失败：{}，原因：{}", converterClass, e.getMessage());
                    }
                }
                return converters;
            }
        }

        /**
         * Default value
         *
         * @param type type
         * @return the object
         * @since 2.1.0
         */
        private static Object defaultValue(Class<?> type) {
            if (type.isPrimitive()) {
                if (type == boolean.class) {
                    return false;
                }
                if (type == char.class) {
                    return (char) 0;
                }
                if (type == byte.class) {
                    return (byte) 0;
                }
                if (type == short.class) {
                    return (short) 0;
                }
                if (type == int.class) {
                    return 0;
                }
                if (type == long.class) {
                    return 0L;
                }
                if (type == float.class) {
                    return 0.0f;
                }
                if (type == double.class) {
                    return 0.0d;
                }
            }
            return null;
        }

    }


    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.20 09:09
     * @since 2.1.0
     */
    @Data
    @Builder
    @Slf4j
    public static class TopicPair {

        /** topic */
        private String topic;

        /** 真实topic值（如果不是占位符，那么这里就为空） */
        private String realityTopic;


        /**
         * Of
         *
         * @param mqttClientCallBackHolder mqtt client call back holder
         * @param topics                   topics
         * @return the list
         * @since 2.1.0
         */
        public static List<TopicPair> of(MqttClientCallBackHolder mqttClientCallBackHolder,
                                         List<String> topics) {
            if (CollectionUtils.isEmpty(topics)) {
                throw new IllegalArgumentException("topics不能为空");
            }
            return topics.stream().map(topic -> {
                TopicPairBuilder topicPairBuilder = TopicPair.builder().topic(topic);
                boolean startsWith = topic.startsWith(StringPool.DOLLAR_LEFT_BRACE);
                //如果是占位符那么 realityTopic 就不会为空
                if (startsWith) {
                    String value = mqttClientCallBackHolder.parseConfig(topic);
                    topicPairBuilder
                        .realityTopic(value);
                }
                return topicPairBuilder.build();
            }).collect(Collectors.toList());
        }

    }


}
