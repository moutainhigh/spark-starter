# Spark Captcha Starter

## 项目简介

验证码组件, 提供验证码生成与验证码校验功能.

## 功能特性

## 核心类介绍

### 自动装配

1. `CaptchaAutoConfiguration`: 默认使用 `Kaptcha` 生成验证码, 且提供普通和增强 2 个验证码类型, 提供可扩展的验证码拦截规则 (`BlockingRules`);

### 核心类

1. `BlockingRules`: 验证码请求拦截规则接口;
2. `CaptchaEndpoint`: 提供验证码生成, 检查等接口, 业务端无需再次开发;
3. `CaptchaCodeFilter`: 验证码过滤器;

## 项目结构

## 集成使用

## 配置说明

## 注意事项

## 常见问题

## 后期计划

- [ ] 短信验证码;
