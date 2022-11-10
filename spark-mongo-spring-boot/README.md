# Spark Mongo Starter

## 依赖

```xml
<dependency>
    <groupId>info.spark</groupId>
    <artifactId>spark-mongo-spring-boot-starter</artifactId>
</dependency>
```
## 功能特性

1.支持配置多个mongo数据源，可以通过提供的方法切换数据源，不切换使用默认数据源。
2.支持mongodb服务器的单机、副本集、集群等部署模式。
3.使用@MongClient注解即可完成 ActiveRecord 模式的封装 API,使得操作更加简洁。
4.支持每个数据源的事物操作(mongodb版本要4.0以上)。

## 配置说明

```yaml
spark:
  mongo:
    datasource:
        # 格式  mongodb://[username:password@]host1[:port1][,...hostN[:portN]][/[defaultauthdb][?options]]");
        # 默认数据源
        default: mongodb://127.0.0.1:27017/defaultDb
        # 副本集配置
        replicaSetDb: mongodb://useraa:123456@192.168.2.80:27017,192.168.2.80:27018,192.168.2.80:27019/aa?replicaSet=repl
  # @MongoCollection扫描路径 (默认跟路径)
  scan-path: /
  # 字段转换策略 (大小写，下划线转换等。默认值org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy，其策略是将驼峰命名的实例属性转变为下划线，如homeAddress-> home_address)
  field-naming-strategy: org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy
  # 是否开启 id 自增 (默认false,id必须为Long类型)
  enable-auto-increment-key: false
  # 是否自动生成 id(默认true)
  enable-auto-create-key: true
  # 是否自动生成创建和更新时间字段(默认false)
  enable-auto-create-time: false
  # 是否保存 class name 到 _class 字段中 (默认false)
  enable-save-class-name: false
  # 是否自动创建索引(默认false, id必须为Long)
  enable-auto-create-index: false
  # 连接池配置 以下值都为默认值
  pool:
      # 每个host允许链接的最小链接数
      min-connection-per-host: 0
      # 每个host允许链接的最大链接数
      max-connection-per-host: 100
      # 应许阻塞的线程连接乘数(它以connectionsPerHost值相乘的结果就是线程队列最大值)
      threads-allowed-to-block-for-connection-multiplier: 5
      # MongoDB Client需要找到可用的MongoDB Server所需要的等待时间
      server-selection-timeout: 30000
      # 应用程序处理线程从连接池中获取Collection，对应的网络等待时间
      max-wait-time: 120000
      #  线程池中连接的最大空闲时间, 0标志Udine空闲时间没有限制,超过这个时间会被关闭
      max-connection-idle-time: 0
      # 线程池中连接的最长生存时间. 0表示没有限制. 超过寿命的会被关闭,必要时通过新连接进行替换.
      max-connection-life-time: 0
      # 连接超时时间,必须大于0
      connect-timeout: 10000
      # socket超时时间
      heartbeat-socket-timeout: 0
      # ssl启用
      ssl-enabled: false
      # 允许ssl无效主机名
      ssl-invalid-host-name-allowed: false
      # 心跳频率。驱动程序将尝试确定群集中每个服务器的当前状态的频率。
      heartbeat-frequency: 10000
      # 设置最小心跳频率。 如果驱动程序必须经常重新检查服务器的可用性，它将至少在上一次检查后等待很长时间，以避免浪费精力。
      min-heartbeat-frequency: 500
      # 集群心跳的连接的连接超时
      heartbeat-connect-timeout: 20000
      # 本地阈值
      local-threshold: 15
```

## 使用说明

1.通过注入的方式获取MongoTemplate, database + MongoTemplate的模式可以得到相关数据源的MongoTemplate；如何不存在对应的数据源则会使用默认数据源。

```java
    // 获取old数据源的MongoTemplate
    @Resource
    private MongoTemplate oldMongoTemplate;

    @Test
    void test_1() {
        Map<String, Object> map = new HashMap<>();
        map.put("name","eed");
        map.put("age",12);
        oldMongoTemplate.insert(map,"sss");
    }
```

2.基本使用MongoDataSource获取MongoTemplate，以及切换数据源

```java
@Test
void test_4() {
    //使用默认数据源
    MongoTemplate mongoTemplate = MongoDataSource.getDataSource();
    mongoTemplate.insert(new UserUsingLong());
    // 切换数据源
    MongoTemplate oldMongoTemplate = MongoDataSource.getDataSource("old");
    oldMongoTemplate.insert(new User());
}
```
3.使用@MongoCollection,配置ActiveRecord类型的实体，需要继承MongoPO

**@MongoCollection(**配置在类上) 参数配置说明
**value**: 集合名词,默认使用类名的简写作为集合名称，同时驼峰会转为下划线如 UserHomeInfo->user_home_info
**desc**: 描述
**datasource**: 数据源(默认使用默认数据源)。如: @MongoCollection(datasource="old") 使用old数据源
**type**: (版本大于3.0的不起作用，可不管)集合类型, 默认为普通类型(ORDINARY)，默认会创建索引, 分表(SHARDING)类型不会自动创建索引

(注: 此处的自动创建索引是使用MongoTemplate本身的机制**与enable-auto-create-key无任何关系**，在对象上使用@Index(对应的@MongoColumn(Index="name_index"))创建索引，但是在mongodb的3.x版本后，停止了使用@Index,@CompoundIndex(对应本组建的@MongoCollection(def="{name:1}"))自动创建索引，
因此当使用的mongodb版本大于了3.0，type不管是何值，都不会自动创建索引)
**def**: 联合索引，使用JSON格式，JSON 文档的键是要索引的字段, 值定义索引方向 (1表示升序, -1表示降序)。如果只对一个字段做索引时，并且该字段又被@MongoColumn所注解，def的优先级高于@MongoColumn
如 @MongoCollection(def = "{username:1,nickname:1}",name="username_nickname_index")
**name**: def创建的索引名称
**unique**: def设置的索引是否唯一(默认false)
**sparse**: 如果设置为true, 则索引将跳过缺少索引字段的任何文档.(默认false)
**useGeneratedName**: 如果设置为true, 那么MongoDB将忽略给定的索引名(name), 而是生成一个新名称
**background**: 如果为true, 则将在后台创建索引.

**@MongoColumn**(配置在字段上)
**index**: 当前字段添加索引，值为索引名称  如：@MongoColumn(index = "email_index")
**fill**: 字段自动填充策略

```java
// 若设置为id自动增长,则ObjectId为Long类型(MongoPO<Long, User>)
@MongoCollection(value = "user",datasource="old")
public class User extends MongoPO<ObjectId, User> {
    /** serialVersionUID */
    private static final long serialVersionUID = -2667532070881853044L;
    /** User name */
    private String username;
    /** Email */
    @MongoColumn(index = "email_index")
    private String email;
    /** Password */
    private String password;
}
```

```java
@Test
void test_1() {
    User user = new User();
    user.setUsername("1");
    user.setNicename("1");
    user.insert();
    // 获取MongoTemplate
    MongoDataSource.getDataSource(User.class).insert(new User());

    User user1 = user.selectById(2L);
    user.setUsername("sdfe4w3").updateById();
    user.deleteById();
}
```

4.字符串转换,可将驼峰的字符转换为下划线，也可将下划线字符转换为驼峰式的

```java
@Test
void test3() {
    //驼峰转换为下划线
    log.info("{}", StringUtils.humpToUnderline("userName"));
    // 下划线转换为驼峰
    log.info("{}", StringUtils.underlineToHump("user_name"));
    // 中横线转换为驼峰
    log.info("{}", StringUtils.lineToHump("user-name"));
}
```

5.使用事物完成异常回滚(目前事物存在一定问题，还不能使用)

```java
public void updateWithTransaction() {
    MongoDataSource.getMongoTransactionTemplate(MongoDataSource.getDataSource(User.class)).execute(new TransactionCallback<String>(){
        @Override
        public String doInTransaction(TransactionStatus transactionStatus){
            User user = new User();
            user.setNicename("xxxxxx");
            user.insert();
            //测试事务回滚
            int i=1/0;
            return user.getUsername();
        }
    });
}
```

## 注意事项

1.public class User extends MongoPO<ObjectId, User>{} 若设置了id自增，需要将ObjectId类型该为Long类型，才可达到自增效果。

```java
@MongoCollection
public class User extends MongoPO<Long, User> {}
```

2.如果使用一个数据源的MongoTemplate去插入另外一个数据源的实例对象，以MongoTemplate的数据源为准。
```java
//使用old数据源的对象
@MongoCollection(collection = "userinfo",datasource="old")
public class User extends MongoPO<ObjectId, User> {}

// 使用默认数据源的MongoTemplate
@Resource
private MongoTemplate mongoTemplate;
@Test
void test_2() {
    User user = new User();
    user.setUsername("loong");
    user.setNicename("zz");

    // 使用的是mongoTemplate的数据源而非old的
    mongoTemplate.insert(user);
}
```
## todo

- [ ] 自定义是否生成 时间审计字段

## Fix

1. 忽略 `_class` 失效
2. 在待迁移的数据库中创建了分表, 且创建了索引, 在备份数据库中的分表没有自动创建索引
3. 迁移过程中可能出现的 id 重复导致迁移失败

