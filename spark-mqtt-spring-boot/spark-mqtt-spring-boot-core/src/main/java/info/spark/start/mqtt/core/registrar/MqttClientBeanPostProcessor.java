package info.spark.start.mqtt.core.registrar;

import info.spark.start.mqtt.core.annotation.MqttClient;
import info.spark.start.mqtt.core.annotation.MqttSubscribe;
import info.spark.start.mqtt.core.callback.MqttCallBackHandler;
import info.spark.start.mqtt.core.callback.MqttClientCallBackHolder;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.Method;

import lombok.AllArgsConstructor;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 15:17
 * @since 2.1.0
 */
@AllArgsConstructor
public class MqttClientBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    /** Mqtt client call back holder */
    private final MqttClientCallBackHolder mqttClientCallBackHolder;

    /**
     * Post process before instantiation
     *
     * @param beanClass bean class
     * @param beanName  bean name
     * @return the object
     * @throws BeansException beans exception
     * @since 2.1.0
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    /**
     * Post process before initialization
     *
     * @param bean     bean
     * @param beanName bean name
     * @return the object
     * @throws BeansException beans exception
     * @since 2.1.0
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (MqttCallBackHandler.class.isAssignableFrom(beanClass)) {
            MqttClient annotation = beanClass.getAnnotation(MqttClient.class);
            this.mqttClientCallBackHolder.add(annotation.value(), (MqttCallBackHandler) bean);
        }
        //处理方法中 @MqttSubscribe 注解
        Method[] methods = beanClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(MqttSubscribe.class)) {
                this.mqttClientCallBackHolder.ofParams(bean, method);
            }
        }
        return bean;
    }

    /**
     * Post process after initialization
     *
     * @param bean     bean
     * @param beanName bean name
     * @return the object
     * @throws BeansException beans exception
     * @since 2.1.0
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
