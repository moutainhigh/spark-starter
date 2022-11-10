# Spark Agent Starter

![](./logoly.pro.png)

## 项目简介

## 功能特性

## 关键类说明

### 自动装配

1. 各种 filter 的装配;
2. 各种安全相关类装配;
3. 参数首尾空白符处理;
4. 参数校验装配;

### 核心类

1. `AgentServiceRegistrar`: 启动时通过 `@ApiService` 和 `@ApiServiceMethod` 注入 Agent Service;
2. `ApiServiceContext`: 解析 Agent Service 并持有 apiName 和 ApiService 的映射关系;
3. `ApiServiceInvoker`: 处理 controller 接收到的请求, 根据 apiName 进行请求分发, 并处理响应结果;
4. `Plugin`: 插件扩展接口;
5. `AgentCommonEndpoint`: REST 接口封装;

## 项目结构

## 集成使用

## 配置说明

## 注意事项

**agent 和 rest 无法兼容**

## 常见问题

## 后期计划
