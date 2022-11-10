# todo

实体没有使用 @MongoCollection 标记时, 插入数据不会自动创建索引

实体使用 @MongoCollection 标记时, 使用 `MongoTemplate.insert(Object, collectionName)` 时, 如果 collectionName 不存在, 也不会创建索引

