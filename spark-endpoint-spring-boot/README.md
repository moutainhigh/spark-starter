# Spark Endpoint Starter

## 项目简介

为了实现在框架内部提供 RESTFul 接口, 集成此组件后只需要使用 `@Endpoint` 即可暴露一个 RESTFul 接口.

## 功能特性

## 核心类介绍

### 自动装配

1. `EndpointAutoConfiguration`: web 服务启动完成后调用一个特定接口进行服务预热, 解决 web 服务第一次请求慢的问题;
2. `ServletStartInfoAutoConfiguration`: 装配了一个 `StartInfoEndpoint` 用于输出应用元数据(git 提交信息)的接口;

### 核心类

1. `ProjectInfoEndpoint`: 提供一个输出所有接口元数据的端点;
2. `EndpointHandlerMapping`: 自动将被 @Endpoint 标识的类注册为一个 controller;

## 项目结构

## 集成使用

## 配置说明

## 注意事项

## 常见问题

## 后期计划

