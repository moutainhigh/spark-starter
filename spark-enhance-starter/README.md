# Spark Enhance Starter

## 项目简介

聚合几个 starter 组成一个全新的项目，主要为了减少业务端引入依赖的数量.

## 组件介绍

1. `spark-framework-starter`: 聚合 launcher 和 logsystem 这个基础组件(所有 v5 项目都会依赖);
2. `spark-agent-sdk-parent`: 此模块应该作为所有 Agent SDK 项目的父项目, 内置了开发 SDK 所需的所有依赖, 因此在开发 SDK 时只需要引入业务依赖即可;
3. `spark-facade-spring-boot-starter`: 如果只是 API 接口项目, 可使用此依赖, 提供 rest 和 swagger 相关依赖;
4. `spark-ssm-spring-boot-starter`: 如果产品服务是一个单体项目, 且需要操作数据库, 则可直接使用此依赖, 与 asm 组件类似, 提供了 swagger 功能;
5. `spark-asm-spring-boot-starter`: 如果业务中台是一个单体项目, 且需要操作数据库, 则可直接使用此依赖, 与 ssm 组件类似, 提供了 swagger 功能;
6. `spark-mservice-spring-boot-starter`: 如果是 dubbo 服务, 可使用此依赖, 提供 cloud 和 dubbo 相关依赖;
7. `spark-state-spring-boot-starter`: Spring Statemachine 相关依赖;


