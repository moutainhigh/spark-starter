package info.spark.starter.es.mapper.container;

import info.spark.starter.es.support.ElasticsearchUtils;
import info.spark.starter.util.ReflectionUtils;
import info.spark.starter.es.entity.document.BaseElasticEntity;
import info.spark.starter.es.mapper.BaseElasticMapper;
import info.spark.starter.es.mapper.structure.Mapper;
import info.spark.starter.es.mapper.structure.MapperMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 管理所有的 name space，多个 （ dao <-> mapper.xml ） </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.22 11:37
 * @since 1.7.1
 */
@Slf4j
public class EsMapperNameSpace extends HashMap<String, Mapper> {
    /** serialVersionUID */
    private static final long serialVersionUID = 6655259349072430864L;

    /**
     * 将扫描到的 mapper class 解析为对象 注册到 内存中
     *
     * @param all all
     * @since 1.7.1
     */
    void register(List<Class<? extends BaseElasticMapper<?, ? extends BaseElasticEntity<?>>>> all) {
        for (Class<? extends BaseElasticMapper<?, ? extends BaseElasticEntity<?>>> aClass : all) {
            String classNameSpace = ElasticsearchUtils.getClassNameSpace(aClass);
            this.put(classNameSpace, this.buildOneMapper(classNameSpace, aClass));
        }
        this.put(BaseElasticMapper.class.getName(), this.buildBaseMapper());
    }

    /**
     * 组装 base mapper
     *
     * @return the mapper
     * @since 1.7.1
     */
    private Mapper buildBaseMapper() {
        return Mapper.builder()
            .nameSpace(BaseElasticMapper.class.getName())
            .mapperMethods(this.buildMapperMethod(BaseElasticMapper.class))
            .build();
    }

    /**
     * Build one mapper
     *
     * @param namespace namespace
     * @param clazz     a class
     * @return the mapper
     * @since 1.7.1
     */
    private Mapper buildOneMapper(String namespace, Class<? extends BaseElasticMapper<?, ? extends BaseElasticEntity<?>>> clazz) {
        Class<? extends BaseElasticEntity<?>> parameterType = ElasticsearchUtils
            .getInterfaceParameterType(clazz, BaseElasticMapper.class, 1);

        Mapper mapper = Mapper.builder()
            .nameSpace(namespace)
            .index(ElasticsearchUtils.getIndexFromBean(parameterType))
            .type(parameterType)
            .mapperXml(ElasticsearchUtils.allMapperPath(clazz))
            .mapperMethods(this.buildMapperMethod(clazz))
            .build();
        mapper.parseMapping();
        return mapper;
    }

    /**
     * 组装 {@link MapperMethod} 对象
     *
     * @param clazz a class
     * @return the list
     * @since 1.7.1
     */
    private List<MapperMethod> buildMapperMethod(Class<?> clazz) {
        Method[] methods = ReflectionUtils.getDeclaredMethods(clazz);
        log.debug("配置 es mapper ：[{}]", clazz);
        List<MapperMethod> mapperMethods = new ArrayList<>(methods.length);
        for (Method method : methods) {
            mapperMethods.add(MapperMethod.builder()
                                  .name(method.getName())
                                  .method(method)
                                  .paramsType(this.parseParams(method))
                                  .resultType(method.getReturnType())
                                  .params(method.getParameterCount())
                                  .build());
        }
        return mapperMethods;
    }

    /**
     * 解析参数
     *
     * @param method method
     * @return the class [ ]
     * @since 1.7.1
     */
    private Class<?>[] parseParams(Method method) {
        List<? extends Class<?>> collect = Arrays.stream(method.getParameters()).map(Parameter::getType).collect(Collectors.toList());
        return collect.toArray(new Class<?>[] {});
    }

}
