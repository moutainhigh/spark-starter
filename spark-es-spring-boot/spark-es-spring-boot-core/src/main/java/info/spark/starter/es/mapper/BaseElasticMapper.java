package info.spark.starter.es.mapper;

import info.spark.starter.es.entity.document.BaseElasticEntity;
import info.spark.starter.support.Page;

import org.frameworkset.elasticsearch.client.ClientInterface;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;

/**
 * <p>Description: </p>
 *
 * @param <T> 实体
 * @param <I> id
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.22 17:01
 * @since 1.7.1
 */
public interface BaseElasticMapper<I extends Serializable, T extends BaseElasticEntity<I>> {

    /**
     * 新增 通过实体
     *
     * @param bean bean
     * @return the boolean
     * @since 1.7.1
     */
    boolean insert(T bean);

    /**
     * 新增 通过实体
     *
     * @param beans beans
     * @return the boolean
     * @since 1.7.1
     */
    boolean insertMany(List<T> beans);

    /**
     * 删除
     *
     * @param ids ids
     * @return the boolean
     * @since 1.7.1
     */
    boolean delete(I... ids);

    /**
     * 主键查询
     *
     * @param id id
     * @return the one
     * @since 1.7.1
     */
    T getOne(I id);

    /**
     * 通过字段 匹配 查询
     *
     * @param field  field
     * @param value  value
     * @param isLike 为 true 时，将使用 match 语法，分词、全量匹配等都能查询到
     * @return the by field
     * @since 1.7.1
     */
    T getByField(String field, Object value, boolean isLike);

    /**
     * 通过字段 匹配 查询 list
     *
     * @param field  field
     * @param value  value
     * @param page   page 为空 查询 前 10
     * @param isLike 为 true 时，将使用 match 语法，分词、全量匹配等都能查询到
     * @return the by field
     * @since 1.7.1
     */
    Page<T> getListByField(String field, Object value, @Nullable Page<?> page, boolean isLike);

    /**
     * 查询所有
     *
     * @return the list
     * @since 1.7.1
     */
    List<T> findAll();

    /**
     * <p>实体对象字段更新 by ID</p>
     * <p color='red'>如果需要只更新几个字段，需要在document实体上添加注解 <b>@JsonInclude(JsonInclude.Include.NON_NULL</b> </p>
     *
     * @param entity entity
     * @return the boolean
     * @since 1.8.0
     */
    boolean update(T entity);

    /**
     * 实体对象 List 字段更新 by ID
     * <p color='red'>如果需要只更新几个字段，需要在document实体上添加注解 <b>@JsonInclude(JsonInclude.Include.NON_NULL</b> </p>
     *
     * @param entity entity
     * @return the boolean
     * @since 1.8.0
     */
    boolean updateMany(List<T> entity);

    /**
     * Gets base client *
     *
     * @return the base client
     * @since 1.8.0
     */
    ClientInterface getBaseClient();
}
