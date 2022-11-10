package info.spark.mqtt;

import info.spark.start.mqtt.core.annotation.EnableMqttClient;
import info.spark.starter.launcher.SparkStarter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 16:39
 * @since 2.1.0
 */
@SpringBootApplication
@PropertySource(value = "classpath:application.yml")
@EnableMqttClient
public class MqttClientStartTest extends SparkStarter {


}
