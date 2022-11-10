package info.spark.starter.mongo.autoconfigure.sync;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.mongo.handler.DefaultTimeMetaHandler;
import info.spark.starter.mongo.listener.AutoCreateIndexEventListener;
import info.spark.starter.mongo.listener.AutoCreateKeyEventListener;
import info.spark.starter.mongo.listener.AutoCreateTimeEventListener;
import info.spark.starter.mongo.listener.AutoIncrementKeyEventListener;
import info.spark.starter.mongo.spi.MongoLauncherInitiation;
import info.spark.starter.util.core.metadata.MetaObjectHandler;

import com.mongodb.MongoClient;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.11 22:20
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnClass(value = {MongoClient.class, MongoLauncherInitiation.class})
@Import(value = {MongoAutoConfiguration.class})
public class MongoEventListenerAutoConfiguration implements SparkAutoConfiguration {

    /** Meta object handler */
    private MetaObjectHandler metaObjectHandler;

    /**
     * Mongo event listener auto configuration
     *
     * @param metaObjectHandlerObjectProvider meta object handler object provider
     * @since 1.0.0
     */
    public MongoEventListenerAutoConfiguration(@NotNull ObjectProvider<MetaObjectHandler> metaObjectHandlerObjectProvider) {
        this.metaObjectHandler = metaObjectHandlerObjectProvider.getIfAvailable();
        if (this.metaObjectHandler == null) {
            this.metaObjectHandler = new DefaultTimeMetaHandler();
        }
    }

    /**
     * 自动生成 id 且自增
     *
     * @param mongoTemplate mongo template
     * @return the abstract mongo event listener
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(name = ConfigKey.MongoConfigKey.ENABLE_AUTO_INCREMENT_KEY, havingValue = "true")
    public AbstractMongoEventListener<?> autoIncrementKeyEventListener(MongoTemplate mongoTemplate) {
        return new AutoIncrementKeyEventListener(mongoTemplate);
    }

    /**
     * 自动生成 id (默认开启)
     *
     * @param properties properties
     * @return the abstract mongo event listener
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(name = ConfigKey.MongoConfigKey.ENABLE_AUTO_CREATE_KEY, havingValue = "true", matchIfMissing = true)
    public AbstractMongoEventListener<?> autoCreateKeyEventListener(@NotNull MongoProperties properties) {
        return new AutoCreateKeyEventListener(properties.isEnableAutoIncrementKey());
    }

    /**
     * 自动填充 create time 和 update time
     *
     * @return the abstract mongo event listener
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(name = ConfigKey.MongoConfigKey.ENABLE_AUTO_CREATE_TIME, havingValue = "true")
    public AbstractMongoEventListener<?> autoCreateTimeEventListener() {
        return new AutoCreateTimeEventListener(this.metaObjectHandler);
    }

    /**
     * 分表时自动创建索引
     *
     * @return the abstract mongo event listener
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(name = ConfigKey.MongoConfigKey.ENABLE_AUTO_CREATE_INDEX, havingValue = "true")
    public AbstractMongoEventListener<?> autoCreateIndexEventListener() {
        return new AutoCreateIndexEventListener();
    }

}
