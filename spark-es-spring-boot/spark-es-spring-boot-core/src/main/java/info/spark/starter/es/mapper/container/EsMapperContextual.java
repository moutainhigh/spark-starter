package info.spark.starter.es.mapper.container;

import info.spark.starter.es.entity.document.BaseElasticEntity;
import info.spark.starter.es.mapper.BaseElasticMapper;
import info.spark.starter.es.mapper.structure.Mapper;
import info.spark.starter.es.support.ElasticsearchUtils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import lombok.Data;

/**
 * <p>Description: es mapper 上下文 </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.22 11:40
 * @since 1.7.1
 */
@Data
public final class EsMapperContextual {

    /** Name space */
    private final EsMapperNameSpace nameSpace;

    /** Mapper cache */
    public static final List<Class<? extends BaseElasticMapper<?, ? extends BaseElasticEntity<?>>>> MAPPER_CACHE = new LinkedList<>();

    /**
     * Es mapper contextual
     *
     * @param nameSpace nameSpace
     * @since 1.7.1
     */
    public EsMapperContextual(EsMapperNameSpace nameSpace) {
        this.nameSpace = nameSpace;
    }

    /**
     * Build
     *
     * @param classSet class set
     * @since 1.7.1
     */
    public void build(List<Class<? extends BaseElasticMapper<?, ? extends BaseElasticEntity<?>>>> classSet) {
        this.nameSpace.register(classSet);
    }

    /**
     * Gets mapper *
     *
     * @param method method
     * @return the mapper
     * @since 1.7.1
     */
    public Mapper getMapper(Method method) {
        return this.getNameSpace().get(ElasticsearchUtils.getClassNameSpace(method.getDeclaringClass()));
    }
}
