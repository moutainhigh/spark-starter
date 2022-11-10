package info.spark.agent.adapter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import info.spark.agent.adapter.client.AgentClient;
import info.spark.agent.adapter.enums.AgentClientErrorCodes;
import info.spark.agent.adapter.exception.AgentClientException;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.base.BaseQuery;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>Description:  </p>
 * todo-dong4j : (2021.11.1 21:11) [尝试实现 ICrudDelegate]
 *
 * @param <A> parameter
 * @param <Q> parameter
 * @param <D> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.30 17:26
 * @since 2.0.0
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public abstract class AbstractAgentManager<A extends AbstractAgentClient<? extends AgentClient, Q, D>,
    Q extends BaseQuery<? extends Serializable>,
    D extends BaseDTO<? extends Serializable>> {

    /** Sdk client */
    @Autowired
    protected A sdk;

    /**
     * 创建
     * 保存
     *
     * @param <I> parameter
     * @param dto dto
     * @return the serializable
     * @throws Exception exception
     * @since 2.0.0
     */
    @NotNull
    public <I extends Serializable> I create(@NotNull D dto) throws Exception {
        Assertions.notNull(dto, () -> new AgentClientException("新增实体不能为 null"));
        Assertions.isNull(dto.getId(), () -> new AgentClientException("新增数据时不能传入 id"));
        return this.sdk.create(dto);
    }

    /**
     * 创建批处理
     *
     * @param dto dto
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void createBatch(Collection<D> dto) throws AgentClientException {
        Assertions.notEmpty(dto, () -> new AgentClientException("新增实体不能为 null"));
        this.sdk.createBatch(dto);
    }

    /**
     * 创建批处理
     *
     * @param dto       dto
     * @param batchSize 批量大小
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void createBatch(Collection<D> dto, int batchSize) throws AgentClientException {
        this.sdk.createBatch(dto, batchSize);
    }

    /**
     * 创建忽略
     *
     * @param <I> parameter
     * @param d   d
     * @return {@link I}
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public <I extends Serializable> I createIgnore(@NotNull D d) throws AgentClientException {
        Assertions.notNull(d, () -> new AgentClientException("新增实体不能为 null"));
        Assertions.isNull(d.getId(), () -> new AgentClientException("新增数据时不能传入 id"));
        return this.sdk.createIgnore(d);
    }

    /**
     * 创建忽略批
     *
     * @param ds ds
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void createIgnoreBatch(Collection<D> ds) throws AgentClientException {
        Assertions.notEmpty(ds, () -> new AgentClientException("新增实体不能为 null"));
        this.sdk.createIgnoreBatch(ds);
    }

    /**
     * 创建忽略批
     *
     * @param ds        ds
     * @param batchSize 批量大小
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void createIgnoreBatch(Collection<D> ds, int batchSize) throws AgentClientException {
        this.sdk.createIgnoreBatch(ds, batchSize);
    }

    /**
     * 创建替代
     *
     * @param <I> parameter
     * @param d   d
     * @return {@link I}
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public <I extends Serializable> I createReplace(@NotNull D d) throws AgentClientException {
        Assertions.notNull(d, () -> new AgentClientException("新增实体不能为 null"));
        Assertions.isNull(d.getId(), () -> new AgentClientException("新增数据时不能传入 id"));
        return this.sdk.createReplace(d);
    }

    /**
     * 创建取代批
     *
     * @param ds ds
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void createReplaceBatch(Collection<D> ds) throws AgentClientException {
        Assertions.notEmpty(ds, () -> new AgentClientException("新增实体不能为 null"));
        this.sdk.createReplaceBatch(ds);
    }

    /**
     * 创建取代批
     *
     * @param ds        ds
     * @param batchSize 批量大小
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void createReplaceBatch(Collection<D> ds, int batchSize) throws AgentClientException {
        this.sdk.createReplaceBatch(ds, batchSize);
    }

    /**
     * 创建或更新
     *
     * @param <I> parameter
     * @param d   d
     * @return {@link I}
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public <I extends Serializable> I createOrUpdate(@NotNull D d) throws AgentClientException {
        Assertions.notNull(d, () -> new AgentClientException("新增实体不能为 null"));
        return this.sdk.createOrUpdate(d);
    }

    /**
     * 创建或更新批
     *
     * @param ds ds
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void createOrUpdateBatch(Collection<D> ds) throws AgentClientException {
        Assertions.notEmpty(ds, () -> new AgentClientException("新增实体不能为 null"));
        this.sdk.createOrUpdateBatch(ds);
    }

    /**
     * 创建或更新批
     *
     * @param ds        ds
     * @param batchSize 批量大小
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void createOrUpdateBatch(Collection<D> ds, int batchSize) throws AgentClientException {
        this.sdk.createOrUpdateBatch(ds, batchSize);
    }

    /**
     * 更新
     *
     * @param dto dto
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void update(@NotNull D dto) throws AgentClientException {
        Assertions.notNull(dto, () -> new AgentClientException("新增实体不能为 null"));
        this.sdk.update(dto);
    }

    /**
     * 批处理更新
     *
     * @param ds ds
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void updateBatch(Collection<D> ds) throws AgentClientException {
        Assertions.notEmpty(ds, () -> new AgentClientException("批量更新实体不能为 null"));
        this.sdk.updateBatch(ds);
    }

    /**
     * 批处理更新
     *
     * @param ds        ds
     * @param batchSize 批量大小
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void updateBatch(Collection<D> ds, int batchSize) throws AgentClientException {
        this.sdk.updateBatch(ds, batchSize);
    }

    /**
     * 删除
     *
     * @param id id
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void delete(@NotNull Serializable id) throws AgentClientException {
        Assertions.notNull(id, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        this.sdk.delete(id);
    }

    /**
     * 删除
     *
     * @param <I> parameter
     * @param ids id
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public <I extends Serializable> void delete(Collection<I> ids) throws AgentClientException {
        Assertions.notEmpty(ids, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        this.sdk.delete(ids);
    }

    /**
     * 删除
     *
     * @param columnMap 列映射
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void delete(@NotNull Map<String, Object> columnMap) throws AgentClientException {
        Assertions.notEmpty(columnMap, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        this.sdk.delete(columnMap);
    }

    /**
     * 计数
     *
     * @return int int
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public int counts() throws AgentClientException {
        return this.sdk.counts();
    }

    /**
     * 计数
     *
     * @param query 查询
     * @return int int
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public int counts(@NotNull Q query) throws AgentClientException {
        Assertions.notNull(query, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        return this.sdk.counts(query);
    }

    /**
     * 找到
     *
     * @param <I> parameter
     * @param id  id
     * @return {@link D}
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public <I extends Serializable> D find(@NotNull I id) throws AgentClientException {
        Assertions.notNull(id, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        return this.sdk.find(id);
    }

    /**
     * 找到
     *
     * @param query 查询
     * @return {@link D}
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public D find(@NotNull Q query) throws AgentClientException {
        Assertions.notNull(query, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        return this.sdk.find(query);
    }

    /**
     * 找到
     *
     * @param <I> parameter
     * @param ids id
     * @return {@link List}<{@link D}>
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public <I extends Serializable> List<D> find(Collection<I> ids) throws AgentClientException {
        Assertions.notEmpty(ids, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        return this.sdk.find(ids);
    }

    /**
     * 找到所有
     *
     * @return {@link List}<{@link D}>
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public List<D> find() throws AgentClientException {
        return this.sdk.find();
    }

    /**
     * 页面
     *
     * @param query 查询
     * @return the page
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public IPage<D> page(@NotNull Q query)
        throws AgentClientException {
        Assertions.notNull(query, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        return this.sdk.page(query);
    }

    /**
     * 列表
     *
     * @param query 查询
     * @return the list
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public List<D> list(@NotNull Q query)
        throws AgentClientException {
        Assertions.notNull(query, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        return this.sdk.list(query);
    }

}
