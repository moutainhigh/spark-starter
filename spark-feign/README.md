# 简介

此模块是使用 `spark-starter-agent` 的 demo 应用, `spark-starter-agent` 对 Controller 层进行了统一封装,
简化传统 SSM 框架中的 Controller 层的代码, 将 Controller 请求转发的逻辑使用 @ApiService 和 @ApiServiceMethod 进行了包装, 调用时使用 apiName 和 version 来执行指定的业务逻辑.

## 构建

```
mvn install
```

## 模块

```
├── spark-example-feign
│  └── spark-example-feign-customer              # 集成 spark-feign-adapter 的例子
├── spark-feign-adapter                           # 业务端集成此依赖进行 Feign 调用
└── spark-feign-spring-boot
    ├── spark-feign-spring-boot-autoconfigure     # Feign Client 自动装配模块
    ├── spark-feign-spring-boot-core              # Feign Client 核心模块
    ├── spark-feign-spring-boot-sample            # 例子
    └── spark-feign-spring-boot-starter           # starter 模块
```
## 集成

## 配置

## 说明

## 使用方式

