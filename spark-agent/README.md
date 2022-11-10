# Spark Agent

## 简介

此模块是使用 `spark-agent-spring-boot-starter` 的 demo 应用, `spark-agent-spring-boot-starter` 对 Controller 层进行了统一封装, 简化传统 SSM 框架中的 Controller 层的代码, 将
Controller 请求转发的逻辑使用 @ApiService 和 @ApiServiceMethod 进行了包装, 调用时使用 apiName 和 version 来执行指定的业务逻辑.

## 构建

```
mvn install
```

## 模块

```
.
├── spark-agent-adapter                               # v4 使用的适配模块
│  └── spark-agent-feign-adapter                     # 配到的 feign agent client
├── spark-agent-basic                                 # 底层依赖
├── spark-agent-spring-boot-starter                   # v5 使用的 client, 提供几个封装的 endpoint
├── spark-agent-core                                  # 被 adapter 和 client 依赖, agent 项目的主要逻辑实现
└── spark-example-agent                               # 集成测试模块
    ├── spark-example-agent-service                   # 集成 spark-agent-spring-boot-starter 后经过 agent 改造后的 web 服务
    ├── spark-example-agent-feign-adapter-customer    # 通过 feign agent client 消费 agent 服务
    ├── spark-example-feign--gateway-customer         # 通过 feign client 走网关消费 agent 服务
    ├── spark-example-feign-customer                  # 普通的 feign client 消费 agent 服务
    ├── spark-example-rest--gateway-customer          # 通过 RestTemplate 走网关消费 agent 服务
    └── spark-example-rest-customer                   # 通过 RestTemplate 消费 agent 服务
.
├── spark-agent-adapter                                       # agent client
│  ├── spark-agent-feign-adapter                             # agent feign 封装
│  └── spark-agent-rest-adapter                              # agent rest 封装
├── spark-agent-basic                                         # agent 基础模块
├── spark-agent-spring-boot                                   # agent spring boot 封装
│  ├── spark-agent-spring-boot-autoconfigure                 # agent server 自动装配
│  ├── spark-agent-spring-boot-core                          # agent 核心模块
│  │  ├── spark-agent-common                                # agent 公共模块
│  │  └── spark-agent-endpoint                              # agent endpoint
│  ├── spark-agent-spring-boot-sample                        # agent 实例
│  │  ├── spark-agent-spring-boot-sample-cloud-integration  # spring cloud 应用集成 agent server
│  │  ├── spark-agent-spring-boot-sample-endpoint           # 集成
│  │  └── spark-agent-spring-boot-sample-integration        #
│  └── spark-agent-spring-boot-starter                       #
└── spark-example-agent                                       #
    ├── spark-example-agent-feign-adapter-customer            #
    ├── spark-example-agent-rest-adapter-customer             #
    ├── spark-example-feign-gateway-customer                  #
    ├── spark-example-rest-customer                           #
    └── spark-example-rest-gateway-customer                   #


```

`spark-agent-basic` 是为了兼容 v4 版本, 不引入过多依赖, 将 2 个版本需要的类单独提出来.

## 集成

1. 添加依赖:

    ```xml
    <dependency>
        <groupId>info.spark</groupId>
        <artifactId>spark-agent-spring-boot-starter</artifactId>
        <version>last-version</version>
    </dependency>
    ```
2. 使用 @EnableAgentService 开启 @ApiService 扫描, 自动注入到 IoC, 便于后期映射处理;
    1. 如果此注解使用在 @SpringBootApplication 类上, 则不需要指定扫描路径;
    2. 否则需要指定扫描路径, 或者使用 services 属性注入指定的 apiService;
3. 继承 AbstractApiService 或者实现 ApiServiceDefinition 接口, 实现业务逻辑.

## 配置

```yaml
spark:
  agent:
    # 开启快速失败将在启动时检查 gent service 写法, 错误将抛出异常导致启动失败, 默认 true
    enable-fail-fast: true
    # 重复提交检查, 默认 false (暂未完成)
    enable-reply-check: false
    # 签名检查, 默认 false (暂未完成)
    enable-sign-check: true
```

## 说明

在经典的 SSM 框架分层结构上, 我们封装了 DispatcherServlet 的统一入口, 对外默认提供了 6 个 RESTFul 统一入口, 2 个辅助接口:

**6 个 RESTFul 接口**

1. GET /agent
2. GET /agent/{id}
3. POST /agent
4. PUT /agent
5. DELETE /agent
6. DELETE /agent/{id}

**2 个辅助接口**

1. GET /agent/time
2. GET /agent/ping

在统一入口上我们能对所有请求进行全局处理, 比如日志记录, 请求时间统计, 参数验证等处理, 最主要的是我们能简化 controller 的代理, 减少 controller 的数量.

为了将请求路由到指定的业务入口上, 我们使用 @ApiService 和 @ApiServiceMethod 2 个注解将业务方法和请求标识进行映射, 前端只需要确定 `apiName` 和 `version` 即可调用.

MVC DispatcherServlet 负责将请求路由到我们统一的入口, 方便我们做统一处理, 然后 agent 会通过映射关系将请求路由到我们的业务入口上.

`spark-agent` 组件的意义在于**通用性与规范性**.

将原来通过 RequestMapper 查找入口的方式转变为使用 `apiName + version` 的方式, 接口的地址都是确定不变的, 通过 version 我们更好的管理接口版本, 接口参数修改时后端改动的地方较 SSM 更少, 为了提高通用性, 我们的接口入参都是 byte[], 在业务入口会自动进行转换.

数据全部采用 JSON 格式,  请求方不需要依赖后端服务提供的 entity, 只需要按照接口说明传入指定的字段接口, 这样能让项目依赖关系简单化.

## 使用方式

参照 `spark-example-agent-service` 模块, 我们将列举 `spark-agent` 组件的使用方式.

### 开启 @ApiService 扫描

使用 @EnableAgentService 标识启动类:

用于指定被 {@link ApiService} 和 {@link ApiServiceMethod} 标识的类, 注入到 IoC

* 1. 如果不配置任何属性, 将默认扫描被标注类的包下所有的类;
* 2. 使用 value 或者 basePackages 指定扫描的包名;
* 3. 使用 basePackageClasses 扫描指定的每个类的包;
* 4. 使用 services 扫描指定的 class;

### 注册一个 API

可以通过 2 种方式将一个普通的类注册为一个 API

#### @ApiService

一个普通的 class 只要用 `@ApiService` 标识后, 就会自动注入到 IoC 中, 然后经过映射处理后即可成为一个 API

**API 定义**

```java
/**
 * <p>Description:
 * 使用 @ApiService 标识 class, 且继承 AbstractApiService 重写 service()
 * 然后使用 info.spark.exmaple.agent.TestService_1.0.0 调用即可
 * </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 10:54
 */
@Slf4j
@ApiService
public class TestService extends AbstractApiService<User, User> {

    /**
     * 业务入口方法
     *
     * @param user   user   入参
     * @param extend extend 扩展参数
     * @return the user     出参
     */
    @Override
    public User service(User user, ApiExtend extend) {
        return user;
    }
}
```

**调用 API**

```java
@Test
public void test() throws Exception {
  // 1. 构建入参, 转换为 btye[]
  JSONObject jsonObject = new JSONObject();
  jsonObject.put("username", "dong4j");
  jsonObject.put("date", new Date());
  byte[] body = jsonObject.toJSONString().getBytes();
	// 2. 构建 header
  HttpHeaders httpHeaders = new HttpHeaders();
  httpHeaders.add(AgentConstant.X_AGENT_API, "info.spark.exmaple.agent.TestService");
  httpHeaders.add(AgentConstant.X_AGENT_VERSION, "1.0.0");
	// 3. 调用
  HttpEntity<?> requestEntity = new HttpEntity(httpHeaders);
        ResponseEntity<byte[]> resEntity = restTemplate.exchange(url + "?data=" + Base64.encodeBase64URLSafeString(body), HttpMethod.GET, requestEntity, byte[].class);

	log.info("{}", JsonUtils.parse(resEntity.getBody(), Result.class));
}
```

`@ApiService` 有多个属性, 说明如下:

```
// 1. 直接使用 @ApiService, 不配置任何属性, 将通过被标注的类的全类名作为 apiName, versuon 默认为 1.0.0
@ApiService
// 2. 自定义 apiName, 请求方将使用 testService 调用 API
@ApiService(apiName = "testService")
// 3. 自定义 version, 简化版本升级
@ApiService(apiName = "testService", version = "1.0.1")
// 4. 自定义 value 属性, 这个与 apiName 没有直接关系, 只会影响注入到 IoC 的 bean name, 和 @Service 的 value 相关类似
@ApiService("testService")
// 5, 自定义 name 属性, 效果和 value 一样, 生成的 apiName 还是为标注类的全类名
@ApiService(name = "testService")
```

我们提供了 2 种接口需要业务类来实现, 如果业务方法只有一个入口, 使用 @ApiService + ApiService 接口即可, 我们还提供了一个抽象类, 用于简化 api service 的写法, 综上所述 api service 的 2 种写法:

1. 继承 AbstractApiService 抽象类:

   ```java
   @ApiService
   public class TestService extends AbstractApiService<User, User> {

     /**
       * 业务入口方法
       *
       * @param user   user   入参
       * @param extend extend 扩展参数
       * @return the user     出参
       */
     @Override
     public User service(User user, ApiExtend extend) {
       return user;
     }
   }
   ```

2. 实现 ApiService 接口

   这种方式不推荐, 这里就不写了.

#### @ApiServiceMethod

上面的方式只适合一个类一个业务的情况, 如果在同一个类中需要存在多个业务入口, 比如我们都会将 User 相关的操作放在同一个 UserController 一样, 就需要使用 @ApiServiceMethod + ApiServiceDefinition 的方式实现:

```java
/**
 * <p>Description: @ApiServiceMethod + ApiServiceDefinition 在同一个类中实现多个业务入口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 10:54
 */
@Slf4j
@ApiService
public class TestMethod implements ApiServiceDefinition {

    /**
     * Service user
     *
     * @param user   user
     * @param extend extend
     * @return the user
     * @see LocalSenderTest#test_method_1()
     */
    @ApiServiceMethod
    public User service1(User user, ApiExtend extend) {
        log.error("执行 info.spark.agent.local.service.TestMethod.service_1.0.0 in: [{}]", user);
        user.setVersion("1.0.0");
        return user;
    }

    /**
     * Service 1 user
     *
     * @param user   user
     * @param extend extend
     * @return the user
     * @see LocalSenderTest#test_method_2()
     */
    @ApiServiceMethod(value = "service_2", version = "2.0.0")
    public User service2(User user, ApiExtend extend) {
        log.error("执行 info.spark.agent.local.service.TestMethod.service_2_2.0.0 in: [{}]", user);
        user.setVersion("2.0.0");
        return user;
    }

    /**
     * Service 2 user
     * 方法签名错误, 如果配置了 快速失败 模式, 将在启动时抛出异常, 否则将在调用时失败
     *
     * @param user   user
     * @param extend extend
     * @return the user
     * @see LocalSenderTest#test_method_3()
     */
    @ApiServiceMethod()
    public User service3(User user) {
        return user;
    }
}
```

**调用方式**

```java
/**
 * 使用 @ApiServiceMethod, 将使用 @ApiService 指定的 apiName + @ApiServiceMethod 指定的 methodServiceName 生成最终的 apiName
 * version 以 @ApiServiceMethod 设置为准.
 * 1. @ApiService 为指定 apiName, apiName = 全类名
 * 2. @ApiServiceMethod 未指定 value, methodServiceName = method.name
 * 因此最终的 apiName = info.spark.agent.local.service.TestMethod.service1
 *
 * @see TestMethod#service1
 */
@Test
public void test_method_1() {
    test("local.info.spark.agent.service.TestMethod.service1", "1.0.0");
}

/**
 * 1. @ApiService 为指定 apiName, apiName = 全类名
 * 2. @ApiServiceMethod value = service_2
 * 3. @ApiServiceMethod version = 2.0.0, 将覆盖 @ApiService 的 version(没有指定, 默认为 1.0.0)
 *
 * @see TestMethod#service2 info.spark.agent.local.service.TestMethod#service2
 */
@Test
public void test_method_2() {
    test("local.info.spark.agent.service.TestMethod.service_2", "2.0.0");
}

/**
 * 错误的 agent service method 方法签名
 *
 * @see TestMethod#service3
 */
@Test
public void test_method_3() {
    test("local.info.spark.agent.service.TestMethod.service3", "2.0.0");
}

private void test(String apiName, String version) {
  ApiServiceRequest apiServiceRequest = new ApiServiceRequest();
  apiServiceRequest.setApi(apiName);
  apiServiceRequest.setVersion(version);

  byte[] data_send = JsonUtils.toJsonAsBytes(User.builder().username("dong4j").build());
  apiServiceRequest.setMessage(data_send);
  byte[] data = agentService.send(apiServiceRequest);
  Result result = JsonUtils.parse(data, Result.class);
  log.info("{}", result);
  Assert.assertEquals("2000", result.getCode());
}
```

需要注意的是:

1. 使用 @ApiServiceMethod() 标识的方法需要确保方法签名格式正确:

   ```java
   public Out[出参实体] methodName(In[入参实体], ApiExtend extend){}
   ```
2. 实现 ApiServiceDefinition 接口, 需要使用 @ApiServiceMethod 注解, 标识业务入口方法;
3. 实现 ApiService 接口不需要使用 @ApiServiceMethod 注解, 实现指定接口即可;

看查看 `spark-agent-spring-boot-sample` 模块内的单元测试了解相关用法.

## 使用Vert.x替换agent服务端SpringMvc

***vert.x通过server监听一个端口，所有接口都通过这个端口访问服务资源，可将其配置在yml文件中。 其所有的入口都在路由中，对比springmvc的controller层；可以在router中处理类似filter，exception等功能。<br/>**

***由于vert.x是响应式的框架，而agent的其它底层结构(jdbc,mq)都是阻塞式的，要使得vert.x可以使用，必须要使用其阻塞的方式开发。<br/>**

**可以通过自定义的注解如@RouteHandler,@RouteMapping来代替springmvc的@RequestMapping，方便开发使用。**<br/>

<u>**整合项目domo；spark-agent-spring-boot-vertx-sample-integration。 git地址：git@gitlab.server:share/spark-framework-guide.git**</u>

### 1. Vert.x底层使用netty，因此不仅仅要替换SpringMvc还需要替换Servlet。使用了Vert-web后Spring-web也可以不需要

**依赖**

```
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-web</artifactId>
    <version>4.0.3</version>
</dependency>
```

**简单demo**

```java
    @Override
    public void start() throws Exception {
        super.start();
        HttpServerOptions options =
            new HttpServerOptions().setMaxWebSocketFrameSize(1000000).setPort(8081);
        vertx.createHttpServer(options);

        Router router = Router.router(vertx);
        Route route = router.route("/agent");
        // 阻塞处理器
        route.blockingHandler(ctx -> {
            HttpServerRequest request = ctx.request();
            String header = request.getHeader("X-Agent-Api");

            //do something

            ctx.response()
                .putHeader("contentType", "text/plain")
                .end("sgf");
        });
        server.requestHandler(router).listen();
    }
```

**使用Spring容器可以将Httpserver，Router注入到ioc中，这样可以方便获取Router并添加新功能**

```java
/**
 * 将Router，Httpserver注入至容器
 */
@Configuration
public class VertxConfigure {

    @Resource
    private Vertx vertx;

    @Bean
    public Router getRouter(){
        return Router.router(vertx);
    }

    @Bean
    public HttpServer server(Router router){
        HttpServerOptions options =
            new HttpServerOptions().setMaxWebSocketFrameSize(1000000).setPort(8081);
        HttpServer server = vertx.createHttpServer(options).requestHandler(router);
        server.listen();
        return server;
    }
}

/**
 * 设置router
 */
public void setRouter() {
    filter();
    errorHandling();
    Route route = router.route("/agent");
    route.handler(ctx -> {
        HttpServerRequest request = ctx.request();
        String header = request.getHeader("X-Agent-Api");

        // do somting

        //校验异常处理
        // int i = 1 / 0;
        ctx.response()
            .putHeader("content-type", "text/plain; charset='utf-8'")
            .end("sgf");
    });
}
```

### 2. 去除SpringMvc后，agent组件服务端中的过滤器，全局异常都不可再使用。

#### 解决方案:

**router可以配置级别来达到过滤的效果**

```java
/**
 * 过滤器
 */
@RouteSetting
public void filter() {
    router.route()
        .order(1)
        .handler(ctx -> {
        System.out.println("filter");

        //do something

        ctx.next();
    });
}
```
**异常处理**

```java
    /**
     * 异常处理
     */
    @RouteSetting
    public void errorHandling() {
        router.route().failureHandler(ctx -> {

            //do something

            ctx.response()
                .putHeader("Content-Type", "text/plain;charset='utf-8'")
                .setStatusCode(500)
                .end("程序异常");
        });
    }
```

### 3.使用自定义注解简化开发。

```
/**
 * Router API Mehtod 标识注解
 * @author zml
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RouteHandler {

    String value() default "";

    boolean isOpen() default false;

    /**
     * 注册顺序，数字越大越先注册
     */
    int order() default 0;
}
```

```
/**
 * Router API Mehtod 标识注解
 * @author zml
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RouteMapping {

    String value() default "";

    /**** 是否覆盖 *****/
    boolean isCover() default true;

    /**** 使用http method *****/
    RouteMethod method() default RouteMethod.GET;

    /**** 接口描述 *****/
    String descript() default "";

    /**
     * 注册顺序，数字越大越先注册
     */
    int order() default 0;

}
```

代码示例：

```
@RouteHandler("restApp")
public class RestApi {

    @Resource
    private SchoolMapper schoolMapper;

    @RouteMapping(value = "/*", method = RouteMethod.ROUTE, order = 2)
    public Handler<RoutingContext> appFilter() {
        return ctx -> {
            System.err.println("我是appFilter过滤器！");
            ctx.next();
        };
    }

    /**
     * 演示路径参数
     *
     * @return
     */
    @RouteMapping(value = "/test", method = RouteMethod.GET)
    public Handler<RoutingContext> myTest() {
        return ctx -> {

            // List<String> strings = schoolMapper.selectAll();
            // System.out.println(strings);
            // do something

            ctx.response()
                .putHeader("content-Type", "text/html")
                .end("Hello World!");
        };
    }

}
```

