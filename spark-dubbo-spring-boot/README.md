# Spark Dubbo Starter

## 项目简介

## 功能特性

## 核心类介绍

### 自动装配

1. `DubboAutoConfiguration`: 启动完成后输出 dubbo 的 url;
   1. `DubboRegistryInvokerRebuildListener` 为了解决注册中心推送新IP的时候, dubbo 服务一直不替换老 IP 或端口的 BUG, 不过官方已经修复此问题, 这个类可以删除了;
   2. `RpcCheck`: 自动生成了 dubbo 连通性检查服务, dubbo 应用在启动完成后通过注册中心找到所有的提供者并调用此服务来检查服务是否可用;


### 核心类

1. `GrayLoadBalance`: 自定义 dubbo 负载均衡策略, 根据配置来选择服务;
2. `ConsumerExceptionFilter`: 自定义异常处理拦截器, 主要是转化 `ServiceInternalException` 和 `BasicException`;
3. `CrossJvmParameterPassingFilter`: 使用 RpcContext 透明的传递参数;
4. `DubboRegistryCheckListener`: 自动生成 `RpcCheck` 服务;
5. `DubboLauncherInitiation`: 修改了 dubbo 的一些默认配置, 提供生成区间段随机端口;
6. `AbstractDubboManager`: dubbo 服务的 manager 层的父类, 使用方式参考 guide;
7. `AbstractDubboProvider`: dubbo 服务提供者的父类, 使用方式参考 guide;
8. `HashedWheelTimer`: 拷贝 dubbo 中的时间轮实现类, 主要处理 dubbo 应用如果暴露的服务超过了 64 个时的警告问题;

## 项目结构

## 集成使用

## 配置说明

## 注意事项

## 常见问题

## 后期计划

