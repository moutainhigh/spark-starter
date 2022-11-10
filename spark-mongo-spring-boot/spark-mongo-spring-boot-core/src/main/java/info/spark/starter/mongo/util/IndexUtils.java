package info.spark.starter.mongo.util;

import com.google.common.collect.Lists;

import info.spark.starter.common.context.SpringContext;
import info.spark.starter.mongo.factory.MongoProviderFactory;
import info.spark.starter.mongo.index.CustomMongoPersistentEntityIndexResolver;
import info.spark.starter.util.ObjectUtils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Arrays;
import java.util.List;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.05 21:11
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class IndexUtils {
    /** mongoMappingContext */
    private static MongoMappingContext mongoMappingContext;

    /**
     * 为指定的实体添加索引:
     * 1. 如果传 collectionName, 将使用 clazz 的 collectionName
     *
     * @param mongoTemplate  mongo template
     * @param clazz          clazz
     * @param collectionName collection name
     * @since 1.0.0
     */
    public static void createIndexes(@NotNull MongoTemplate mongoTemplate, Class<?> clazz, String... collectionName) {

        List<String> collectionNames = Lists.newArrayList();
        if (ObjectUtils.isEmpty(collectionName)) {
            collectionNames.add(MongoProviderFactory.collectionName(clazz));
        } else {
            Arrays.stream(collectionName).filter(StringUtils::isNotBlank).forEach(collectionNames::add);
        }

        if (mongoMappingContext == null) {
            mongoMappingContext = SpringContext.getInstance(MongoMappingContext.class);
        }

        IndexResolver resolver = new CustomMongoPersistentEntityIndexResolver(mongoMappingContext);

        try {
            collectionNames.forEach(collection -> {
                IndexOperations indexOps = mongoTemplate.indexOps(collection);
                resolver.resolveIndexFor(clazz).forEach(indexOps::ensureIndex);
            });
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
    }
}
