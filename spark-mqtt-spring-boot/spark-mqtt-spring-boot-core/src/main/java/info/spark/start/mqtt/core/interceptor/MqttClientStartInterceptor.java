package info.spark.start.mqtt.core.interceptor;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * <p>Description: mqtt客户端拦截器 </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 11:00
 * @since 2.1.0
 */
public interface MqttClientStartInterceptor {

    /**
     * Before
     *
     * @param clientId           client id
     * @param mqttConnectOptions mqtt connect options
     * @since 2.1.0
     */
    void before(String clientId, MqttConnectOptions mqttConnectOptions);

    /**
     * After
     *
     * @param mqttAsyncClient mqtt async client
     * @since 2.1.0
     */
    void after(IMqttAsyncClient mqttAsyncClient);
}
