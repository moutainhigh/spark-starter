package info.spark.starter.mybatis.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.base.BaseDao;
import info.spark.starter.common.base.BaseQuery;
import info.spark.starter.mybatis.injector.MybatisSqlMethod;
import info.spark.starter.mybatis.service.BaseService;
import info.spark.starter.support.Condition;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>Description: BaseService 实现类 </p>
 *
 * @param <DAO> parameter
 * @param <PO>  parameter
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.10 17:48
 * @since 1.0.0
 */
public class BaseServiceImpl<DAO extends BaseDao<PO>, PO> extends ServiceImpl<DAO, PO> implements BaseService<PO> {

    /**
     * Save ignore boolean
     *
     * @param entity entity
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean saveIgnore(PO entity) {
        Assertions.notNull(entity);
        return SqlHelper.retBool(this.baseMapper.insertIgnore(entity));
    }

    /**
     * Save replace boolean
     *
     * @param entity entity
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean saveReplace(PO entity) {
        Assertions.notNull(entity);
        return SqlHelper.retBool(this.baseMapper.replace(entity));
    }

    /**
     * Save ignore batch boolean
     *
     * @param entityList entity list
     * @param batchSize  batch size
     * @return the boolean
     * @since 1.0.0
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveIgnoreBatch(Collection<PO> entityList, int batchSize) {
        Assertions.notEmpty(entityList);
        return this.saveBatch(entityList, batchSize, MybatisSqlMethod.INSERT_IGNORE_ONE);
    }

    /**
     * Save replace batch boolean
     *
     * @param entityList entity list
     * @param batchSize  batch size
     * @return the boolean
     * @since 1.0.0
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveReplaceBatch(Collection<PO> entityList, int batchSize) {
        Assertions.notEmpty(entityList);
        return this.saveBatch(entityList, batchSize, MybatisSqlMethod.REPLACE_ONE);
    }

    /**
     * Save batch boolean
     *
     * @param entityList entity list
     * @param batchSize  batch size
     * @param sqlMethod  sql method
     * @return the boolean
     * @since 1.0.0
     */
    private boolean saveBatch(@NotNull Collection<PO> entityList, int batchSize, MybatisSqlMethod sqlMethod) {
        Assertions.notEmpty(entityList);
        String sqlStatement = this.mybatisSqlStatement(sqlMethod);
        try (SqlSession batchSqlSession = SqlHelper.sqlSessionBatch(this.entityClass)) {
            int i = 0;
            for (PO anEntityList : entityList) {
                batchSqlSession.insert(sqlStatement, anEntityList);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }

    /**
     * Mybatis sql statement string.
     *
     * @param sqlMethod ignore
     * @return sql string
     * @since 1.0.0
     */
    protected String mybatisSqlStatement(@NotNull MybatisSqlMethod sqlMethod) {
        return SqlHelper.table(this.currentModelClass()).getSqlStatement(sqlMethod.getMethod());
    }

    /**
     * 分页查询接口
     *
     * @param <DTO> parameter
     * @param <Q>   {@link BaseQuery} 子类
     * @param page  分页参数
     * @param query 业务查询参数
     * @return the {@link IPage} 的子类 {@link Page}
     * @since 1.6.0
     * @deprecated 使用 {@link BaseServiceImpl#page(BaseQuery)}
     */
    @Override
    @Deprecated
    public <DTO extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> IPage<DTO>
    page(IPage<DTO> page, @NotNull Q query) {
        Assertions.notNull(query);
        Condition.getPage(query);
        return this.baseMapper.page(page, query);
    }

    /**
     * 分页查询接口
     *
     * @param <DTO> parameter
     * @param <Q>   {@link BaseQuery} 子类
     * @param query 业务查询参数
     * @return the {@link IPage} 的子类 {@link Page}
     * @since 1.6.0
     */
    @Override
    public <DTO extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> IPage<DTO> page(@NotNull Q query) {
        Assertions.notNull(query);
        Condition.getPage(query);
        return this.baseMapper.page(Condition.getPage(query), query);
    }

    /**
     * 根据条件查询所有记录
     *
     * @param <DTO> parameter
     * @param <Q>   {@link BaseQuery} 子类
     * @param query 业务查询参数
     * @return the list
     * @since 1.6.0
     */
    @Override
    public <DTO extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> List<DTO> list(@NotNull Q query) {
        Assertions.notNull(query);
        return this.baseMapper.page(query);
    }

    /**
     * Count
     *
     * @param query query
     * @return the int
     * @since 1.8.0
     */
    @Override
    public <Q extends BaseQuery<? extends Serializable>> int count(@NotNull Q query) {
        Assertions.notNull(query);
        return this.baseMapper.count(query);
    }

    /**
     * 流式查询
     *
     * @param <DTO> parameter
     * @param <Q>   parameter
     * @param query query
     * @return the cursor
     * @since 1.7.0
     */
    @Override
    public <DTO extends BaseDTO<? extends Serializable>,
        Q extends BaseQuery<? extends Serializable>> Cursor<DTO> stream(@NotNull Q query) {
        Assertions.notNull(query);
        return this.baseMapper.stream(query);
    }

}
