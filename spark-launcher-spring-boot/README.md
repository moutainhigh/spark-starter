# Spark Launcher Starter

## 项目简介

v5 应用的底层 starter 组件, 提供了简化的启动类(`SparkStarter`), 通过对应用生命周期内的各种事件的监听以实现自定义逻辑.

## 功能特性

## 核心类介绍

1. `RunningType`: 应用启动类型注解, 如果是 Dubbo 服务, 使用 `ApplicationType.SERVICE` 即可, 可以减少很多自动装配逻辑;
2. `RangeRandomValuePropertySource`: 扩展 `RandomValuePropertySource` 以支持 range.random.int(min, max) 和 range.random.key(字符串长度), 可用于随机端口(一个范围内,
   如果端口被占用, 可自动使用下一个端口, 直到可用), 原来的 ${random.uuid} 只支持 32 位的小写字母和数字的字符串, 这里扩展为有大写字母且长度可自定义, 可用于自动生成密钥的业务
3. `DnsCacheListener`: 在配置初始化完成后加载 dns 配置, 优先级必须设置为最高, 否则连接不了 Nacos;
4. `SparkLauncherListener`: 核心事件监听处理:
    1. `onApplicationStartingEvent`: 启动类检查, 强制启动类继承 `SparkStarter`, 否则一些功能无法使用;
    2. `onApplicationEnvironmentPreparedEvent`: 加载完配置后初始化 `ConfigKit` 并将应用类型写入到 JVM 环境变量.
    3. `onWebServerInitializedEvent`: web 应用相关的组件初始化完成后, 获取 web 应用端口号并设置到环境变量中, 用于在启动完成后输出端口信息;
    4. `onApplicationStartedEvent`: 启动完成后初始化 `ThreadUtils` 的线程池;

5. `StarterInfoRunner`: 应用启动完成后输出应用信息;
6. `SubLauncherInitiation`: 主要作用:

    1. 检查应用中所有的 `SerializeEnum` 实现是否存在相同的枚举 value;
    2. 配置全局默认配置:
        1. 存在相同的 bean name 时是否允许覆写;
        2. 设置 pid 文件的生成路径;
        3. 设置 jasypt 默认加密验证;
        4. `spark-plugin/profile/spring.profiles.active` 文件处理;

7. `SparkStarter`: 应用启动类的父类,
8. `SparkApplication`: Spring Boot 启动逻辑封装, 通过 SPI 从各个组件获取默认配置, 着重处理不同启动方式下 `applicationName` 的获取方式.

## 项目结构

## 集成使用

## 配置说明

## 注意事项

## 常见问题

**应用启动后注册到 Nacos 的服务名与 pom.xml 的 package.name 不一致**

这种问题一般都是 bootstrap.yml 中的 `${pakcage.name}` 没有被正确编译(可查看 target/classes/bootstrap.yml 验证), 重新编译项目接口.

补充: `info.spark.starter.launcher.SparkApplication#loadMainProperties`: 获取 applicationName 的逻辑.

## 后期计划
