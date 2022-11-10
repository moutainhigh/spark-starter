# 模块说明

此模块的功能类似于 Spring Cloud Feign, 主要使用对象是 v4 项目, 为了不引入 Spring Cloud 相关依赖, 这里使用原生的 Feign 实现了 Spring Cloud Feign 的功能,
只需要简单的使用 `@FeignClient` 标识接口, 即可自动生成代理类, 并注入到 IoC, 调用 http 服务时, 只需要使用 @Resource 或者 @Autowired 注入 client, 用于简化 v4 调用 v5 服务的成本, 提高开发效率.

## 集成方式

### 非 Spring Boot 项目

1. 添加依赖

    ```
    <dependency>
        <groupId>info.spark</groupId>
        <artifactId>spark-starter-feign-adapter</artifactId>
        <version>last-version</version>
    </dependency>
    ```
2. 使用 `@EnableFeignClients` 指定自定义的 Feign Client package;
3. 导入配置, 用于加载 Feign Client 注册相关的 bean:
    1. Java Config 方式:

    ```
    @ImportResource( {"classpath*:spark-starter-feign-adapter.xml"})
    ```
    或者:

    ```
    @Import(value = FeignClientAdapterConfiguration.class)
    ```
    2. xml 方式:

    ```xml
    <import resource="classpath*:spark-starter-feign-adapter.xml"/>
    ```

推荐使用 Java Config 的方式

### Spring Boot 项目

1. 添加依赖

    ```
    <dependency>
        <groupId>info.spark</groupId>
        <artifactId>spark-starter-feign-adapter</artifactId>
        <version>last-version</version>
    </dependency>
    ```
2. 使用 `@EnableFeignClients` 指定自定义的 Feign Client package;

单元测试分为 v4 和 v5 两种方式.
集成测试可见 `spark-starter-example-feign-adapter` 模块.

## 配置说明

全部配置项如下:

```properties
# 服务负载地址
spark.feign.ribbon.list-of-servers=127.0.0.1:18080
# 同一实例最大重试次数, 不包括首次调用
spark.feign.ribbon.max-auto-retries=0
# 重试其他实例的最大重试次数, 不包括首次所选的 server
spark.feign.ribbon.max-auto-retries-next-server=1
# 是否所有操作都进行重试
spark.feign.ribbon.ok-to-retry-on-all-operations=false
# 服务刷新时间
spark.feign.ribbon.server-list-refresh-interval=2000
# feign 连接超时时间
spark.feign.ribbon.connect-timeout=5000
# http 请求超时时间
spark.feign.ribbon.read-timeout=10000
```

### url 说明

@FeignClient 目前支持通过 value(name) 和 url 进行注册, value(name) 表示 v5 的服务名, 需要查看 spark-gateway 的路由配置, 确保服务名配置正确 (后期会根据需求考虑是否将服务名注册到 v4 的 zookeeper 中).

#### 使用 value(name)

如果使用 value(name) 注册,  将自动生成 url, 规则如下:

```
url = http://gateway/服务名
```

如果 @FeignClient 的 agent 属性设置为 true, 将会在 url 后加上 `/agent`, 因此最终的 url 为:

```
url = http://gateway/服务名/agent
```

注意:

1. 如果 agent = true, 请确保服务端支持 agent 模式;
2. 如果 value, name, url 都没有配置, 则自动生成 `http://gateway/`, 一般用于本地测试';

Example:

```
@FeignClient
@FeignClient("demo-service")
@FeignClient(name = "demo-service")
@FeignClient(name = "demo-service", agent = true)
```

#### 使用 url

如果配置了 url, 则必须添加 `spark.feign.url.服务名` 配置, 且需要确保配置前缀为 `http://gateway/`, agent 属性配置同上.

注意:

1. 如果既配置了 value(name), 又配置了 url, 将优先使用 url 属性;
2. 如果服务端使用 agent 模式, 不需要在 `spark.feign.url.服务名` 的配置添加 `/agent`, 只需要设置 `agent = true` 即可;

Example:

```
# 使用 url
@FeignClient(url = "${spark.feign.url.demo-service}")
@FeignClient(url = "${spark.feign.url.demo-service}", agent = true)

# 在配置中添加如下配置
## 1. 如果服务通过 spark-gateway 路由 (一般用于线上服务):
spark.feign.url.demo-service=http://gateway/服务名
## 2. 没有通过 spark-agteway 路由 (一般用于本地开发, 直接调用服务)
spark.feign.url.demo-service=http://gateway/
```

### 服务负载说明

服务负载通过 `spark.feign.ribbon.list-of-servers` 指定, 默认配置为本地网关地址(`127.0.0.1:18080`), 如果服务还没有通过网关路由, 可通过 url 指定服务地址, 然后修改 `list-of-servers` 的配置.

Example:

```
# 1. 单个服务
spark.feign.ribbon.list-of-servers=127.0.0.1:18080
# 2. 多个服务, 使用 逗号 分隔服务列表
spark.feign.ribbon.list-of-servers=127.0.0.1:18080,127.0.0.1:18081
```

## 其他配置

如果需要关闭服务重试, 需要如下配置:

```
spark.feign.ribbon.max-auto-retries=0
spark.feign.ribbon.ok-to-retry-on-all-operations=false
```

如果开启重试, 也只会对 GET 请求有效, 这是防止 POST 等对数据有影响的请求在重试后因为接口未做幂等性导致数据异常

## Ribbon 负载策略

| 策略名 | 策略声明 | 策略描述 | 实现说明 |
|:--- | ---| ---| --- |
| BestAvailableRule | public class BestAvailableRule extends ClientConfigEnabledRoundRobinRule | 选择一个最小的并发请求的server | 逐个考察Server, 如果Server被tripped了, 则忽略, 在选择其中ActiveRequestsCount最小的server |
| AvailabilityFilteringRule | public class AvailabilityFilteringRule extends PredicateBasedRule | 过滤掉那些因为一直连接失败的被标记为circuit tripped的后端server, 并过滤掉那些高并发的的后端server (active connections 超过配置的阈值)  | 使用一个AvailabilityPredicate来包含过滤server的逻辑, 其实就就是检查status里记录的各个server的运行状态 |
| WeightedResponseTimeRule | public class WeightedResponseTimeRule extends RoundRobinRule | 根据响应时间分配一个weight, 响应时间越长, weight越小, 被选中的可能性越低.  | 一个后台线程定期的从status里面读取评价响应时间, 为每个server计算一个weight. Weight的计算也比较简单responsetime 减去每个server自己平均的responsetime是server的权重. 当刚开始运行, 没有形成status时, 使用roubine策略选择server.  |
| RetryRule | public class RetryRule extends AbstractLoadBalancerRule | 对选定的负载均衡策略机上重试机制.  | 在一个配置时间段内当选择server不成功, 则一直尝试使用subRule的方式选择一个可用的server |
| RoundRobinRule | public class RoundRobinRule extends AbstractLoadBalancerRule | roundRobin方式轮询选择server | 轮询index, 选择index对应位置的server |
| RandomRule | public class RandomRule extends AbstractLoadBalancerRule | 随机选择一个server | 在index上随机, 选择index对应位置的server |
| ZoneAvoidanceRule | public class ZoneAvoidanceRule extends PredicateBasedRule | 复合判断server所在区域的性能和server的可用性选择server | 使用ZoneAvoidancePredicate和AvailabilityPredicate来判断是否选择某个server, 前一个判断判定一个zone的运行性能是否可用, 剔除不可用的zone (的所有server) , AvailabilityPredicate用于过滤掉连接数过多的Server.  |
