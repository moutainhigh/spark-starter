package info.spark.starter.es.support;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.context.SpringContext;
import info.spark.starter.util.DateUtils;
import info.spark.starter.es.annotation.EsMapping;
import info.spark.starter.es.config.ElasticsearchProperties;
import info.spark.starter.es.entity.constant.ElasticStarterConstant;
import info.spark.starter.es.entity.document.BaseElasticEntity;
import info.spark.starter.es.enums.ESMappingType;
import info.spark.starter.es.exception.EsErrorCodes;
import info.spark.starter.es.mapper.structure.Mapper;
import com.frameworkset.orm.annotation.ESIndex;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import cn.hutool.core.util.ClassUtil;
import lombok.experimental.UtilityClass;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 18:47
 * @since 1.7.1
 */
@UtilityClass
public class ElasticsearchUtils {

    /** properties */
    private static ElasticsearchProperties properties;

    /** camelToUnderscoreConverter */
    private static final Converter<String, String> CAMEL_TO_UNDERSCORE_CONVERTER =
        CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

    /**
     * 获取 bean 的 index，有注解就注解，没有就转化为 xx_xx_xx (下划线)
     *
     * @param type type
     * @return the index from bean
     * @since 1.7.1
     */
    public static String getIndexFromBean(Class<?> type) {
        EsErrorCodes.DATA_NOT_FOUNT.notNull(type);
        return Optional.ofNullable(type.getAnnotation(ESIndex.class))
            .map(ESIndex::name)
            .orElse(CAMEL_TO_UNDERSCORE_CONVERTER.convert(type.getSimpleName()));
    }

    /**
     * 类名获取 namespace
     *
     * @param clazz clazz
     * @return the class name space
     * @since 1.7.1
     */
    public static String getClassNameSpace(Class<?> clazz) {
        return clazz.getName();
    }

    /**
     * mapper 资源解析
     *
     * @param clazz clazz
     * @return the string
     * @since 1.7.1
     */
    public static String allMapperPath(Class<?> clazz) {
        if (null == properties) {
            properties = SpringContext.getInstance(ElasticsearchProperties.class);
        }
        // todo: 判读mapperPath准确性
        return properties.getMapperPath() + clazz.getSimpleName() + ".xml";
    }

    /**
     * Gets interface parameter type *
     *
     * @param type          type
     * @param findInterface find interface
     * @param index         index
     * @return the interface parameter type
     * @since 1.7.1
     */
    public static Class<? extends BaseElasticEntity<?>> getInterfaceParameterType(Class<?> type, Class<?> findInterface, int index) {
        Type[] genericInterfaces = type.getGenericInterfaces();
        if (0 != genericInterfaces.length) {
            int paramsIndex = index;
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    Type[] params = ((ParameterizedType) genericInterface).getActualTypeArguments();
                    paramsIndex = (index > params.length || index < 0) ? 0 : paramsIndex;
                    //noinspection unchecked
                    return (Class<? extends BaseElasticEntity<?>>) params[paramsIndex];
                }
                if (findInterface.isAssignableFrom((Class<?>) genericInterface)) {
                    return getInterfaceParameterType(((Class<?>) genericInterface), findInterface, index);
                }
            }
        }
        throw EsErrorCodes.PARAM_TYPE_ERROR.newException();
    }

    /**
     * 将Class解析成映射JSONString
     *
     * @param mapper mapper
     * @param clazz  clazz
     * @return the string
     * @since 1.8.0
     */
    public String parseMapping(Mapper mapper, Class<? extends BaseElasticEntity<?>> clazz) {
        // 构建索引映射
        ReactorHashMap<Object, Object> put = ReactorHashMap.builder()
            .put("mappings", () -> ReactorHashMap.builder()
                     // 7.x 之后 type 默认是_doc，不需要写，写了会报错
                     .put("properties", () ->
                         getObjectObjectReactorHashMap(mapper, clazz.getDeclaredFields()))
                );
        return JsonUtils.toJson(put);
    }

    /**
     * 在解析type之后，进行后续的操作后返回
     *
     * @param mapper         mapper
     * @param field          field
     * @param jsonColumnName json column name
     * @return the reactor hash map
     * @since 1.8.0
     */
    @SuppressWarnings("PMD.UndefineMagicConstantRule")
    private ReactorHashMap<Object, Object> afterType(Mapper mapper, Field field, String jsonColumnName) {
        ReactorHashMap<Object, Object> mapping = toEsJson(mapper, field);
        if ("date".equals(mapping.get("type"))) {
            JsonFormat jsonFormat = field.getAnnotation(JsonFormat.class);
            mapping.put("format", null == jsonFormat ? DateUtils.PATTERN_DATETIME : jsonFormat.pattern());
        }
        // 向mapper中进行注册 key、value
        mapper.getNameType().put(jsonColumnName, (String) mapping.get("type"));
        return mapping;
    }

    /**
     * Gets json column name *
     *
     * @param field field
     * @return the json column name
     * @since 1.8.0
     */
    public String getJsonColumnName(Field field) {
        JsonProperty annotation = field.getAnnotation(JsonProperty.class);
        return null != annotation ? annotation.value() : field.getName();
    }

    /**
     * 单个 field 解析
     *
     * @param mapper mapper
     * @param field  field
     * @return the reactor hash map
     * @since 1.8.0
     */
    @SuppressWarnings({"LineLength", "NestedIfDepth"})
    private ReactorHashMap<Object, Object> toEsJson(Mapper mapper, Field field) {
        //基本数据类型
        if (ClassUtil.isSimpleTypeOrArray(field.getType())) {
            //对字符串做大小限制、分词设置
            if (new ArrayList<>(Collections.singletonList(String.class)).contains(field.getType())) {
                // string 类型，默认为 不分词
                ReactorHashMap<Object, Object> put = ReactorHashMap.builder()
                    .put("type", ESMappingType.keyword::getValue);

                if (field.isAnnotationPresent(EsMapping.class)) {
                    EsMapping esMapping = field.getAnnotation(EsMapping.class);
                    put.put("type", () -> esMapping.value().getValue());

                    if (ESMappingType.text == esMapping.value()) {
                        //设置聚合分组
                        if (esMapping.fieldData()) {
                            put.put("fielddata", () -> true);
                        }
                        //设置加权
                        put.put("boost", esMapping::boost);

                        //设置是否进行分词
                        if (!ElasticStarterConstant.ANALYZED.equals(esMapping.index())) {
                            put.put("analyzed", esMapping::analyzer);
                        }
                        //分词器
                        put.put("analyzer", esMapping::analyzer);
                        // 如果是 string
                        put.put("fields", () -> ReactorHashMap.builder()
                            .put("keyword", () -> ReactorHashMap.builder()
                                .put("type", () -> "keyword")
                                .put("ignore_above", () -> 256)));
                    }
                }
                return put;
            }
            //设置默认类型
            return ReactorHashMap.builder().put("type", () -> {
                if (field.isAnnotationPresent(EsMapping.class)) {
                    EsMapping esMapping = field.getAnnotation(EsMapping.class);
                    return esMapping.value().getValue();
                }
                if (new ArrayList<>(Arrays.asList(byte.class, Byte.class, short.class, Short.class, int.class, Integer.class,
                                                  long.class, Long.class)).contains(field.getType())) {
                    return "long";
                } else if (new ArrayList<>(Arrays.asList(double.class, Double.class, float.class, Float.class)).contains(field.getType())) {
                    return "double";
                } else if (new ArrayList<>(Arrays.asList(Date.class, java.sql.Date.class, LocalDate.class, LocalDateTime.class,
                                                         LocalTime.class)).contains(field.getType())) {
                    return "date";
                } else if (new ArrayList<>(Arrays.asList(boolean.class, Boolean.class)).contains(field.getType())) {
                    return "boolean";
                }
                return "text";
            });

        } else {
            //设置对象类型
            ReactorHashMap<Object, Object> propText = ReactorHashMap.builder()
                .put("properties", () ->
                         getObjectObjectReactorHashMap(mapper, field.getType().getDeclaredFields())
                    );
            if (field.isAnnotationPresent(EsMapping.class)) {
                EsMapping esMapping = field.getAnnotation(EsMapping.class);
                propText.put("type", esMapping.value().getValue());
            }
            return propText;
        }
    }

    /**
     * Gets object object reactor hash map *
     *
     * @param mapper mapper
     * @param fields fields
     * @return the object object reactor hash map
     * @since 2.0.0
     */
    @NotNull
    private ReactorHashMap<Object, Object> getObjectObjectReactorHashMap(Mapper mapper, Field[] fields) {
        ReactorHashMap<Object, Object> builder = ReactorHashMap.builder();
        for (Field field : fields) {
            if (field.isAnnotationPresent(JsonIgnore.class)) {
                continue;
            }
            String jsonColumnName = getJsonColumnName(field);
            builder.put(jsonColumnName, () -> afterType(mapper, field, jsonColumnName));
        }
        return builder;
    }

}
