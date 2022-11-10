# Spark Logsystem Starter

## 项目简介

全局管理应用日志配置, 所有应用服务不再需要添加日志配置文件(如果应用有特殊需求, 也可执行添加, 修改需要加载的日志配置文件名即可, 具体的日志配置可在 「v5 开发手册」查看).

全局配置包括应用运行日志配置文件, 单元测试日志配置文件, 组件日志配置文件, 并基于 sl4j2-simple 实现了 `spark-logsystem-simple` 组件, 主要用于框架内组件或应用内的组件的日志输出.

此组件的核心功能时在日志系统初始化之前将自定义配置注入到容器中, 核心逻辑为 `SparkLoggingListener`.

## 功能特性

1. 统一管理日志配置，日志框架使用性能最好的 log4j2;
2. 生产环境将 System.out 重定向到 log 输出;
3. 动态修改日志等级;

## 核心类介绍

1. `LogSystemAutoConfiguration`: 自动装配动态修改日志相关 bean;
2. `info.spark.starter.logsystem.handler`: 包下所有的类, 主要是自定义配置与日志配置间的映射关系;
3. `SparkLoggingListener`: 在日志系统加载之前, 将自定义配置注入到日志配置文件中, 完成日志自定义配置;

## 项目结构

## 集成使用

请查看 guide 项目

## 配置说明

## 注意事项

## 常见问题

如果执行 sample 时给出下面的错误信息:

```
Error:(5, 43) java: 程序包info.spark.starter.logsystem.factory不存在
Error:(55, 16) java: 找不到符号
  符号:   类 LogStorageFactory
  位置: 类 autoconfigure.info.spark.starter.logsystem.LogSystemAutoConfiguration.LogSystemRecordAutoConfiguration
```

需要先执行 `mvn install`

## 后期计划
