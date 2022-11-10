# Spark Doc Starter

> API 文档服务

基于 Swagger 生成 API 接口文档, 目前提供 2 大类:

1. dubbo 服务接口文档;
2. RESTFul 接口文档;

## Table of Contents

- [Background](#background)
- [Install](#install)
- [Usage](#usage)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)

## Background

## Install

```
mvn clean install -U -Dmaven.test.skip=true
```

## Usage

可参考 `spark-doc-spring-boot-sample` 内的 sample.

需要注意的是, 我们将 UI 移除了, 如果在单模块(不走网关)的情况下, 需要显式添加 ui 组件才可查看接口, 如果走了网关, 则所有服务的接口文档都将在网关进行聚合, 微服务模块只需添加基础模块即可,
比如 `spark-doc-knife4j-spring-boot-starter`, `spark-doc-swagger-spring-boot-starter`.

rest 组件默认添加 `spark-doc-knife4j-spring-boot-starter` 作为文档组件, 因此业务端如果依赖了 `spark-rest-spring-boot-starter`, 则不需要在自行添加相关的文档组件;

如果不需要使用文档服务, 在对应的 pom 中排除即可.

最后一点: 生产环境 (prod) 将自动禁用文档服务!

### 项目结构

```
.
├── spark-doc-spring-boot-autoconfigure
├── spark-doc-spring-boot-core
│   ├── spark-doc-agent    // agent
│   ├── spark-doc-common
│   ├── spark-doc-dubbo
│   ├── spark-doc-knife4j
│   ├── spark-doc-restdoc
│   └── spark-doc-swagger
└── spark-doc-spring-boot-starter
		├── spark-doc-agent-spring-boot-starter  // agent
		├── spark-doc-dubbo-spring-boot-starter
		├── spark-doc-knife4j-spring-boot-starter
		├── spark-doc-restdoc-spring-boot-starter
		└── spark-doc-swagger-spring-boot-starter

```

### 简要说明

使用详情请查看 `spark-framework-guide`项目

#### 简单使用

1. center层引入agent依赖将间接引入`spark-doc-agent-spring-boot-starter`依赖

```xml
<dependency>
	<groupId>info.spark</groupId>
	<artifactId>spark-agent-spring-boot-starter</artifactId>
</dependency>
```

2. 启动项目，启动完毕后，在控制台将打印swagger地址，点击打开网页
3. 与swagger一样正常使用，可查看接口说明，接口调试

#### 代码说明

- **schema.info.spark.starter.doc.agent.AgentHandlerPlugin**
  该类实现了`springfox.documentation.spi.service.RequestHandlerProvider`，作用为扫描指定类，组装`springfox.documentation.RequestHandler`
  ，agent-doc，借助该类对`@ApiService`、`@ApiServiceMethod`进行扫描组装

- **schema.info.spark.starter.doc.agent.AgentOperationParameterReader**
  该类作用主要是对`agent`方法参数进行自动展开、隐藏。 使用了`aop`对`springfox.documentation.spring.web.readers.operation.OperationParameterReader`
  进行了拦截（因为翻阅源码没有看到可以通过扩展修改逻辑的代码...）。 对`ApiExtend`参数进行了隐藏

- **schema.info.spark.starter.doc.agent.AgentParameterHeadersConditionReader**
  对`agent`的一些请求头显示进行了处理，一些请求头是非必填的

- **schema.info.spark.starter.doc.agent.AgentParameterRequiredBuilderPlugin**
  对`agent`进行了适配，让`JSR 303`等验证注解能够在swagger渲染的时候显示正确的是否必填

- **schema.info.spark.starter.doc.agent.AgentParameterTypeReaderPlugin**
  对`agent`进行了适配，让`agent`参数，GET请求参数类型为`path`，其他请求的参数类型为`body`

- **hand.info.spark.starter.doc.agent.AgentDocHandlerFilter**
  对`agent`调试进行了增强，过滤器拦截如果请求为GET，请求头有`X-Agent-Doc-Test`，会在调用`agent-endpoint`GET方法前进行适配处理

#### Q&A

##### Q：调试页面怎么没有新增header的按钮？

A：点击「文档管理-个性化管理」，勾选开启动态请求参数，关闭之前的接口文档tab（注意，不是关闭网页），重新打开，调试即可出现请求头，该问题不知是否可以在初始化swagger的时候写入默认勾选

##### Q：点击控制台swagger文档地址，127.0.0.1:8080/doc.html怎么打开404？

A：这个问题在`agent-doc`刚开发完成时，电商同事遇到过，原因是电商同事修改了spring-boot默认的那3个静态资源访问目录地址，导致swagger生成的静态资源无法访问到

## Todo

- [ ] 接口文档聚合服务

## Maintainers

[@dong4j](mailto:dong4j@gmail.com)

## Contributing

请按照 Spark 代码规范进行编码开发, 使用 checkstyle 检查通过后才提交代码.

## License

