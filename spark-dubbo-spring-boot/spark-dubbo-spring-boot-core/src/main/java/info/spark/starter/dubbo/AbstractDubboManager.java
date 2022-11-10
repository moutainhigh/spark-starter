package info.spark.starter.dubbo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.base.BaseQuery;
import info.spark.starter.common.base.Bridge;
import info.spark.starter.common.base.IDubboClient;
import info.spark.starter.dubbo.exception.DubboCodes;
import info.spark.starter.dubbo.exception.DubboException;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * <p>Description: dubbo 服务消费端集成，前置参数校验与异常包装，所有异常全部包装为 {@link DubboException} </p>
 *
 * @param <R> DubboReference
 * @param <D> BaseDTO
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.28 16:47
 * @since 2.0.0
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public abstract class AbstractDubboManager<R extends IDubboClient<D>,
    D extends BaseDTO<? extends Serializable>> {

    /** service */
    @Autowired
    protected R reference;

    /** 桥 */
    private Bridge<D> bridge;

    /**
     * Init
     *
     * @since 2.1.0
     */
    @PostConstruct
    public void init() {
        bridge = new Bridge<D>(reference) {
        };
    }

    /**
     * 创建
     * 根据是否存在 id 进行新增或更新
     *
     * @param <I> parameter
     * @param dto 数据实体
     * @return the serializable
     * @since 2.0.0
     */
    @NotNull
    public <I extends Serializable> I create(@NotNull D dto) {
        dto.setId(this.bridge.create(dto, DubboCodes.OPERATION_ERROR));
        //noinspection unchecked
        return (I) dto.getId();
    }

    /**
     * 创建批处理
     *
     * @param ds ds
     * @since 2.1.0
     */
    public void createBatch(Collection<D> ds) {
        this.bridge.createBatch(ds, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建批处理
     *
     * @param ds        ds
     * @param batchSize 批量大小
     * @since 2.1.0
     */
    public void createBatch(Collection<D> ds, int batchSize) {
        this.bridge.createBatch(ds, batchSize, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建忽略
     *
     * @param <I> parameter
     * @param dto dto
     * @return {@link I}
     * @since 2.1.0
     */
    public <I extends Serializable> I createIgnore(@NotNull D dto) {
        dto.setId(this.bridge.createIgnore(dto, DubboCodes.OPERATION_ERROR));
        //noinspection unchecked
        return (I) dto.getId();
    }

    /**
     * 创建忽略批
     *
     * @param ds ds
     * @since 2.1.0
     */
    public void createIgnoreBatch(Collection<D> ds) {
        this.bridge.createIgnoreBatch(ds, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建忽略批
     *
     * @param ds        ds
     * @param batchSize 批量大小
     * @since 2.1.0
     */
    public void createIgnoreBatch(Collection<D> ds, int batchSize) {
        this.bridge.createIgnoreBatch(ds, batchSize, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建替代
     *
     * @param <I> parameter
     * @param dto dto
     * @return {@link I}
     * @since 2.1.0
     */
    public <I extends Serializable> I createReplace(@NotNull D dto) {
        dto.setId(this.bridge.createReplace(dto, DubboCodes.OPERATION_ERROR));
        //noinspection unchecked
        return (I) dto.getId();
    }

    /**
     * 创建取代批
     *
     * @param ds ds
     * @since 2.1.0
     */
    public void createReplaceBatch(Collection<D> ds) {
        this.bridge.createReplaceBatch(ds, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建取代批
     *
     * @param ds        ds
     * @param batchSize 批量大小
     * @since 2.1.0
     */
    public void createReplaceBatch(Collection<D> ds, int batchSize) {
        this.bridge.createReplaceBatch(ds, batchSize, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建或更新
     *
     * @param <I> parameter
     * @param dto dto
     * @return {@link I}
     * @since 2.1.0
     */
    public <I extends Serializable> I createOrUpdate(@NotNull D dto) {
        dto.setId(this.bridge.createOrUpdate(dto, DubboCodes.OPERATION_ERROR));
        //noinspection unchecked
        return (I) dto.getId();
    }

    /**
     * 创建或更新批
     *
     * @param ds ds
     * @since 2.1.0
     */
    public void createOrUpdateBatch(Collection<D> ds) {
        this.bridge.createOrUpdateBatch(ds, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建或更新批
     *
     * @param ds        ds
     * @param batchSize 批量大小
     * @since 2.1.0
     */
    public void createOrUpdateBatch(Collection<D> ds, int batchSize) {
        this.bridge.createOrUpdateBatch(ds, batchSize, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 更新
     *
     * @param dto dto
     * @since 2.1.0
     */
    public void update(@NotNull D dto) {
        this.bridge.update(dto, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 批处理更新
     *
     * @param ds ds
     * @since 2.1.0
     */
    public void updateBatch(Collection<D> ds) {
        this.bridge.updateBatch(ds, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 批处理更新
     *
     * @param ds        ds
     * @param batchSize 批量大小
     * @since 2.1.0
     */
    public void updateBatch(Collection<D> ds, int batchSize) {
        this.bridge.updateBatch(ds, batchSize, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 删除
     *
     * @param id id
     * @since 2.1.0
     */
    public void delete(@NotNull Serializable id) {
        this.bridge.delete(id, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 删除
     *
     * @param ids id
     * @since 2.1.0
     */
    public void delete(Collection<? extends Serializable> ids) {
        this.bridge.delete(ids, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 删除
     *
     * @param columnMap 列映射
     * @since 2.1.0
     */
    public void delete(@NotNull Map<String, Object> columnMap) {
        this.bridge.delete(columnMap, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 计数
     *
     * @return int int
     * @since 2.1.0
     */
    public int counts() {
        return this.bridge.counts(DubboCodes.OPERATION_ERROR);
    }

    /**
     * 计数
     *
     * @param <E>   parameter
     * @param query 查询
     * @return int int
     * @since 2.1.0
     */
    public <E extends BaseQuery<? extends Serializable>> int counts(@NotNull E query) {
        return this.bridge.counts(query, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 找到
     *
     * @param <I> parameter
     * @param id  id
     * @return {@link D}
     * @since 2.1.0
     */
    public <I extends Serializable> D find(@NotNull I id) {
        return this.bridge.find(id, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 找到
     *
     * @param <E>   parameter
     * @param query 查询
     * @return {@link D}
     * @since 2.1.0
     */
    public <E extends BaseQuery<? extends Serializable>> D find(@NotNull E query) {
        return this.bridge.find(query, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 找到
     *
     * @param <I> parameter
     * @param ids id
     * @return {@link List}<{@link D}>
     * @since 2.1.0
     */
    public <I extends Serializable> List<D> find(Collection<I> ids) {
        return this.bridge.find(ids, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 找到所有
     *
     * @return {@link List}<{@link D}>
     * @since 2.1.0
     */
    public List<D> find() {
        return this.bridge.find(DubboCodes.OPERATION_ERROR);
    }

    /**
     * 页面
     *
     * @param <D1>  parameter
     * @param <E>   parameter
     * @param query 查询
     * @return {@link IPage}<{@link D1}>
     * @since 2.1.0
     */
    public <D1 extends BaseDTO<? extends Serializable>,
        E extends BaseQuery<? extends Serializable>> IPage<D1> page(@NotNull E query) {
        return this.bridge.page(query, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 列表
     *
     * @param <D1>  parameter
     * @param <E>   parameter
     * @param query 查询
     * @return {@link List}<{@link D1}>
     * @since 2.1.0
     */
    public <D1 extends BaseDTO<? extends Serializable>,
        E extends BaseQuery<? extends Serializable>> List<D1> list(@NotNull E query) {
        return this.bridge.list(query, DubboCodes.OPERATION_ERROR);
    }

}
