package info.spark.agent;

import com.baomidou.mybatisplus.core.metadata.IPage;
import info.spark.agent.annotation.ApiServiceMethod;
import info.spark.agent.core.ApiServiceDefinition;
import info.spark.agent.exception.AgentCodes;
import info.spark.agent.exception.AgentServiceException;
import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.basic.util.ClassUtils;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.base.BaseQuery;
import info.spark.starter.common.base.Bridge;
import info.spark.starter.common.base.ICrudDelegate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: agent service 通用接口, 方法名不能重复, 否则将被覆盖 </p>
 *
 * @param <S> 底层操作类
 * @param <Q> 查询参数实体
 * @param <D> 数据交换实体
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.14 20:23
 * @since 2.0.0
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@Slf4j
public abstract class AbstractAgentService<S extends ICrudDelegate<D>,
    Q extends BaseQuery<? extends Serializable>,
    D extends BaseDTO<? extends Serializable>> implements ApiServiceDefinition {

    /** 类类型的缓存 */
    private static final Map<String, Class<?>> CLASS_TYPE_CACHE = new ConcurrentHashMap<>(64);

    /** 服务 */
    @Autowired
    protected S service;

    /** 桥 */
    private Bridge<D> bridge;

    /**
     * 初始化
     *
     * @since 2.1.0
     */
    @PostConstruct
    public void init() {
        bridge = new Bridge<D>(service) {};
    }

    /**
     * 创建
     * 通过 D 新增数据, 不能传 id
     *
     * @param <I> parameter
     * @param dto dto
     * @return 返回新增的数据 id
     * @since 2.0.0
     */
    @NotNull
    @ApiServiceMethod(code = BasicConstant.Agent.CREATE)
    public <I extends Serializable> I create(@NotNull D dto) {
        return this.bridge.create(dto, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 创建批处理
     * 插入（批量）
     *
     * @param dtos dtos
     * @since 1.8.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.CREATE_BATCH)
    public void createBatch(@NotEmpty Collection<D> dtos) {
        String data = JsonUtils.toJson(dtos);
        List<D> ds = JsonUtils.toList(data, getDtoClass());
        this.bridge.createBatch(ds, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 创建忽略
     * 插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param <I> parameter
     * @param dto dto
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    @NotNull
    @ApiServiceMethod(code = BasicConstant.Agent.CREATE_IGNORE)
    public <I extends Serializable> I createIgnore(@NotNull D dto) {
        return this.bridge.createIgnore(dto, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 创建忽略批
     * 插入 (批量) ,如果已经存在相同的记录,则忽略当前新数据
     *
     * @param dtos dats
     * @since 1.0.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.CREATE_IGNORE_BATCH)
    public void createIgnoreBatch(@NotEmpty Collection<D> dtos) {
        String data = JsonUtils.toJson(dtos);
        List<D> ds = JsonUtils.toList(data, getDtoClass());
        this.bridge.createIgnoreBatch(ds, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 创建替代
     * 表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param <I> parameter
     * @param dto dto
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    @NotNull
    @ApiServiceMethod(code = BasicConstant.Agent.CREATE_REPLACE)
    public <I extends Serializable> I createReplace(@NotNull D dto) {
        return this.bridge.createReplace(dto, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 创建取代批
     * 插入 (批量) ,表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param dtos dats
     * @since 1.0.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.CREATE_REPLACE_BATCH)
    public void createReplaceBatch(@NotEmpty Collection<D> dtos) {
        String data = JsonUtils.toJson(dtos);
        List<D> ds = JsonUtils.toList(data, getDtoClass());
        this.bridge.createReplaceBatch(ds, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 创建或更新
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param <I> parameter
     * @param dto dto
     * @return the boolean
     * @since 1.8.0
     */
    @NotNull
    @ApiServiceMethod(code = BasicConstant.Agent.CREATE_UPDATE)
    public <I extends Serializable> I createOrUpdate(@NotNull D dto) {
        return this.bridge.createOrUpdate(dto, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 创建或更新批
     * 批量修改插入
     *
     * @param dtos dtos
     * @since 1.8.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.CREATE_UPDATE_BATCH)
    public void createOrUpdateBatch(@NotEmpty Collection<D> dtos) {
        String data = JsonUtils.toJson(dtos);
        List<D> ds = JsonUtils.toList(data, getDtoClass());
        this.bridge.createOrUpdateBatch(ds, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 更新
     * 通过 D 更新数据
     *
     * @param dto entity
     * @since 1.7.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.UPDATE)
    public void update(@NotNull D dto) {
        this.bridge.update(dto, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 批处理更新
     * 根据ID 批量更新
     *
     * @param dtos dtos
     * @since 1.8.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.UPDATE_BATCH)
    public void updateBatch(@NotEmpty Collection<D> dtos) {
        String data = JsonUtils.toJson(dtos);
        List<D> ds = JsonUtils.toList(data, getDtoClass());
        this.bridge.updateBatch(ds, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 删除
     * 通过 id 删除数据
     *
     * @param id id
     * @since 1.7.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.DELETE)
    public void delete(@NotNull Serializable id) {
        this.bridge.delete(id, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 删除由ids
     * 根据多个 id 删除数据
     *
     * @param <I> parameter
     * @param ids ids
     * @since 2.0.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.DELETE_BATCH)
    public <I extends Serializable> void deleteBatch(@NotEmpty Collection<I> ids) {
        this.bridge.delete(ids, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 按地图删除
     * Delete
     *
     * @param columnMap column map
     * @since 2.0.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.DELETE_BY_CONDITION)
    public void deleteByMap(@NotEmpty Map<String, Object> columnMap) {
        this.bridge.delete(columnMap, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 重要的查询
     * 查询总记录数
     *
     * @param query query
     * @return the int
     * @since 1.8.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.COUNTS_BY_QUERY)
    public int countsByQuery(@NotNull Q query) {
        return this.bridge.counts(query, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 计数
     * 查询总记录数
     *
     * @param noParams 为满足 agent service 入参要求添加的无用参数
     * @return the int
     * @since 1.8.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.COUNTS)
    public int counts(Void noParams) {
        return this.bridge.counts(AgentCodes.INVOKER_ERROR);
    }

    /**
     * 发现通过id
     * 通过 id 获取数据.
     *
     * @param <I> parameter
     * @param id  数据 id
     * @return dto 数据对象
     * @since 2.0.0
     */
    @Nullable
    @ApiServiceMethod(code = BasicConstant.Agent.FIND)
    public <I extends Serializable> D find(@NotNull I id) {
        return this.bridge.find(id, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 发现通过查询
     * 根据查询条件获取一条数据, 底层调用的是 page sql.
     *
     * @param query query
     * @return the d
     * @throws AgentServiceException agent service exception
     * @since 2.0.0
     */
    @Nullable
    @ApiServiceMethod(code = BasicConstant.Agent.FIND_BY_QUERY)
    public D findByQuery(@NotNull Q query) {
        return this.bridge.find(query, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 发现由ids
     * 查询（根据ID 批量查询）
     *
     * @param <I> parameter
     * @param ids 主键ID列表
     * @return the list
     * @since 1.8.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.FIND_BATCH)
    public <I extends Serializable> List<D> findBatch(@NotEmpty Collection<I> ids) {
        return this.bridge.find(ids, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 找到所有
     * 查询所有的 D 数据
     *
     * @param noParams 为满足 agent service 入参要求添加的无用参数
     * @return the list
     * @since 1.8.0
     */
    @ApiServiceMethod(code = BasicConstant.Agent.FIND_ALL)
    public List<D> findAll(Void noParams) {
        return this.bridge.find(AgentCodes.INVOKER_ERROR);
    }

    /**
     * 页面
     * 获取分页数据.
     *
     * @param query 查询参数
     * @return page 分页数据
     * @since 2.0.0
     */
    @NotNull
    @ApiServiceMethod(code = BasicConstant.Agent.PAGE)
    public IPage<D> page(@NotNull Q query) {
        return this.bridge.page(query, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 列表
     * 获取列表数据, 根据 limit 返回指定数量的数据.
     *
     * @param query 查询参数
     * @return list 数据
     * @since 2.0.0
     */
    @NotNull
    @ApiServiceMethod(code = BasicConstant.Agent.LIST)
    public List<D> list(@NotNull Q query) {
        return this.bridge.list(query, AgentCodes.INVOKER_ERROR);
    }

    /**
     * 得到dto类
     * 获取当前泛型参数类型
     *
     * @return Class reference class d
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    private Class<D> getDtoClass() {
        return (Class<D>) CLASS_TYPE_CACHE.computeIfAbsent(this.getClass().getName(), k -> ClassUtils.getSuperClassT(this.getClass(), 2));
    }

}
