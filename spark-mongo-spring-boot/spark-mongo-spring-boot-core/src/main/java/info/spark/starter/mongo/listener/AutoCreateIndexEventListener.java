package info.spark.starter.mongo.listener;

import com.google.common.collect.Sets;

import info.spark.starter.mongo.datasource.MongoContextHolder;
import info.spark.starter.mongo.datasource.MongoDataSource;
import info.spark.starter.util.StringUtils;
import info.spark.starter.mongo.mapper.MongoPO;
import info.spark.starter.mongo.util.IndexUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 自动生成索引, 解决直接使用 collection name 时不创建索引的问题  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.17 14:27
 * @since 1.0.0
 */
@Slf4j
public class AutoCreateIndexEventListener extends AbstractMongoEventListener<MongoPO<?, ?>> {
    /** 缓存已处理过的 collection */
    private final Set<String> initIndexedCollectionNames = Sets.newHashSetWithExpectedSize(8);

    /**
     * 判断 DB 是否存在指定的 collection name, 不存在则创建索引
     *
     * @param event event
     * @see MongoPO
     * @since 1.0.0
     */
    @Override
    public void onBeforeSave(@NotNull BeforeSaveEvent<MongoPO<?, ?>> event) {
        try {
            MongoPO<?, ?> source = event.getSource();

            // 具体操作时传入的 collection name
            String collectionName = event.getCollectionName();
            if (StringUtils.isBlank(collectionName)) {
                // 获取原始的 collection name
                collectionName = MongoPO.collection(source.getClass());
                if (StringUtils.isBlank(collectionName)) {
                    collectionName = StringUtils.humpToUnderline(source.getClass().getSimpleName());
                }
            }

            // 已经处理过则退出
            if (!this.initIndexedCollectionNames.contains(collectionName)) {
                // 使用动态代理和私有线程解决不同数据源创建表时，数据和索引分开创建的问题
                MongoTemplate mongoTemplate = MongoContextHolder.getDatasource();
                if (mongoTemplate == null) {
                    mongoTemplate = MongoDataSource.getDataSource(source.getClass());
                }
                boolean exists = mongoTemplate.collectionExists(collectionName);
                if (!exists) {
                    // 为指定的 collectionName 创建索引
                    IndexUtils.createIndexes(mongoTemplate, source.getClass(), collectionName);
                }
                this.initIndexedCollectionNames.add(collectionName);
            }
        } catch (Exception e) {
            log.error("MongoTemplate插入监听器错误", e);
        } finally {
            MongoContextHolder.clear();
        }
    }

}
