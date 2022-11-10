package info.spark.starter.es.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.es.config.ElasticsearchProperties;
import info.spark.starter.es.mapper.container.EsMapperContextual;
import info.spark.starter.es.mapper.container.EsMapperNameSpace;
import info.spark.starter.es.register.EsProMapperRegister;
import info.spark.starter.es.service.ElasticMapperService;
import info.spark.starter.es.service.MapperSpringboardService;
import info.spark.starter.es.proxy.BaseProxyProcessor;
import info.spark.starter.es.service.ElasticCrudService;
import info.spark.starter.es.service.ElasticIndexService;

import org.apache.commons.lang3.BooleanUtils;
import org.frameworkset.elasticsearch.boot.BBossESProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;


/**
 * <p>Description: </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 17:59
 * @since 1.7.1
 */
@Import({EsProMapperRegister.class})
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchAutoConfiguration implements SparkAutoConfiguration {

    /**
     * if ues bBoss, build custom properties
     *
     * @param elasticsearchProperties elasticsearch properties
     * @param bossEsProperties       b boss es properties
     * @since 2.0.0
     */
    @Primary
    @Bean(name = "bBossESProperties")
    @ConditionalOnClass(name = "org.frameworkset.elasticsearch.boot.BBossESStarter")
    public BBossESProperties bossEsPropRebuild(ElasticsearchProperties elasticsearchProperties, BBossESProperties bossEsProperties) {
        if (null != bossEsProperties.getElasticsearch()) {
            bossEsProperties.getElasticsearch().setShowTemplate(BooleanUtils.isTrue(elasticsearchProperties.getShowLog()) + "");
            bossEsProperties.getElasticsearch().setDateFormat(elasticsearchProperties.getDateFormat());

            if (null != bossEsProperties.getElasticsearch().getRest()) {
                bossEsProperties.getElasticsearch().getRest().setHostNames(elasticsearchProperties.getHost());
            }
        }
        bossEsProperties.setElasticUser(elasticsearchProperties.getUser());
        bossEsProperties.setElasticPassword(elasticsearchProperties.getPassword());
        return bossEsProperties;
    }

    /**
     * index相关操作 client
     *
     * @return the b boss es pro client
     * @since 1.7.1
     */
    @Bean(name = "elasticIndexService")
    public ElasticIndexService elasticIndexService() {
        return new ElasticIndexService();
    }

    /**
     * curd相关操作 client
     *
     * @return the b boss es pro client
     * @since 1.7.1
     */
    @Bean(name = "elasticCrudService")
    public ElasticCrudService elasticCrudClient() {
        return new ElasticCrudService();
    }

    /**
     * Elastic mapper client
     *
     * @return the elastic mapper client
     * @since 1.7.1
     */
    @Bean(name = "elasticMapperService")
    public ElasticMapperService elasticMapperClient() {
        return new ElasticMapperService();
    }

    /**
     * Es mapper contextual
     *
     * @param esMapperNameSpace es mapper name space
     * @return the es mapper contextual
     * @since 1.7.1
     */
    @Bean(name = "esMapperContextual")
    public EsMapperContextual esMapperContextual(EsMapperNameSpace esMapperNameSpace) {
        return new EsMapperContextual(esMapperNameSpace);
    }

    /**
     * Es mapper name space
     *
     * @return the es mapper name space
     * @since 1.8.0
     */
    @Bean
    public EsMapperNameSpace esMapperNameSpace() {
        return new EsMapperNameSpace();
    }

    /**
     * baseMapper中的跳板方法
     *
     * @return the mapper springboard service
     * @since 1.8.0
     */
    @Bean
    public MapperSpringboardService mapperSpringboardService() {
        return new MapperSpringboardService();
    }

    /**
     * Base proxy processor
     *
     * @return the base proxy processor
     * @since 1.7.1
     */
    @Bean
    @DependsOn({"elasticCrudService", "elasticMapperService", "esMapperContextual"})
    public BaseProxyProcessor baseProxyProcessor() {
        return new BaseProxyProcessor();
    }
}
