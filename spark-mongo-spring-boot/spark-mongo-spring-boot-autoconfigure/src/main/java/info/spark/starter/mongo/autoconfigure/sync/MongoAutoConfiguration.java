package info.spark.starter.mongo.autoconfigure.sync;

import com.google.common.collect.Lists;

import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.context.EarlySpringContext;
import info.spark.starter.common.exception.PropertiesException;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.mongo.exception.MongoException;
import info.spark.starter.mongo.factory.MongoProviderFactory;
import info.spark.starter.mongo.index.CustomMongoPersistentEntityIndexCreator;
import info.spark.starter.mongo.spi.MongoLauncherInitiation;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.mongo.annotation.MongoCollection;
import info.spark.starter.mongo.core.MongoBean;
import info.spark.starter.mongo.mapper.MongoTemplateProxy;
import info.spark.starter.mongo.scanner.EntityScanner;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexCreator;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.mongodb.MongoClientOptions.builder;

/**
 * <p>Description: mongodb ??????????????? </p>
 * https://docs.spring.io/spring-data/mongodb/docs/2.2.1.RELEASE/reference/html
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.03 12:07
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnClass(value = {MongoClient.class, MongoLauncherInitiation.class})
@Import(value = {MongoDataAutoConfiguration.class})
public class MongoAutoConfiguration implements SparkAutoConfiguration {
    /** MONGODB_PROTOCOL */
    private static final String MONGODB_PROTOCOL = "mongodb://";

    /**
     * ?????? mongodb ???????????????.
     *
     * @param mongoProperties the mongo properties
     * @return the mongo client options
     * @since 1.0.0
     * @deprecated ?????? {@link ConnectionString} ??????
     */
    @Bean
    @Deprecated
    public MongoClientOptions mongoClientOptions(MongoProperties mongoProperties) {
        if (mongoProperties == null) {
            return new MongoClientOptions.Builder().build();
        }

        log.info("init mongoClientOptions...");
        return new MongoClientOptions.Builder()
            .minConnectionsPerHost(mongoProperties.getPool().getMinConnectionPerHost())
            .connectionsPerHost(mongoProperties.getPool().getMaxConnectionPerHost())
            .threadsAllowedToBlockForConnectionMultiplier(mongoProperties.getPool().getThreadsAllowedToBlockForConnectionMultiplier())
            .serverSelectionTimeout(mongoProperties.getPool().getServerSelectionTimeout())
            .maxWaitTime(mongoProperties.getPool().getMaxWaitTime())
            .maxConnectionIdleTime(mongoProperties.getPool().getMaxConnectionIdleTime())
            .maxConnectionLifeTime(mongoProperties.getPool().getMaxConnectionLifeTime())
            .connectTimeout(mongoProperties.getPool().getConnectTimeout())
            .socketTimeout(mongoProperties.getPool().getSocketTimeout())
            .sslEnabled(mongoProperties.getPool().getSslEnabled())
            .sslInvalidHostNameAllowed(mongoProperties.getPool().getSslInvalidHostNameAllowed())
            .heartbeatFrequency(mongoProperties.getPool().getHeartbeatFrequency())
            .minConnectionsPerHost(mongoProperties.getPool().getMinConnectionPerHost())
            .heartbeatConnectTimeout(mongoProperties.getPool().getHeartbeatConnectTimeout())
            .heartbeatSocketTimeout(mongoProperties.getPool().getSocketTimeout())
            .localThreshold(mongoProperties.getPool().getLocalThreshold())
            .build();
    }

    /**
     * Mongo provider factory (????????????????????????????????? MongoTemplate).
     *
     * @param mongoProperties    the mongo properties
     * @param mongoClientOptions the mongo client options
     * @param applicationContext application context
     * @param context            context
     * @param conversions        conversions
     * @return the mongo provider factory
     * @since 1.0.0
     */
    @Bean
    @Primary
    public MongoTemplate mongoTemplate(@NotNull MongoProperties mongoProperties,
                                       MongoClientOptions mongoClientOptions,
                                       ConfigurableApplicationContext applicationContext,
                                       MongoMappingContext context,
                                       MongoCustomConversions conversions) {

        Map<String, String> datasource = mongoProperties.getDatasource();

        // ?????????????????????????????????????????????????????????????????????????????????????????? default
        if (datasource.size() == 0) {
            this.addDefaultDatasource(datasource);
        }

        // ????????????????????????, ??????????????? default
        if (datasource.size() == 1 && datasource.get(MongoProperties.DEFAULT_DATASOURCE) == null) {
            throw new PropertiesException("????????? default ?????????????????????");
        } else if (datasource.size() == 1 && datasource.get(MongoProperties.DEFAULT_DATASOURCE).equals(StringPool.EMPTY)) {
            // ????????????????????????, ?????? value ????????????
            this.addDefaultDatasource(datasource);
        }

        // ?????????????????????, ?????????????????????
        List<String> defaultDatasourceConnectionString = Lists.newArrayListWithCapacity(1);

        datasource.forEach((k, v) -> {
            // ?????? default ??????
            if (k.equals(MongoProperties.DEFAULT_DATASOURCE)) {
                defaultDatasourceConnectionString.add(v);
            } else {
                // ????????????????????????
                this.initDatasource(k, v,
                                    mongoProperties,
                                    mongoClientOptions,
                                    applicationContext,
                                    context,
                                    conversions);
            }
        });

        if (defaultDatasourceConnectionString.size() != 1) {
            throw new PropertiesException("?????????????????????????????????.");
        }

        // ?????????????????????
        String defaultUri = defaultDatasourceConnectionString.get(0);
        try {
            // ?????? ConnectionString ?????? url, ?????????????????????
            new ConnectionString(defaultUri);
            // ????????????????????????
            this.initDatasource(MongoProperties.DEFAULT_DATASOURCE,
                                defaultUri,
                                mongoProperties,
                                mongoClientOptions,
                                applicationContext,
                                context,
                                conversions);
        } catch (Exception e) {
            // ???????????????????????????????????????????????????????????????
            if (!defaultUri.startsWith(MONGODB_PROTOCOL)) {
                // ??????????????? URL, ????????????????????????
                MongoTemplate defaultTemplate = MongoProviderFactory.getConfigureWithMongoTemplate(defaultUri);
                if (defaultTemplate == null) {
                    throw new PropertiesException("?????????????????????????????????: [{}]", defaultUri);
                }
                // ?????????????????????
                MongoProviderFactory.addConfigureWithMongoTemplate(MongoProperties.DEFAULT_DATASOURCE, defaultTemplate);
            }
        }

        this.scanEntity(mongoProperties.getScanPath(), applicationContext);
        return MongoProviderFactory.getConfigureWithMongoTemplate(MongoProperties.DEFAULT_DATASOURCE);
    }

    /**
     * ?????????????????????
     *
     * @param datasource datasource
     * @since 1.0.0
     */
    private void addDefaultDatasource(@NotNull Map<String, String> datasource) {
        datasource.put(MongoProperties.DEFAULT_DATASOURCE, "mongodb://127.0.0.1:27017/dev");
        log.warn("????????????????????????, ?????????????????????: [mongodb://127.0.0.1:27017/dev], "
                 + "??????: mongodb://[username:password@]host1[:port1][,...hostN[:portN]][/[defaultauthdb][?options]]");
    }

    /**
     * Init datasource *
     *
     * @param datasourceName     datasource name
     * @param datasourceUri      datasource uri
     * @param mongoProperties    mongo properties
     * @param mongoClientOptions mongo client options
     * @param applicationContext application context
     * @param context            context
     * @param conversions        conversions
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    private void initDatasource(String datasourceName,
                                String datasourceUri,
                                @NotNull MongoProperties mongoProperties,
                                MongoClientOptions mongoClientOptions,
                                ConfigurableApplicationContext applicationContext,
                                MongoMappingContext context,
                                MongoCustomConversions conversions) {
        // 1. ?????? dataSourceUri ?????? MongoClient dong4j : (2020???04???04??? 23:40) [???????????? mongoClient]
        MongoClient mongoClient = this.createMongoClient(mongoClientOptions, datasourceUri);
        // 2. ?????? MongoClient ?????? MongoDbFactory
        MongoDbFactory mongoDbFactory = createMongoDbFactory(datasourceUri);
        // 3. ?????????????????????
        MappingMongoConverter mappingMongoConverter = this.buildMappingMongoConverter(mongoDbFactory,
                                                                                      context,
                                                                                      conversions,
                                                                                      mongoProperties);
        // 4. ?????? MongoDbFactory ?????? MongoTemplate
        MongoTemplate mongoTemplate = createMongoTemplate(mongoDbFactory, mappingMongoConverter, mongoProperties.isEnableAutoCreateIndex());
        mongoTemplate.setApplicationContext(applicationContext);
        // 5. ?????? mongoTemplate
        this.registerMongoTemplate(datasourceName, applicationContext, mongoTemplate);
        this.processorIndexes(mongoProperties, applicationContext, context, datasourceName, mongoTemplate);
        // 6. ?????? dataSource ??? MongoTemplate ?????????
        MongoProviderFactory.addConfigureWithMongoTemplate(datasourceName, mongoTemplate);
        // 7. ?????? MongoTransactionManager
        MongoProviderFactory.addMongoTransactionTemplate(mongoTemplate,
                                                         new TransactionTemplate(this.transactionManager(mongoTemplate)));
    }

    /**
     * ??? mongoTemplate ????????? IoC, bean name = datasource + MongoTemplate
     *
     * @param datasourceName     datasource name
     * @param applicationContext application context
     * @param mongoTemplate      mongo template
     * @since 1.0.0
     */
    private void registerMongoTemplate(String datasourceName,
                                       ConfigurableApplicationContext applicationContext,
                                       MongoTemplate mongoTemplate) {
        EarlySpringContext.registerBean(applicationContext,
                                        datasourceName + MongoTemplate.class.getSimpleName(),
                                        mongoTemplate);
    }

    /**
     * ?????????????????????????????????
     *
     * @param mongoProperties    mongo properties
     * @param applicationContext application context
     * @param context            context
     * @param datasourceName     datasource name
     * @param mongoTemplate      mongo template
     * @since 1.0.0
     */
    private void processorIndexes(@NotNull MongoProperties mongoProperties,
                                  ConfigurableApplicationContext applicationContext,
                                  MongoMappingContext context,
                                  String datasourceName,
                                  MongoTemplate mongoTemplate) {
        // 4. ?????????????????????????????????, ???????????? IoC ???, ?????????????????????????????????????????????
        if (mongoProperties.isEnableAutoIncrementKey()) {
            try {

                EarlySpringContext.registerBean(applicationContext,
                                                datasourceName + MongoPersistentEntityIndexCreator.class.getSimpleName(),
                                                new CustomMongoPersistentEntityIndexCreator(context, mongoTemplate));
            } catch (Exception ex) {
                if (ex instanceof DataIntegrityViolationException) {
                    log.warn(ex.getMessage());
                } else {
                    log.error("", ex);
                }
            }
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param mongoTemplate mongo template
     * @return the mongo transaction manager
     * @since 1.0.0
     */
    @NotNull
    @Contract("_ -> new")
    private MongoTransactionManager transactionManager(@NotNull MongoTemplate mongoTemplate) {
        return new MongoTransactionManager(mongoTemplate.getMongoDbFactory());
    }

    /**
     * ?????? MongoClient
     *
     * @param options the options
     * @param uri     uri
     * @return the mongo client
     * @since 1.0.0
     */
    @NotNull
    private MongoClient createMongoClient(MongoClientOptions options, String uri) {
        try {
            return createNetworkMongoClient(options, uri);
        } finally {
            this.clearPassword();
        }
    }

    /**
     * Create mongo db factory simple mongo db factory
     *
     * @param dataSourceUri data source uri
     * @return the simple mongo db factory
     * @since 1.0.0
     */
    @Contract("_ -> new")
    @NotNull
    private static SimpleMongoClientDbFactory createMongoDbFactory(String dataSourceUri) {
        return new SimpleMongoClientDbFactory(dataSourceUri);
    }

    /**
     * Create mongo template mongo template
     *
     * @param mongoDbFactory        mongo db factory
     * @param enableAutoCreateIndex enable auto create index
     * @return the mongo template
     * @since 1.0.0
     */
    @NotNull
    @Contract("_ -> new")
    private static MongoTemplate createMongoTemplate(MongoDbFactory mongoDbFactory, boolean enableAutoCreateIndex) {
        if (enableAutoCreateIndex) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(MongoTemplate.class);
            enhancer.setCallback(new MongoTemplateProxy());
            Object[] objects = {mongoDbFactory};
            Class[] classes = {MongoDbFactory.class};
            return (MongoTemplate) enhancer.create(classes, objects);
        } else {
            return new MongoTemplate(mongoDbFactory);
        }
    }

    /**
     * ???????????????
     *
     * @param factory         factory
     * @param context         context
     * @param conversions     conversions
     * @param mongoProperties mongo properties
     * @return the mapping mongo converter
     * @since 1.0.0
     */
    private @NotNull MappingMongoConverter buildMappingMongoConverter(MongoDbFactory factory,
                                                                      MongoMappingContext context,
                                                                      MongoCustomConversions conversions,
                                                                      @NotNull MongoProperties mongoProperties) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        mappingConverter.setCustomConversions(conversions);

        if (!mongoProperties.isEnableSaveClassName()) {
            // ?????? _class
            mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        }
        // ???????????????????????? bean, ?????????????????????????????????
        mappingConverter.afterPropertiesSet();
        return mappingConverter;
    }


    /**
     * Create mongo template mongo template
     *
     * @param mongoDbFactory        mongo db factory
     * @param mongoConverter        mongo converter
     * @param enableAutoCreateIndex enable auto create index
     * @return the mongo template
     * @since 1.0.0
     */
    @Contract("_, _ -> new")
    @NotNull
    private static MongoTemplate createMongoTemplate(MongoDbFactory mongoDbFactory, @Nullable MongoConverter mongoConverter,
                                                     boolean enableAutoCreateIndex) {
        if (enableAutoCreateIndex) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(MongoTemplate.class);
            enhancer.setCallback(new MongoTemplateProxy());
            Object[] objects = {mongoDbFactory, mongoConverter};
            Class[] classes = {MongoDbFactory.class, MongoConverter.class};
            return (MongoTemplate) enhancer.create(classes, objects);
        } else {
            return new MongoTemplate(mongoDbFactory, mongoConverter);
        }
    }

    /**
     * ????????????, ?????? className ??? mongoTemplate ???????????????.
     * ?????????????????? @EnableEntityScanner ?????????????????????????????? basePackages ?????????, ??????????????????????????????????????????;
     * ??????????????? spark.mongo.scan-path ??? @EnableEntityScanner, ??????????????? spark.mongo.scan-path;
     *
     * @param scanPath           scan path
     * @param applicationContext application context
     * @since 1.0.0
     */
    @SneakyThrows
    private void scanEntity(List<String> scanPath, ApplicationContext applicationContext) {
        EntityScanner entityScanner = new EntityScanner(applicationContext);

        if (CollectionUtils.isEmpty(scanPath)) {
            scanPath = entityScanner.getScannerPackages();
            if (CollectionUtils.isEmpty(scanPath)) {
                log.warn("????????? spark.mongo.scan-path, "
                         + "????????? [{}] ???????????? root package ????????????, "
                         + "?????????????????????, ????????? @EnableEntityScanner ????????????????????? Mongodb Entity ??????!",
                         scanPath);
            }

        } else {
            entityScanner.setScannerPackages(scanPath);
        }

        // ?????????????????????
        Set<Class<?>> classesWithAnnotation = entityScanner.scan(MongoCollection.class);

        if (classesWithAnnotation.size() == 0) {
            log.warn("[{}] ?????????????????? @MongoCollection ???????????????, ???????????? MongoDataSource.getDataSource(Class claz) ???????????????", scanPath);
            return;
        }

        classesWithAnnotation.forEach(c -> {
            try {
                MongoCollection mongoCollection = AnnotationUtils.findAnnotation(c, MongoCollection.class);
                if (mongoCollection != null) {
                    MongoProviderFactory.addAnnotation2BeanMap(c.getName(), build(c, mongoCollection));
                }
            } catch (Exception e) {
                throw new MongoException(e.getMessage());
            }
        });
    }

    /**
     * Create network mongo client mongo client
     *
     * @param options options
     * @param uri     uri
     * @return the mongo client
     * @since 1.0.0
     */
    @NotNull
    @Contract("_, _ -> new")
    private static MongoClient createNetworkMongoClient(MongoClientOptions options, String uri) {
        return new MongoClient(new MongoClientURI(uri, builder(options)));
    }

    /**
     * Clear password
     *
     * @since 1.0.0
     */
    private void clearPassword() {

    }

    /**
     * ?????? MongoCollection ?????????????????????, ?????? datasource ?????????, ????????????????????????
     * ?????? collectionName ?????????, ???????????????(??????????????????) ??????
     *
     * @param clazz           clazz
     * @param mongoCollection the mongo collection
     * @return the mongo bean
     * @since 1.0.0
     */
    private static @NotNull MongoBean build(Class<?> clazz, @NotNull MongoCollection mongoCollection) {

        String collectionName = mongoCollection.value();
        if (StringUtils.isBlank(collectionName)) {
            collectionName = StringUtils.humpToUnderline(clazz.getSimpleName());
        }
        // ???????????????
        String datasource = mongoCollection.datasource();
        String desc = mongoCollection.desc();

        MongoBean mongoBean = new MongoBean();
        mongoBean.setCollectionName(collectionName);
        mongoBean.setDesc(desc);

        if (StringUtils.isBlank(datasource)) {
            mongoBean.setMongoTemplate(MongoProviderFactory.getConfigureWithMongoTemplate(MongoProperties.DEFAULT_DATASOURCE));
        } else {
            mongoBean.setMongoTemplate(MongoProviderFactory.getConfigureWithMongoTemplate(datasource));
        }

        return mongoBean;
    }

}
