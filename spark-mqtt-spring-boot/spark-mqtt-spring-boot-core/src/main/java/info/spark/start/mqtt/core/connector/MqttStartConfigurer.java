package info.spark.start.mqtt.core.connector;

import info.spark.start.mqtt.core.annotation.MqttInterceptor;
import info.spark.start.mqtt.core.callback.MqttClientCallBackHolder;
import info.spark.start.mqtt.core.interceptor.MqttClientStartInterceptor;
import info.spark.start.mqtt.core.properties.MqttProperties;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * <p>Description:  mqtt启动上下文 </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 11:02
 * @since 2.1.0
 */
@Data
public abstract class MqttStartConfigurer {

    /** Mqtt properties */
    private MqttProperties mqttProperties;

    /** Mqtt client interceptors */
    private Map<String, List<MqttClientStartInterceptor>> mqttClientInterceptors = new HashMap<>();

    /** Mqtt client call back holder */
    private MqttClientCallBackHolder mqttClientCallBackHolder;

    /**
     * Mqtt start configurer
     *
     * @param mqttProperties           mqtt properties
     * @param mqttClientCallBackHolder mqtt client call back holder
     * @param mqttClientInterceptors   mqtt client interceptors
     * @since 2.1.0
     */
    public MqttStartConfigurer(MqttProperties mqttProperties,
                               MqttClientCallBackHolder mqttClientCallBackHolder,
                               List<MqttClientStartInterceptor> mqttClientInterceptors) {
        this.mqttProperties = mqttProperties;
        mqttClientInterceptors.forEach(mqttClientInterceptor -> {
            MqttInterceptor annotation = mqttClientInterceptor.getClass().getAnnotation(MqttInterceptor.class);
            String[] clientIds = annotation.value();
            if (clientIds.length > 0) {
                for (String clientId : clientIds) {
                    clientId = mqttClientCallBackHolder.parseConfig(clientId);
                    List<MqttClientStartInterceptor> clientInterceptors = this.mqttClientInterceptors.get(clientId);
                    if (CollectionUtils.isEmpty(clientInterceptors)) {
                        this.mqttClientInterceptors.putIfAbsent(clientId, Collections.singletonList(mqttClientInterceptor));
                    } else {
                        clientInterceptors.add(mqttClientInterceptor);
                    }
                }
            }
        });
    }

    /**
     * Before
     *
     * @param clientId           client id
     * @param mqttConnectOptions mqtt connect options
     * @since 2.1.0
     */
    protected void before(String clientId, MqttConnectOptions mqttConnectOptions) {
        List<MqttClientStartInterceptor> mqttClientInterceptors = this.mqttClientInterceptors.get(clientId);
        if (CollectionUtils.isEmpty(mqttClientInterceptors)) {
            return;
        }
        mqttClientInterceptors.forEach(interceptor -> interceptor.before(clientId, mqttConnectOptions));
    }

    /**
     * After
     *
     * @param mqttAsyncClient mqtt async client
     * @since 2.1.0
     */
    protected void after(IMqttAsyncClient mqttAsyncClient) {
        String clientId = mqttAsyncClient.getClientId();
        List<MqttClientStartInterceptor> mqttClientInterceptors = this.mqttClientInterceptors.get(clientId);
        if (CollectionUtils.isEmpty(mqttClientInterceptors)) {
            return;
        }
        mqttClientInterceptors.forEach(interceptor -> interceptor.after(mqttAsyncClient));
    }


    /**
     * Create mqtt client
     *
     * @param clientId           client id
     * @param mqttConnectOptions mqtt connect options
     * @return the mqtt async client
     * @throws MqttException mqtt exception
     * @since 2.1.0
     */
    public IMqttAsyncClient createMqttClient(String clientId, MqttConnectOptions mqttConnectOptions) throws MqttException {
        return new MqttAsyncClient(mqttConnectOptions.getServerURIs()[0], clientId, new MemoryPersistence());
    }

}
