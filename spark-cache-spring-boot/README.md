# Spark Cache Starter

## 项目简介

基于 jetcache 封装到缓存组件. 自定义了序列化与反序列化方式; 提供了基于 Redis 的分布式锁的实现, 包括基于注解与工具类 2 种方式.

## 功能特性

## 核心类介绍

### 自动装配

1. `CacheAutoConfiguration`: 根据条件装配多个 `CacheService` 接口的底层实现类, 装配使用 Jackson 进行序列化与反序列化;
2. `CacheLockAutoConfiguration`: 装配基于 Redis 的分布式锁 (底层使用 RedisLockRegistry 实现);
3. `LettuceConnectionConfiguration`: Redis 的客户端 Lettuce 的自动装配;
4. `RedisKeyExpirationAutoConfiguration`: Redis key 过期事件监听自动装配;

### 注解

1. `CacheLock`: 带 EL 表达式处理的分布式锁注解;
2. `CacheLockUtils`: 分布式锁工具类, 带有自动续期功能; 获取锁的失败策略有快速失败, 超时失败;

## 项目结构

## 集成使用

## 配置说明

## 注意事项

## 常见问题

## 后期计划
