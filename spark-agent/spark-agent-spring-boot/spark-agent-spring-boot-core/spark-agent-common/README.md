# 说明

此模块是 agent 的主要逻辑, 包括注解扫描注入 IoC, 请求关系映射, 请求转发, 请求处理等.

主要入口:

1. EnableAgentService: 开启扫描 @ApiService
2. AgentServiceRegistrar: 注入指定 class 到 IoC
3. ApiServiceContext: 本地维护 apiName 与 invoker 的映射关系
4. AgentService: 请求处理, 目前默认实现了一个本地处理器, 用于直接依赖 `spark-agent-spring-boot-starter` 的请求转发处理, 主要作用是通过 apiName 找到指定的 invoker
5. ApiServiceInvoker: 执行业务逻辑

