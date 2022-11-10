# Spark Mybtis Starter

## 项目简介

封装了 Mybatis Plus 和 Druid 相关逻辑, 并且提供了一些简单的接口, 便于开发者快速搭建项目.

## 功能特性

1. 提供了 Druid 连接池，允许一次执行多条语句;
2. 提供了 SQL 性能规范插件 IllegalSQLInnerInterceptor，拦截掉不规范SQL语句;
3. 提供了 SQL 执行分析插件 SqlExplainInterceptor，拦截一些整表操作,在生产环境需要关闭;
4. 提供了分页插件 PaginationInterceptor, 不需要设置方言, mybatis-plus 自动判断;
5. 提供了自定义 SQL 注入器 MybatisSqlInjector;
6. 提供了性能分析插件 PerformanceInterceptor 和 p6spy 插件进行sql性能分析，用于显示每条 SQL 语句及其执行时间;
7. 提供了 mybatis-plus 敏感字段加解密拦截器 SensitiveFieldDecryptIntercepter、SensitiveFieldEncryptIntercepter;
8. 使用 MybatisEnumTypeHandler 代替默认的 EnumTypeHandler, 实现 EntityEnum 子类的类型转换(数据库存 value, 返回 Entity)
9. 提供了自动创建时间和更新时间 TimeMetaHandler，处理新增和更新的基础数据填充,配合 BaseEntity 和 MyBatisPlusConfig 使用;
10. 封装了 BaseService、BaseDao,提供了新的常用方法等;
11. 基于 CQRS 思想，提供了 command 与 query 分离的模式

## 核心类介绍

1. `MybatisAutoConfiguration`: 全部的功能都可以在张哥装配类中查看;
2. `P6spyAutoConfiguration`: p6spy 的自动转配, p6spy 的使用请查看 guide;
3. `info.spark.starter.common.base`: 基础接口, 抽象类与基础实体, 应该要明确每个实体的使用方式;
4. `info.spark.starter.mybatis.handler`: MyBatis Plus 的功能自定义与扩展;
5. `DatasourceInitializeListener`: 配置加载完成后检查是否存在 datasource 配置, 如果不存在, 则排除 datasource 自动配置, 避免启动失败
6. `PerformanceInterceptor`: 会以 error 日志等级输出慢 SQL(超过 MybatisProperties.performmaxTime 设置的时长), 并会发送 `SqlExecuteTimeoutEvent` 事件, 应用端可执行处理,
   比如记录慢查询日志等;
7. `IExchangeService`: 用作 Service 与 Dto 间的桥梁, 具体使用请查看 guide;
8. `DruidLauncherInitiation`: 修改了 Druid 部分默认配置;
9. `MybatisLauncherInitiation`: 修改了 Mybatis Plus 部分默认配置;

## 项目结构

## 集成使用

## 配置说明

## 注意事项

可以看看 Mybatis Plus 官网, 我们已经有很长一段时间没有更新 Mybatis Plus 了, 一些新的特性可以直接替代我们的逻辑, 可以考虑使用最新版本.

## 常见问题

## 后期计划
