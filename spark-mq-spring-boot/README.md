# Spark MQ Starter

## 项目简介

此项目为提供了一套通用接口, 简化发送与接收处理消息的逻辑, 目前集成了 Kafka 和 RocketMQ, 后期可使用 [Apache Pulsar](https://pulsar.apache.org/) 代替.

## 功能特性

## 核心类介绍

```
.
├── IMessageType.java                       # 消息类型接口
├── MessageConstant.java                    # 消息常量类
├── RoleType.java                           # 应用角色
├── annotation
│   └── MessageHandler.java                 # 消息处理注解, 用在 `AbstractMessageNotifyHandler` 的子类上, 会自动注入到 IoC 中, 用于处理特定事件
├── consumer
│   ├── AbstractConsumer.java               # 消费者基类, 用于分发消息到具体的 handler 处理器上
│   ├── AbstractMessageHandler.java         # 消息处理器基类, 由子类继承后处理具体某类消息
│   └── AbstractMessageNotifyHandler.java   # 消息通知处理器基类, 用于处理消息通知
├── entity
│   ├── AbstractMessage.java                # 消息基类
│   ├── Distribute.java                     # 消息分发实体
│   ├── MessageExtAdapter.java              # 消息元数据
│   ├── MessageWrapper.java                 # 消息包装类
│   ├── MqResults.java                      # 消息 offset 信息
│   ├── SendResultInfo.java                 # 消息发送的一些信息
│   ├── TestHandler.java                    # 内置测试使用的消息处理器
│   └── TestMessage.java                    # 测试消息实体
├── exception
│   ├── MessageException.java
│   └── NotSupportMethodException.java
├── provider
│   ├── AbstractProvider.java               # 向外暴露的发送消息接口
│   └── ErrorMqProvider.java
└── support
    ├── ConsumerCacheMaps.java
    ├── MessageDispatcher.java              # 消息分发接口
    ├── MessageLoader.java                  # 根据消息类型从缓存中获取消息处理器
    ├── MessageProcessFactoriesLoader.java  # 消息类型与消息处理器的
    ├── MessageProcessManager.java          # 应用监听器, 在接收到 ContextRefreshedEvent 事件后处理应用内 @MessageHandler 标识的类, 主要是建立消息类型与消息处理器的映射关系
    └── MessageRegister.java                # 将消息处理器与消息类型的映射关系注册到缓存中
```

## 项目结构

## 集成使用

## 配置说明

## 注意事项

## 常见问题

## 后期计划

- [ ] 消息增加 id
- [ ] 实体类型的消息 traceId 透传问题处理;
- [ ] 增加配置项
