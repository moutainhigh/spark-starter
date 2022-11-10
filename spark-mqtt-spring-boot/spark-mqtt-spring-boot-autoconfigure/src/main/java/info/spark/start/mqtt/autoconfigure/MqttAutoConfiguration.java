package info.spark.start.mqtt.autoconfigure;

import info.spark.start.mqtt.core.callback.MqttClientCallBackHolder;
import info.spark.start.mqtt.core.common.MqttStartConstName;
import info.spark.start.mqtt.core.connector.Connector;
import info.spark.start.mqtt.core.connector.MqttConnector;
import info.spark.start.mqtt.core.connector.MqttStartConfigurer;
import info.spark.start.mqtt.core.converter.MqttConverterService;
import info.spark.start.mqtt.core.interceptor.MqttClientStartInterceptor;
import info.spark.start.mqtt.core.properties.MqttProperties;
import info.spark.start.mqtt.core.provider.MqttMessageProvider;
import info.spark.start.mqtt.core.registrar.MqttCallBackRegistrar;
import info.spark.starter.common.start.SparkAutoConfiguration;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

import javax.annotation.Resource;

/**
 * <p>Description:  mqtt自动配置类 </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 10:27
 * @since 2.1.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MqttAutoProperties.class)
@ConditionalOnProperty(prefix = MqttAutoProperties.PREFIX, name = MqttStartConstName.ENABLE, matchIfMissing = true)
@Import(MqttCallBackRegistrar.class)
public class MqttAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Mqtt auto configuration
     *
     * @param beanFactory bean factory
     * @since 2.1.0
     */
    public MqttAutoConfiguration(ListableBeanFactory beanFactory) {
        //将容器中转换器，注册到 MqttConverterService 中
        MqttConverterService.addConfigure(MqttConverterService.getSharedInstance(), beanFactory);
    }

    /**
     * Mqtt client call back holder
     *
     * @return the mqtt client call back holder
     * @since 2.1.0
     */
    @Bean
    public MqttClientCallBackHolder mqttClientCallBackHolder() {
        return new MqttClientCallBackHolder();
    }

    /**
     * Mqtt connector
     *
     * @param mqttProperties           mqtt properties
     * @param mqttStartConfigurer      mqtt start configurer
     * @param mqttClientCallBackHolder mqtt client call back holder
     * @return the mqtt connector
     * @since 2.1.0
     */
    @Bean
    @ConditionalOnMissingBean(Connector.class)
    public MqttConnector mqttConnector(MqttAutoProperties mqttProperties,
                                       MqttStartConfigurer mqttStartConfigurer,
                                       MqttClientCallBackHolder mqttClientCallBackHolder) {
        MqttProperties properties = new MqttProperties(mqttProperties.isEnable(), mqttProperties.getClients());
        return new MqttConnector(properties,
                                 mqttStartConfigurer,
                                 mqttClientCallBackHolder);
    }

    /**
     * Mqtt start configurer
     *
     * @param mqttProperties           mqtt properties
     * @param mqttClientCallBackHolder mqtt client call back holder
     * @param mqttClientInterceptors   mqtt client interceptors
     * @return the mqtt start configurer
     * @since 2.1.0
     */
    @Bean
    @ConditionalOnBean(Connector.class)
    public MqttStartConfigurer mqttStartConfigurer(MqttAutoProperties mqttProperties,
                                                   MqttClientCallBackHolder mqttClientCallBackHolder,
                                                   List<MqttClientStartInterceptor> mqttClientInterceptors) {
        MqttProperties properties = new MqttProperties(mqttProperties.isEnable(), mqttProperties.getClients());
        return new MqttStartConfigurer(properties,
                                       mqttClientCallBackHolder,
                                       mqttClientInterceptors) {
        };
    }

    /**
     * Mqtt msg sender
     *
     * @return the mqtt msg sender
     * @since 2.1.0
     */
    @Bean
    @ConditionalOnBean(Connector.class)
    public MqttMessageProvider mqttMessageProvider() {
        return new MqttMessageProvider();
    }

    /**
     * Mqtt start runner
     *
     * @return the mqtt start runner
     * @since 2.1.0
     */
    @Bean
    public MqttStartRunner mqttStartRunner() {
        return new MqttStartRunner();
    }


    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.27 17:02
     * @since 2.1.0
     */
    public static class MqttStartRunner implements ApplicationRunner {

        /** Mqtt connector */
        @Resource
        private Connector mqttConnector;

        /**
         * Run
         *
         * @param args args
         * @throws Exception exception
         * @since 2.1.0
         */
        @Override
        public void run(ApplicationArguments args) throws Exception {
            mqttConnector.start();
        }
    }
}
