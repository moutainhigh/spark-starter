package info.spark.starter.common.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:42
 * @since 1.0.0
 */
public interface BaseDao<T> extends BaseMapper<T> {

    /**
     * 插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param entity 实体对象
     * @return 更改的条数 int
     * @since 1.0.0
     */
    int insertIgnore(T entity);

    /**
     * 表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param entity 实体对象
     * @return 更改的条数 int
     * @since 1.0.0
     */
    int replace(T entity);

    /**
     * 通用分页查询接口
     *
     * @param <D>   {@link BaseDTO} 子类
     * @param <Q>   {@link BaseQuery} 子类
     * @param page  分页参数
     * @param query 查询参数
     * @return the page
     * @since 1.6.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<?>> IPage<D> page(@Param("page") IPage<D> page,
                                                                                      @Param("query") Q query);

    /**
     * 通用 list 查询接口, sql 走的是 page 分页查询, 但是不会执行 count 查询.
     *
     * @param <D>   {@link BaseDTO} 子类
     * @param <Q>   {@link BaseQuery} 子类
     * @param query 查询参数
     * @return the page
     * @since 1.6.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<?>> List<D> page(@Param("query") Q query);


    /**
     * 流式查询：防止查询返回得数据量太大导致应用OOM
     * 要使用该方法前提是必须在 Mapper.xml 中定义 stream 对应的 statement 语句，否则报错
     *
     * @param <D>   parameter
     * @param <Q>   parameter
     * @param query query
     * @return the cursor
     * @since 1.7.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<?>> Cursor<D> stream(@Param("query") Q query);

    /**
     * Count
     *
     * @param <Q>   parameter
     * @param query query
     * @return the int
     * @since 1.8.0
     */
    <Q extends BaseQuery<?>> int count(Q query);
}
