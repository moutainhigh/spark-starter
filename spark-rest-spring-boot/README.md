# Spark REST Starter

## Features

1. 定义全局异常处理类: `ServletGlobalExceptionAutoConfiguration`;
2. 所有接口返回统一的数据结构;
3. 接口入参校验;
4. 返回结果基础类型, List 等二次包装: `CustomizeReturnValueHandler`;
5. 自动删除 String 中的首尾空白符: `ControllerStringParamTrimConfig`
6. web 容器配置与优化: `UndertowAutoConfiguration`;
7. 各种过滤器: `ServletWebConfiguration`;
    1. `CharacterEncodingFilter`: 字符集过滤器;
    2. `ServletGlobalCacheFilter`: Request 与 Response 缓存过滤器;
    3. `CorsFilter`: 跨域过滤器;
    4. `ExceptionFilter`: Filter 异常过滤器 (将 Filter 异常流转到 Controller, 以便复用全局异常处理器逻辑);
    5. `GlobalParameterFilter`: 全局参数过滤器, 可直接在 Controller 中注入 token 中的字段, CurrentUser 等;
    6. `XssFilter`: XSS 过滤器;
8. 消息转换器:
    1. `StringToDateConverter`: 字符串转日期;
    2. `GlobalEnumConverterFactory`: 枚举转换器;
    3. `MappingApiJackson2HttpMessageConverter`: 全局 json 转换器;
    4. `EntityEnumSerializer`: 枚举序列化器;
    5. `EntityEnumDeserializer`: 枚举反序列化器;
9. 参数处理器:
    1. `RequestSingleParamHandlerMethodArgumentResolver`: 处理 @RequestSingleParam;
    2. `RequestAbstractFormMethodArgumentResolver`: 处理 @RequestAbstractForm, 用于接口接收抽象类, 此注解将自动转换为对应子类;
    3. `FormdataBodyArgumentResolver`: 处理 @FormDataBody, 用于将 formdata 数据转换为实体;
10. 拦截器:
    1. `CurrentUserInterceptor`: 注入 CurrentUser 到 Controller;
    2. `AuthenticationInterceptor`: 使用注解代替接口注入的 CurrentUser;
11. API version 处理器: `ApiVersionRequestMappingHandlerMapping`

### 1. Rest ApiVersion 新特性

#### 1.1. 说明

我们简化了rest层多接口版本的需求。

1. 兼容老版本接口路径
2. ~~提供版本平滑降级（访问不存在的版本自动匹配到最接近版本接口）~~
3. 无需硬编码`@RequestMapping`
4. 注解可在method、class, method为高优先级
5. 支持swagger显示
6. 支持多version（数组多value）

#### 1.2. 规定

规定版本格式，我们将在`Url Pattern`第一层级追加版本匹配符，如:

- `/demo/hello`
- `/v1/demo/hello`
- `/v2/demo/hello`

#### 1.3. 使用

在springMvc / springboot项目中引入`spark-facade-spring-boot-starter`/`spark-rest-spring-boot-starter`
在需要开发多版本接口的method上添加注解`@ApiVerion(2)`

```java
@Slf4j
@RestController
@RequestMapping(value = "/demo")
public class TestApiController {

    /**
     * Hello 1
     *
     * @return the string
     * @since 1.0.0
     */
    @ApiVersion({1, 6})
    @GetMapping("hello")
    public String hello1() {
        return "hello version1 版本1、6";
    }

    /**
     * Hello
     *
     * @return the string
     * @since 1.0.0
     */
    @GetMapping("hello")
    public String hello() {
        return "hello，原始版本";
    }
}
```

启动服务，

访问[127.0.0.1:8080/demo/hello](http://127.0.0.1:8080/demo/hello), 响应`"hello，原始版本"`

访问[127.0.0.1:8080/v1/demo/hello](http://127.0.0.1:8080/v1/demo/hello), 响应`"hello version1 版本1、6"`

访问[127.0.0.1:8080/v6/demo/hello](http://127.0.0.1:8080/v6/demo/hello), 响应`"hello version1 版本1、6"`

## Bug Fixes


