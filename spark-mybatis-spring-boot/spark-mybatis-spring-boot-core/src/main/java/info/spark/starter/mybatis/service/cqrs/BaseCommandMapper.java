package info.spark.starter.mybatis.service.cqrs;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.28 02:05
 * @since 1.7.3
 */
public interface BaseCommandMapper<T> extends Mapper<T> {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     * @return the int
     * @since 1.7.3
     */
    int insert(T entity);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     * @return the int
     * @since 1.7.3
     */
    int deleteById(Serializable id);

    /**
     * 根据 columnMap 条件，删除记录
     *
     * @param columnMap 表字段 map 对象
     * @return the int
     * @since 1.7.3
     */
    int deleteByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);

    /**
     * 根据 entity 条件，删除记录
     *
     * @param wrapper 实体对象封装操作类（可以为 null）
     * @return the int
     * @since 1.7.3
     */
    int delete(@Param(Constants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     * @return the int
     * @since 1.7.3
     */
    int deleteBatchIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> idList);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     * @return the int
     * @since 1.7.3
     */
    int updateById(@Param(Constants.ENTITY) T entity);

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity        实体对象 (set 条件值,可以为 null)
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     * @return the int
     * @since 1.7.3
     */
    int update(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) Wrapper<T> updateWrapper);

}
