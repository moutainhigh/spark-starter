package info.spark.starter.es.register;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.util.StringUtils;
import info.spark.starter.es.entity.constant.ElasticStarterConstant;
import info.spark.starter.es.entity.document.BaseElasticEntity;
import info.spark.starter.es.mapper.BaseElasticMapper;
import info.spark.starter.es.mapper.container.EsMapperContextual;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Iterator;
import java.util.Set;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.21 17:20
 * @since 1.7.1
 */
@Slf4j
public class EsProMapperRegister implements PriorityOrdered, ImportBeanDefinitionRegistrar {

    /**
     * Register bean definitions
     *
     * @param importingClassMetadata importing class metadata
     * @param registry               registry
     * @since 1.7.1
     */
    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Iterator<Class<? extends BaseElasticMapper>> iterator = this.scanMapperInterfaces();

        while (iterator.hasNext()) {
            //noinspection unchecked
            Class<? extends BaseElasticMapper<?, ? extends BaseElasticEntity<?>>> mapper =
                (Class<? extends BaseElasticMapper<?, ? extends BaseElasticEntity<?>>>) iterator.next();
            log.info("加载到 es mapper：{}", mapper);
            // 扫描 继承了 base elastic mapper 并且有 @re注解的接口信息，为接口提供代理实现
            if (mapper.isInterface()) {
                this.registerEsMapper(registry, mapper);
                EsMapperContextual.MAPPER_CACHE.add(mapper);
            }
        }
    }

    /**
     * Register es mapper
     *
     * @param registry    registry
     * @param mapperClass mapper class
     * @since 1.7.1
     */
    @SneakyThrows
    private void registerEsMapper(BeanDefinitionRegistry registry, Class<? extends BaseElasticMapper> mapperClass) {
        String className = StringUtils.firstCharToLower(mapperClass.getSimpleName());
        // 设置 factory, 实现 es mapper client bean 的具体装配逻辑, 入口类
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(EsProFactoryBean.class);
        try {
            definition.addPropertyValue(ElasticStarterConstant.TYPE, mapperClass);
            definition.addAutowiredProperty("esMapperContextual");
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
        } catch (Exception e) {
            log.error(StrFormatter.format("[{}] 注册失败:", className), e);
            return;
        }
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary(true);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                                                               new String[] {className + ElasticStarterConstant.BEAN_HOLD_SUFFIX});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        log.info("注册 Es Mapper Client: [{}] [{}]", className, holder);
    }

    /**
     * Scan mapper interfaces
     *
     * @return the iterator
     * @since 1.7.1
     */
    @SuppressWarnings("rawtypes")
    private Iterator<Class<? extends BaseElasticMapper>> scanMapperInterfaces() {

        ConfigurationBuilder config = new ConfigurationBuilder();
        config.filterInputsBy(new FilterBuilder().includePackage(ConfigDefaultValue.BASE_PACKAGES));
        config.addUrls(ClasspathHelper.forPackage(ConfigDefaultValue.BASE_PACKAGES));
        config.setScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false));
        config.setExpandSuperTypes(false);
        Reflections reflections = new Reflections(config);
        Set<Class<? extends BaseElasticMapper>> subTypesOf = reflections.getSubTypesOf(BaseElasticMapper.class);
        return subTypesOf.iterator();
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.7.1
     */
    @Override
    public int getOrder() {
        return PriorityOrdered.LOWEST_PRECEDENCE;
    }
}
