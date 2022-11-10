#### 问题原因

在自动创建的的索引的监听器中使用了操作集合的方法

1.mongoTemplate.collectionExists(collectionName)导致报错；mongodb 4.2以下**不支持在事物中使用操作集合的方法**(任何操作集合的方法)。

2.Spring-Data-MongoDB组建最新版(3.2.5)也不支持在事物中操作集合。

3.**mongodb4.4支持在事物中创建索引。**



#### 解决方案

一.由使用者自行判断。

1.通过文档规范，如果使用事物，就需要自行创建集合以及索引；同时提供创建集合以及索引的方法(结合手动配置在实体上的索引)

2.通过动态代理以及ThreadLocal类，判断在插入数据时是否使用了事物，使用了事物不在执行监听器创建索引的方法。

缺点：需要使用者自行调用，增加了业务代码的繁杂度。

二.可以使用MongoTemplate提供的原生类MongoDatabase完成集合操作(mongoTemplate.getMongoDbFactory())，并且不影响其它流程。

需要修改:

    1.判断集合 boolean exists = mongoTemplate.collectionExists(collectionName);

 	2.创建所以静态方法 util.info.spark.starter.mongo.IndexUtils#createIndexes

缺点：mongodb服务器必须要求4.4及以上版本。
