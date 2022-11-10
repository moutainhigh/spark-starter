# Spark Idempotent Starter

## 项目简介

基于 token 实现的幂等校验组件, 调用幂等接口前需要先获取 token, 只有成功获取到 token 才能调用幂等接口.

## 功能特性

## 核心类介绍

### 自动装配

1. `IdempotentAutoConfiguration`: 装配了 `ApiIdempotentFilter` 用于拦截幂等接口;

### 核心类

1. `IdempotentEndpoint`: 自动暴露获取幂等 token 的接口;
2. `ApiIdempotentFilter`: 拦截幂等接口, 校验 header 是否存在幂等 token, 不存在报错, 存在则放行并删除 token;
3. `ApiIdempotent`: 在需要保证 接口幂等性 的 Controller 的方法上使用此注解;

## 项目结构

## 集成使用

## 配置说明

## 注意事项

## 常见问题

## 后期计划
