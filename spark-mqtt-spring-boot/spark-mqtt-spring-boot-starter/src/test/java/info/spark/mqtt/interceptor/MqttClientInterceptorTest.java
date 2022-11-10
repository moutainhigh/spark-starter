package info.spark.mqtt.interceptor;

import info.spark.start.mqtt.core.annotation.MqttInterceptor;
import info.spark.start.mqtt.core.interceptor.MqttClientStartInterceptor;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.27 15:25
 * @since 2.1.0
 */
@MqttInterceptor("${spark.mqtt.clients[0].clientId}")
@Slf4j
public class MqttClientInterceptorTest implements MqttClientStartInterceptor {

    /**
     * Before
     *
     * @param clientId           client id
     * @param mqttConnectOptions mqtt connect options
     * @since 2.1.0
     */
    @Override
    public void before(String clientId, MqttConnectOptions mqttConnectOptions) {

    }

    /**
     * After
     *
     * @param mqttAsyncClient mqtt async client
     * @since 2.1.0
     */
    @Override
    public void after(IMqttAsyncClient mqttAsyncClient) {
        try {
            log.info("启动成功后订阅topic信息...........");
            mqttAsyncClient.subscribe("client1_test_topic", 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
