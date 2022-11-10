package info.spark.starter.es.proxy;

import info.spark.starter.common.context.SpringContext;
import info.spark.starter.es.entity.document.BaseElasticEntity;
import info.spark.starter.es.mapper.BaseElasticMapper;
import info.spark.starter.es.mapper.container.EsMapperContextual;
import info.spark.starter.es.mapper.structure.Mapper;
import info.spark.starter.es.support.ElasticsearchUtils;
import info.spark.starter.util.BeanUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

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
public class EsSimpleProxy implements InvocationHandler {

    /** Es mapper contextual */
    private final EsMapperContextual esMapperContextual;
    /** Base proxy processor */
    private BaseProxyProcessor baseProxyProcessor;
    /** Target type */
    private final Class<?> targetType;
    /** Index */
    private final String index;

    /**
     * Es simple proxy
     *
     * @param targetType         target type
     * @param esMapperContextual es mapper contextual
     * @since 1.7.1
     */
    @Contract(pure = true)
    public EsSimpleProxy(Class<?> targetType, EsMapperContextual esMapperContextual) {
        this.targetType = targetType;
        this.esMapperContextual = esMapperContextual;
        Class<? extends BaseElasticEntity> beanType = ElasticsearchUtils.getInterfaceParameterType(this.targetType,
                                                                                                   BaseElasticEntity.class, 0);
        this.index = ElasticsearchUtils.getIndexFromBean(beanType);
    }

    /**
     * Invoke
     *
     * @param proxy  proxy
     * @param method method
     * @param args   args
     * @return the object
     * @since 1.7.1
     */
    @SneakyThrows
    @Override
    public Object invoke(Object proxy, @NotNull Method method, Object[] args) {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        Mapper mapper = this.esMapperContextual.getMapper(method);
        // 如果是base里面的方法，初始化的时候是没有 给每一个接口 mapper 都组装对应类型的对象的。
        if (BaseElasticMapper.class.getName().equals(mapper.getNameSpace())) {
            Mapper targetMapper = this.esMapperContextual.getNameSpace().get(this.targetType.getName());
            Mapper copy = BeanUtils.copy(mapper, Mapper.class);
            copy.setIndex(targetMapper.getIndex());
            copy.setType(targetMapper.getType());
            copy.setMapping(targetMapper.getMapping());
            copy.setMapperXml(targetMapper.getMapperXml());
            mapper = copy;
        }

        if (null == this.baseProxyProcessor) {
            this.baseProxyProcessor = SpringContext.getInstance(BaseProxyProcessor.class);
        }
        return this.baseProxyProcessor.handler(mapper, method, args);

    }
}
