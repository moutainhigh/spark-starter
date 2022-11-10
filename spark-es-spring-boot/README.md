# Spark Elasticsearch Starter

## 项目简介

> 该项目使用开源的bboss-es组件，对此进行了封装，目前在bboss-es能力基础上提供了类似mybatis-plus一样的功能：

> 1. spark功能：自动关联xml脚本，不需要手写名字
> 2. spark功能：只需要定义es dao层接口，项目启动自动生成代理对象
> 3. spark功能：提供了简单crud功能
> 4. spark功能：提供了ORM自动创建index的功能
> 4. bboss核心功能：像mybatis一样将es脚本写入xml中维护，支持热部署，详情请查看bboss-es官方说明文档

未来打算考虑集成第二个组件想spring-data-es那样使用代码的方式组装条件对象，进行查询；
[bboss-es官方文档](https://esdoc.bbossgroups.com/#/README)

## bboss在代码层次上的优点

1. ORM和DSL二者兼顾，类mybatis方式操作ElasticSearch
2. 将es脚本维护到xml中，支持热部署，供丰富的逻辑判断语法,在dsl脚本中可以使用变量、脚本片段、foreach循环、逻辑判断、注释；
3. 支持分表功能，通过配置后缀，（只支持年月等）
4. 其他服务节点、可重连的优点可查询官方文档

## bboss在代码层次上的缺点

1. 对开发人员要求过高，需要熟练掌握es脚本语法
2. 对开发人员要求过高，需要熟练掌握es脚本语法
3. 对开发人员要求过高，需要熟练掌握es脚本语法
4. 目前官网并没有提供在`idea`的开发插件，没有任何提示，没有任何语法高亮，导致在xml文件中手写es脚本容易出错（笔者在2021年11月在bboss官方QQ群询问过群主，确实没有提供`idea`插件，多半也不会考虑未来提供）
5. bboss-es中提供了es脚本动态语法，导致需要花时间单独去学习他的语法特点，在编写xml中的es脚本时，也就更容易出错了！
6. 维护成本太大，对于之后接手项目的新人，99%是第一次接触使用bboss的语法来写es的，面对小小复杂的动态语法，即可搞晕他

## 项目结构

```
.
├── spark-es-spring-boot-autoconfigure
├── spark-es-spring-boot-core
└── spark-es-spring-boot-starter
```

## 简要说明

使用详情请查看 `spark-framework-guide`项目

### 项目启动

1. `EsProMapperRegister#registerBeanDefinitions`方法将扫描所有继承了`BaseElasticMapper`接口的接口类。
2. 组装`BeanDefinition`注册到`IoC`容器中，该`BeanDefinition`为`EsProFactoryBean`工厂bean。
3. 将接口`Class`放入`EsMapperContextual.MAPPER_CACHE`缓存中，后面使用。
4. `ElasticMapperContextualRegisListener#onApplicationStartedEvent`开始处理`EsMapperContextual.MAPPER_CACHE`
   中的接口信息，解析xml位置、方法名、namespace、index、index_mapping等信息，组装为`structure.mapper.info.spark.starter.es.Mapper`对象放入缓存中
5. 初始化单例对象，为接口创建代理对象`EsSimpleProxy`，代码位置：`EsProFactoryBean#createInstance`

### 请求调用

1. 注入接口
2. 调用方法
3. 进入到接口动态代理实现对象的invoke方法中，`EsSimpleProxy#invoke`
4. 代理对象`EsSimpleProxy`预处理拿到`Mapper`对象信息
5. 代理对象`EsSimpleProxy`委托`BaseProxyProcessor#handler`继续后续操作
6. 如果调用的父接口中提供的基础方法，则调用`MapperSpringboardService`跳板机类进行方法调用，跳板机类聚合了`ElasticIndexService、ElasticCrudService`
7. 如果调用的是自定义方法，则获取bboss的`ClientInterface`接口，在service实际调用该方法时，需要传入`lamda`表达式编写逻辑；（1.9.0后父接口中提供了getBaseClient方法，可以在service中直接得到client进行操作）

## Q&A

### Q：新增时候，没有自动创建index，报错index不存在怎么办

A：定义index对象、定义index对象和对应的mapper接口，不要直接注入`ElasticCrudService`来进行新增等操作，这个类是最底层的封装，没有判断index是否存在，然后去自动创建的逻辑；index对象根据需要打上ORM注解，**
（业务不需要全文检索的建议不要打上分词ORM注解，这样分词索引会大量占用服务器内存空间，我看v4使用es好像全是用的es默认的mapping_setting，我记得2020年，苏建他们组就经常说es内存占用很大，可能就是这些地方导致的）**

### Q：update方法，我只想update某几个字段，但是把我document文档其他字段全部删除不见了？

A：这个问题是因为bboss-es使用的http-client与elastic进行交互，参数类型是json，序列化时将没有值的字段转化为null了，导致es更新document，字段全部丢失；如果需要只更新几个字段，需要在document实体（继承了`BaseElasticEntity`
）上添加注解 <b>@JsonInclude(JsonInclude.Include.NON_NULL</b>；如：

```java
/**
 * <p>Description:  </p>
 * JsonInclude 这个注解是 在使用 update 等方法时候，没有set的字段，就不会json为"null"，导致更新覆盖
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.09.29 10:49
 * @since 2.0.0
 */
@Data
@ESIndex(name = "es_demo_document")
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsDemoDocument extends BaseElasticEntity<Long> {

    /** serialVersionUID */
    private static final long serialVersionUID = -5023427171170360993L;
    /** ESId必须有 */
    @ESId
    private Long id;
    /** 可以不写 @EsMapping */
    private String name;
    /** 可以不写 @EsMapping */
    private String desc;
}
```
