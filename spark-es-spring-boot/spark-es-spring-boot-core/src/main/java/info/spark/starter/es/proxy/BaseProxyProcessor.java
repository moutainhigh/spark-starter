package info.spark.starter.es.proxy;

import info.spark.starter.es.entity.DynamicMapperClient;
import info.spark.starter.es.entity.constant.ElasticStarterConstant;
import info.spark.starter.es.exception.EsErrorCodes;
import info.spark.starter.es.mapper.BaseElasticMapper;
import info.spark.starter.es.mapper.container.EsMapperContextual;
import info.spark.starter.es.mapper.structure.Mapper;
import info.spark.starter.es.mapper.structure.MapperMethod;
import info.spark.starter.es.service.ElasticMapperService;
import info.spark.starter.es.service.MapperSpringboardService;
import info.spark.starter.es.support.ElasticsearchUtils;
import info.spark.starter.util.ReflectionUtils;

import org.frameworkset.elasticsearch.client.ClientInterface;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.annotation.Resource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.22 16:48
 * @since 1.7.1
 */
@Slf4j
@Data
public class BaseProxyProcessor {

    /** Mapper springboard service */
    @Resource
    private MapperSpringboardService mapperSpringboardService;
    /** Elastic mapper client */
    @Resource
    private ElasticMapperService elasticMapperService;
    /** Es mapper contextual */
    @Resource
    private EsMapperContextual esMapperContextual;

    /**
     * Template
     *
     * @param method method
     * @param args   args
     * @return the object
     * @since 1.7.1
     */
    public Object template(Method method, Object[] args) {
        EsErrorCodes.PARAM_TYPE_ERROR.isTrue(null != args && 2 == args.length,
                                             "mapper方法参数必须为 [String templateName, Object param]");
        this.elasticMapperService.template(String.valueOf(args[0]), method, args[1]);
        return true;
    }

    /**
     * Search by mapper
     *
     * @param method method
     * @param args   args
     * @return the object
     * @since 1.7.1
     */
    public Object searchByMapper(Method method, Object[] args) {
        Mapper mapper = this.esMapperContextual.getMapper(method);
        EsErrorCodes.PARAM_TYPE_ERROR.isTrue(null != args && 1 == args.length, "mapper方法参数只能一位");
        return this.elasticMapperService.searchList(mapper.getIndex(), method, args[0], mapper.getType());
    }

    /**
     * Handler
     *
     * @param mapper mapper
     * @param method method
     * @param args   args
     * @return the object
     * @since 1.7.1
     */
    public Object handler(Mapper mapper, Method method, Object[] args) {
        // 父类接口中的 基本 API
        if (BaseElasticMapper.class.getName().equals(mapper.getNameSpace())) {
            return provideBasicApi(mapper, method, args);
        }

        // 规定的几种返回值类型的 固定 mapper 方法
        if (method.getName().startsWith(ElasticStarterConstant.SEARCH)) {
            return this.searchByMapper(method, args);
        }
        if (method.getName().startsWith(ElasticStarterConstant.TEMPLATE)) {
            return this.template(method, args);
        }

        // 兜底方法
        return this.doYouWantToTakeOffToo(method, args);
    }

    /**
     * Provide basic api
     *
     * @param mapper mapper
     * @param method method
     * @param args   args
     * @return the object
     * @since 1.7.1
     */
    @Nullable
    private Object provideBasicApi(Mapper mapper, Method method, Object[] args) {
        // 基本 mapper
        MapperMethod mapperMethod = mapper.findMapperMethod(method.getName());
        String methodName = mapperMethod.getName();

        Object[] dist = new Object[args.length + 1];
        System.arraycopy(args, 0, dist, 1, args.length);
        dist[0] = mapper;

        Class<?>[] paramsType = mapperMethod.getParamsType();
        Class<?>[] distParamsType = new Class<?>[paramsType.length + 1];
        System.arraycopy(paramsType, 0, distParamsType, 1, paramsType.length);
        distParamsType[0] = mapper.getClass();
        return ReflectionUtils.invokeMethod(this.mapperSpringboardService, methodName, distParamsType, dist);
    }

    /**
     * If you want diy
     *
     * @param <T>    parameter
     * @param method method
     * @param args   args
     * @return the object
     * @since 1.7.1
     */
    private <T> T doYouWantToTakeOffToo(Method method, Object[] args) {
        ClientInterface client = this.mapperSpringboardService
            .getClient(ElasticsearchUtils.allMapperPath(method.getDeclaringClass()));

        Parameter[] parameters = method.getParameters();
        // 接口不规范，
        EsErrorCodes.PARAM_TYPE_ERROR.isTrue(parameters.length != 0
                                             && DynamicMapperClient.class.isAssignableFrom(parameters[0].getType()),
                                             ElasticStarterConstant.WARNING);

        //noinspection unchecked
        return (T) ((DynamicMapperClient<?>) args[0]).execute(client, method.getName());
    }
}
