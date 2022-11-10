# Spark MQTT Starter

## 项目简介
主要目的用于封装mqtt客户端相关的逻辑，统一代码逻辑

## 功能特性
支持注解的方式订阅相关topic、支持方法级别的监听topic、支持动态参数、支持订阅$SYS系统级别topic，统一入口

## 核心类介绍
MqttConnector：其中包含了创建mqtt客户端的相关逻辑代码，连接mqtt的相关逻辑代码
ParamsHolder：参数持有器，主要持有方法的入参模型
MqttMessageProvider：提供订阅发送数据相关的api
MqttClientCallBackHolder：方法级别模型持有器，以及回调函数的持有器
MqttClientBeanPostProcessor：用于处理 @MqttClient、@MqttSubscribe注解标记的类以及方法，然后处理入参的模型存入到 MqttClientCallBackHolder
MqttCallBackRegistrar：@MqttClient 注解标记的类注册器
MqttConverterService：参数转换器

## 项目结构
- annotation：存放注解
- callback：回调定义存放处
- common：通用工具类
- connector：连接器定义的模块以及定时重联任务
- converter：定义的参数转换类
- entity：模型定义
- interceptor：拦截器定义
- properties：配置文件定义
- provider：提供mqtt客户端上下文、topic的匹配工具类
- registrar：mqtt注解扫描类

## 集成使用
配置文件

```yml

spark:
  mqtt:
    clients:
      - url: tcp://${spring.profiles.active}.mqtt.server:1883
        clientId: spark-collector-iot-${spring.profiles.active}-a # 根据端口号进行区分多实例服务
        username: admin
        password: public
  iot:  #以下根据业务需求进行订阅，主要用于业务上取值
    mqtt:
      topic:
          old: test_topic
```

```java
@MqttClient("${spark.mqtt.clients[0].clientId}")
@Slf4j
public class MqttMessageHandler implements MqttCallBackHandler {

    @Override
    public void messageArrived(MqttClientMessage mqttClientMessage) {
        log.info("数据接收：{}", mqttClientMessage)
    }

    @MqttSubscribe(value = {"${spark.iot.mqtt.topic.old}"}, qos = {1}, topicType = TopicType.NORMAL)
    public void sub(MqttClientMessage mqttClientMessage) {
        log.info("数据接收：{}", mqttClientMessage)
    }
}
```

## 配置说明

```yml
spark:
  mqtt:
    clients:
      - url: tcp://${spring.profiles.active}.mqtt.server:1883 # ip地址
        clientId: spark-collector-iot-${spring.profiles.active}-a # 根据端口号进行区分多实例服务
        username: admin #用户名
        password: public #密码
```

## 注意事项

注意：在 MqttClientStartInterceptor 中不需要在引用 MqttMessageProvider ，否者会出现循环依赖的问题

## 常见问题

## 后期计划

- [ ] 代码结构优化
- [ ] 修改客户端失败重连改为时间轮方式进行连接
- [ ] 重构MqttConnector中代码逻辑
- [ ] 修改方法级别注解的处理逻辑以及代码结构
