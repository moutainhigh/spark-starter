package info.spark.starter.dubbo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.base.BaseQuery;
import info.spark.starter.common.base.Bridge;
import info.spark.starter.common.base.ICrudDelegate;
import info.spark.starter.dubbo.exception.DubboCodes;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * <p>Description:  </p>
 *
 * @param <S> db 操作服务
 * @param <D> 数据传输对象
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.31 14:51
 * @since 2.0.0
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public abstract class AbstractDubboProvider<S extends ICrudDelegate<D>,
    D extends BaseDTO<? extends Serializable>> implements ICrudDelegate<D> {

    /** 服务 */
    @Autowired
    private S service;

    /** 桥 */
    private Bridge<D> bridge;

    /** 初始化  @since 2.1.0 */
    @PostConstruct
    public void init() {
        bridge = new Bridge<D>(service) {
        };
    }

    /**
     * 创建
     *
     * @param <I> parameter
     * @param dto dto
     * @return the serializable
     * @since 2.0.0
     */
    @NotNull
    @Override
    public <I extends Serializable> I create(@NotNull D dto) {
        return this.bridge.create(dto, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建批处理
     *
     * @param ds ds
     * @since 2.1.0
     */
    @Override
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
    @Override
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
    @Override
    public <I extends Serializable> I createIgnore(@NotNull D dto) {
        return this.bridge.createIgnore(dto, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建忽略批
     *
     * @param ds ds
     * @since 2.1.0
     */
    @Override
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
    @Override
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
    @Override
    public <I extends Serializable> I createReplace(@NotNull D dto) {
        return this.bridge.createReplace(dto, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建取代批
     *
     * @param ds ds
     * @since 2.1.0
     */
    @Override
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
    @Override
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
    @Override
    public <I extends Serializable> I createOrUpdate(@NotNull D dto) {
        return this.bridge.createOrUpdate(dto, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 创建或更新批
     *
     * @param ds ds
     * @since 2.1.0
     */
    @Override
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
    @Override
    public void createOrUpdateBatch(Collection<D> ds, int batchSize) {
        this.bridge.createOrUpdateBatch(ds, batchSize, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 更新
     *
     * @param dto dto
     * @since 2.1.0
     */
    @Override
    public void update(@NotNull D dto) {
        this.bridge.update(dto, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 批处理更新
     *
     * @param ds ds
     * @since 2.1.0
     */
    @Override
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
    @Override
    public void updateBatch(Collection<D> ds, int batchSize) {
        this.bridge.updateBatch(ds, batchSize, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 删除
     *
     * @param <I> parameter
     * @param id  id
     * @since 2.1.0
     */
    @Override
    public <I extends Serializable> void delete(@NotNull I id) {
        this.bridge.delete(id, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 删除
     *
     * @param <I> parameter
     * @param ids id
     * @since 2.1.0
     */
    @Override
    public <I extends Serializable> void delete(Collection<I> ids) {
        this.bridge.delete(ids, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 删除
     *
     * @param columnMap 列映射
     * @since 2.1.0
     */
    @Override
    public void delete(@NotNull Map<String, Object> columnMap) {
        this.bridge.delete(columnMap, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 计数
     *
     * @return int int
     * @since 2.1.0
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public <I extends Serializable> List<D> find(Collection<I> ids) {
        return this.bridge.find(ids, DubboCodes.OPERATION_ERROR);
    }

    /**
     * 找到所有
     *
     * @return {@link List}<{@link D}>
     * @since 2.1.0
     */
    @Override
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
    @Override
    public <D1 extends BaseDTO<? extends Serializable>, E extends BaseQuery<? extends Serializable>> IPage<D1> page(@NotNull E query) {
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
    @Override
    public <D1 extends BaseDTO<? extends Serializable>, E extends BaseQuery<? extends Serializable>> List<D1> list(@NotNull E query) {
        return this.bridge.list(query, DubboCodes.OPERATION_ERROR);
    }
}
