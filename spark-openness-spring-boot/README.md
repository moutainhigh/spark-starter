# Spark Openness Starter

## 一. 需求背景

目前公司多个业务系统都需要暴露`open-api`提供给外部第三方使用，不需要依赖`auth`组件进行认证操作，通过签名鉴权等手段，通过即可获取资源。 如：`v5-IoT-cloud`暴露**统计数据查询接口**给第三方，第三方可基于数据自定义`业务ui`展示。

## 二. 前期技术调研

> 在与第三方系统做接口对接时，往往需要考虑接口的安全性问题。

### 1. 认证方案

> 例如订单下单后通过 **延时任务** 对接 **物流系统** 这种 **异步** 的场景，都是属于系统与系统之间的相互交互，不存在用户操作；所以认证时需要的不是用户凭证而是系统凭证，通常包括 **app_id** 与 **app_secrect**。**app_id**与**app_secrect**由接口提供方提供

#### 1.1. Basic 认证

这是一种较为简单的认证方式，客户端通过明文（Base64编码格式）传输用户名和密码到服务端进行认证。

通过在 `Header` 中添加key为 Authorization，值为 Basic 用户名:密码的base64编码，例如app_id为和app_secrect都为 `zlt`，然后对 `zlt:zlt` 字符进行base64编码，最终传值为：

```http
Authorization: Basic emx0OnpsdA==
```

##### 1.1.1. 优点

简单，被广泛支持。

##### 1.1.2. 缺点

安全性较低，需要配合HTTPS来保证信息传输的安全

1. 虽然用户名和密码使用了Base64编码，但是很容易就可以解码。
2. 无法防止 **重放攻击** 与 **中间人攻击**。

#### 1.2. Tonken 认证

使用 `Oauth2.0` 中的 `客户端模式` 进行Token认证，流程如下图所示：

<img src="https://gitee.com/Not_Ann/PicHome/raw/master/uPic/截屏2021-08-17 14.08.41.png" alt="tonken流程图" style="zoom:40%;" />

##### 1.2.1. 优点

安全性相对 `Baic认证` 有所提升，每次接口调用时都使用临时颁发的 `access_token` 来代替 `用户名和密码` 减少凭证泄漏的机率。

##### 1.2.2. 缺点

依然存在 `Baic认证` 的安全问题。

#### 1.3. 动态签名

在每次接口调用时都需要传输以下参数：

- **app_id** 应用id
- **time** 当前时间戳
- **nonce** 随机数
- **sign** 签名

其中sign签名的生成方式为：使用参数中的 app_id + time + nonce 并在最后追加 `app_secrect` 的字符串进行md5加密，并全部转换成大写。

> 如果需要实现参数的防篡改，只需把接口所有的请求参数都作为签名的生成参数即可

##### 1.3.1. 优点

安全性最高

1. 服务端使用相同的方式生成签名进行对比认证，无需在网络上传输 `app_secrect`。
2. 可以防止 **中间人攻击**。
3. 通过 `time` 参数判断请求的时间差是否在合理范围内，可防止 **重放攻击**。
4. 通过 `nonce` 参数进行幂等性判断。

##### 1.3.2. 缺点

不适用于前端应用使用，js源码会暴露签名的方式与app_secrect

## 三. 技术实现

### 1. 实现选择

[动态签名](####1.3. 动态签名)目前是最适用的实现方式。

### 2. 简要流程

1. 第三方系统在`v5租户中心`创建`租户`，由`v5租户中心`生成租户信息与对应`key`信息

2. 第三方调用接口时候，带上参数，并使用参数生成签名一并带过来，`open-api`组件将拦截配置的接口，将`client_id`作为调用`v5-user`服务的入参，获取第三方在创建租户时候生成的`key`信息，将信息缓存。

3. 服务提供方将入参参数再一次进行签名处理，如果和传递过来的一致则表示校验成功，返回资源。

   其中，需要对参数进行验证：

    - client_id是否存在
    - 时间戳是否超时
    - 随机数位数

4. 如果既引入了`auth `，又引入了`openapi`，第三方是不需要登录使用tonken

### 3. 技术注意点

#### 3.1. HttpServletRequest inputStream 读取问题

- 问题：`open-api`主要是做接口验证签名，整体实现相对简单，但如果使用`filter`读取了 `inputStream`， 发现原来`controller`中通过`@RequestBody`
  获取JSON参数的接口抛出`“Required request body is missing”`的错误。这是因为`inputStream`的数据只能读取一次
- 解决：使用spring提供的`ContentCachingRequestWrapper`即可解决

```java
@Override
public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException{
    ContentCachingRequestWrapper requestWrapper=new ContentCachingRequestWrapper((HttpServletRequest)request);
    String body=IOUtils.toString(request.getInputStream(),request.getCharacterEncoding());
    //TODO 验证签名

    chain.doFilter(requestWrapper,response);
    }
```

#### 3.2. Filter、OncePerRequestFilter

我们知道`Filter`与`Interceptor`的一些区别，而为什么项目中使用的`Filter`都是实现自`OncePerRequestFilter`呢？

实现了`Filter`使用@Component + @Order标识类，容器启动时候，根据`servlet3.0`规范，`SPI`将加载spring自己实现的 `ServletContextInitializer`
接口实现，通过spirng类文档得知，该类会加载所有`Filter`的bean到`servlet`容器中。

`OncePerRequestFilter`由spring提供，他继承了`GenericFilterBean`，实现了`javax.servlet.Filter`的接口，

`init`方法执行：

1. 将配置参数映射到此筛选器的bean属性。
2. 然后调用子类`initFilterBean()`初始化。

如果配置`DelegatingFilterProxy`代理自己的`Filter`，那么会执行`DelegatingFilterProxy`实现的`initFilterBean`方法，方法中调用了`initDelegate`

`OncePerRequestFilter`只会过滤一次请求，因为不同的servlet容器，或是在服务器内部的请求转发，都是有可能造成请求被多次过滤的，为了规避风险，我们都使用该类。

使用：自定义编写`Filter`实现`OncePerRequestFilter`，通过@bean注册为`FilterRegistrationBean<Filter>`

### 4. 实现细节

我们最终参照ali `OSS`API方式进行实现，url签名方式会暴露过期时间等，本项目中使用header签名方式。

#### 4.1. header参数

- X-Fapi-ClientId # AccessId，用于获取 secretKey
- X-Fapi-Nonce # 随机位数
- X-Fapi-Timestamp # 时间戳
- X-Fapi-Sign # base64 ( AES加密byte[] (HTTP-Method + 参数MD5 +Content-Type + time + nonce + 资源url) )

#### 4.2. ACL

组件提供接口，业务方实现组件接口，组件即可调用实现逻辑，返回boolean

#### 4.3. 黑白名单

组件提供接口，业务方实现组件接口，组件即可调用实现逻辑，返回boolean

#### 4.4. 请求流程

##### 4.4.1. 添加注解

给需要拦截的api方法或类上添加注解

##### 4.4.2. 黑白名单检验

##### 4.4.3. ACL检验

##### 4.4.4. URL、handler校验

##### 4.4.5. 参数检验、过期检验

##### 4.4.6. 签名校验，防重放等

## 四. 提供并配置API

### 4.1. method/class添加注解

基于`Spirng-MVC`

1. 引入`spark-openness-aes-spring-boot-starter`依赖
2. 在需要暴露的`class`、`method`上面添加`@Openness`注解即可

### 4.2. API配置

| yaml配置              | 默认值         | 说明                            |
| --------------------- | -------------- | ------------------------------- |
| spark:                  |                |                                 |
| ··openness:           |                |                                 |
| ····nonce-length:     | 6              | 随机值位数                      |
| ····time-interval:    | 60 * 1000 毫秒 | 相邻请求的时间间隔              |
| ····include-patterns: | /**            | 需要被过滤器拦截的url pattern   |
| ····exclude-patterns: |                | 不需要被过滤器拦截的url pattern |

### 4.3. 扩展逻辑实现
我们在`spark-openness-spring-boot`组件package`info.spark.starter.openness.handler`中提供了几个handler接口，分别为：
1. IBucketListHandler
黑白名单自定义校验，默认不拦截

2. IResourceAclHandler
资源 ACL 过滤，默认可访问全部资源

3. ISecretAuthHandler
`accessKey`获取逻辑，组件中，默认实现类为`DefaultSecretAuthHandler`，通过RestTemplate调用v5-user

**API提供方按照自己的需求，在项目中实现对应接口逻辑，并注入ioc容器中即可**



## 五. 如何调用API

### 5.1. 请求http header配置

通过API提供方提供的API文档，可知`uri`、`Centent-Type`、`http-method`
在http-hander上添加对应header

| HTTP Header        | 说明                                                   | 必填 |
| ------------------ | ----------------------------------------------------- | --------- |
| `Content-Type`     | 接口对应类型                                           |  √   |
| `X-Fapi-ClientId`  | 在 spark icpic注册时获得的accessId                        |  √   |
| `X-Fapi-Nonce`     | 每次请求为不同的随机值，默认配置为6位，API提供方可配置 |  √   |
| `X-Fapi-Timestamp` | 请求时候，使用System获取到当前时间戳                   |  √   |
| `X-Fapi-Sign`      | 使用工具类提供的签名方法，传入对应参数得到的签名字符串 |  √   |

### 5.2. 签名工具类使用

引入pom

```
<dependency>
  <groupId>info.spark</groupId>
  <artifactId>spark-openness-client-common</artifactId>
  <version>${revision}</version>
</dependency>
```

使用参数生成sign

```
@Test
void test_sign() {
  Map<String, Object> urlParams = new HashMap<>();
  urlParams.put("key2", "value2");
  urlParams.put("key11", "value11");
  urlParams.put("key1", "value1");

  // 1. url params参数 key=value参数都放这里，2.只能放 post json body参数
  String md5Params = OpennessSignUtils.md5Params(urlParams, null);

  SignEntity signEntity = new SignEntity();
  signEntity.setHttpMethodType("post");
  signEntity.setContentTypeStr("application/json");
  signEntity.setUri("/metas/product");
  // 与header X-Fapi-Timestamp 保持一致
  signEntity.setTimestamp(System.currentTimeMillis() + "");
  // 与header X-Fapi-Nonce 保持一致
  signEntity.setNonce("123456");
  signEntity.setParamMd5(md5Params);
  // 你的 accessKey，只用于客户端签名，请保管好，请求时只需要在head传入clientId
  signEntity.setSecretKey("2222222222222222");

	// 使用签名工具类，进行sign签名
  String sign = OpennessSignUtils.sign(signEntity);
}
```

## Todo List

1. 添加请求与相应记录(mongo)
2. 添加限流配置
