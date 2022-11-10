package info.spark.starter.mybatis.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.base.BaseQuery;

import org.apache.ibatis.cursor.Cursor;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @param <PO> parameter
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:21
 * @since 1.0.0
 */
public interface BaseService<PO> extends IService<PO> {
    /**
     * 插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param entity entity
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    boolean saveIgnore(PO entity);

    /**
     * 表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param entity entity
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    boolean saveReplace(PO entity);

    /**
     * 插入 (批量) ,插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param entityList 实体对象集合
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean saveIgnoreBatch(Collection<PO> entityList) {
        Assertions.notEmpty(entityList);
        return this.saveIgnoreBatch(entityList, 1000);
    }

    /**
     * 插入 (批量) ,插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param entityList 实体对象集合
     * @param batchSize  批次大小
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    boolean saveIgnoreBatch(Collection<PO> entityList, int batchSize);

    /**
     * 插入 (批量) ,表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param entityList 实体对象集合
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean saveReplaceBatch(Collection<PO> entityList) {
        Assertions.notEmpty(entityList);
        return this.saveReplaceBatch(entityList, 1000);
    }

    /**
     * 插入 (批量) ,表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param entityList 实体对象集合
     * @param batchSize  批次大小
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    boolean saveReplaceBatch(Collection<PO> entityList, int batchSize);

    /**
     * 分页查询接口
     *
     * @param <D>   {@link BaseDTO} 子类
     * @param <Q>   {@link BaseQuery} 子类
     * @param page  分页参数
     * @param query 业务查询参数
     * @return the {@link IPage} 的子类 {@link Page}
     * @since 1.6.0
     */
    @Deprecated
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> IPage<D> page(IPage<D> page, @NotNull Q query);

    /**
     * 分页查询接口
     *
     * @param <D>   {@link BaseDTO} 子类
     * @param <Q>   {@link BaseQuery} 子类
     * @param query 业务查询参数
     * @return the {@link IPage} 的子类 {@link Page}
     * @since 1.6.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> IPage<D> page(@NotNull Q query);

    /**
     * List
     *
     * @param <D>   {@link BaseDTO} 子类
     * @param <Q>   {@link BaseQuery} 子类
     * @param query 业务查询参数
     * @return the list
     * @since 1.6.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> List<D> list(@NotNull Q query);

    /**
     * Count
     *
     * @param <Q>   parameter
     * @param query query
     * @return the int
     * @since 1.8.0
     */
    <Q extends BaseQuery<? extends Serializable>> int count(@NotNull Q query);

    /**
     * 流式查询
     *
     * @param <D>   parameter
     * @param <Q>   parameter
     * @param query query
     * @return the cursor
     * @since 1.7.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> Cursor<D> stream(@NotNull Q query);

}
