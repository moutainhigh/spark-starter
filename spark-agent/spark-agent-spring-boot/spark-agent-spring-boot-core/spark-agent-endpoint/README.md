# 说明

此模块封装了 6 个通用的 endpoint.

主要入口:

1. AgentClientEndpoint: 用于标识 endpoint;
2. AgentClientEndpointHandlerMapping: 处理被 @AgentClientEndpoint 标识的类, 注册成 RESTFul 接口;
3. AgentConfiguration: 此模块的配置类, 提供几个默认的 bean;
