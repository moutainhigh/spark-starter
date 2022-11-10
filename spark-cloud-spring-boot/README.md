# Spark Cloud Starter

## 项目简介

## 功能特性

## 核心类介绍

### 自动装配

1. `SparkNacosBootstrapConfiguration`: 自定义 Nacos 配置获取逻辑, 实现启动时向 Nacos 写入初始化配置;
2. `SparkNacosDiscoveryClientAutoConfiguration`: 启动时吸入应用元数据;

### 核心类

1. `SparkCloudAppStartedListener`: 启动完成后定时更新 Naccos 的应用元数据;
2. `CloudNacosLauncherInitiation`:使用 SPI 写入 Nacos Client 的默认配置; 减少业务端的配置工作;

## 项目结构

## 集成使用

## 配置说明

## 注意事项

因为需要在 Spring Cloud 启动阶段注入自定义逻辑, 因此需要手写 `spring.factories` 文件, 导致注解处理器不再生效, 因此后期如果添加了自动装配类, 记得要修改 `spring.factories` 文件.

## 常见问题

## 后期计划

- [ ] 封装 Sentinel;
- [ ] 封装 seata;
