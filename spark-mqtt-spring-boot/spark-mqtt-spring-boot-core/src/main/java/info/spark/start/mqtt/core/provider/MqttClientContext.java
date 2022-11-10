package info.spark.start.mqtt.core.provider;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 16:27
 * @since 2.1.0
 */
public class MqttClientContext {

    /** I_MQTT_ASYNC_CLIENT_THREAD_LOCAL */
    public static final ThreadLocal<IMqttAsyncClient> I_MQTT_ASYNC_CLIENT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Get
     *
     * @return the mqtt async client
     * @since 2.1.0
     */
    public static IMqttAsyncClient get() {
        return I_MQTT_ASYNC_CLIENT_THREAD_LOCAL.get();
    }

    /**
     * Remove
     *
     * @since 2.1.0
     */
    public static void remove() {
        I_MQTT_ASYNC_CLIENT_THREAD_LOCAL.remove();
    }

    public static void set(IMqttAsyncClient mqttAsyncClient) {
        I_MQTT_ASYNC_CLIENT_THREAD_LOCAL.set(mqttAsyncClient);
    }
}
