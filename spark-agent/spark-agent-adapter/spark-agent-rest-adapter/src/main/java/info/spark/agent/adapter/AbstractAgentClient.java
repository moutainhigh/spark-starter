package info.spark.agent.adapter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.type.TypeReference;
import info.spark.agent.adapter.client.AgentClient;
import info.spark.agent.adapter.client.AgentOptional;
import info.spark.agent.adapter.client.ResultActions;
import info.spark.agent.adapter.client.ResultCallback;
import info.spark.agent.adapter.enums.AgentClientErrorCodes;
import info.spark.agent.adapter.exception.AgentClientException;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.basic.util.ClassUtils;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.base.BaseQuery;
import info.spark.starter.common.base.IBaseEntity;
import info.spark.starter.support.Page;

import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 * todo-dong4j : (2021.10.31 19:33) [添加 异步 接口]
 *
 * @param <A> parameter
 * @param <Q> 入参
 * @param <D> 不能加 {@code extends BaseDTO<? extends Serializable>} 反序列化会失败
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.27 18:09
 * @since 2.0.0
 */
@Slf4j
@SuppressWarnings(value = {"SpringJavaAutowiredMembersInspection", "checkstyle:MethodLimit"})
public abstract class AbstractAgentClient<A extends AgentClient,
    Q extends BaseQuery<? extends Serializable>,
    D> implements IClient {

    /** 缓存子类类名和对应的 D 的类型 */
    private static final Map<String, Class<?>> CLASS_TYPE_CACHE = new ConcurrentHashMap<>(64);

    /** 代理客户端, 只要引入 sdk 就必须在启动类注入，否则启动失败。 为避免引入 sdk 后未使用造成启动失败的问题使用不强制注入 */
    @Getter
    @Autowired(required = false)
    protected A client;

    /**
     * 新增数据（业务端直接使用此方法需要进行参数检查）
     *
     * @param <I> parameter
     * @param dto dto
     * @return the serializable
     * @throws AgentClientException agent client exception
     * @since 2.0.0
     */
    @NotNull
    public <I extends Serializable> I create(@NotNull D dto) throws AgentClientException {
        Assertions.notNull(dto, () -> new AgentClientException("新增实体不能为 null"));
        Assertions.isFalse(this.hasIdValue(dto), () -> new AgentClientException("新增数据时不能传入 id"));
        return this.createOrUpdate(dto);
    }

    /**
     * 创建批处理
     * 保存
     *
     * @param dto dto
     * @throws AgentClientException agent client exception
     * @since 2.0.0
     */
    public void createBatch(Collection<D> dto) throws AgentClientException {
        Assertions.notEmpty(dto, () -> new AgentClientException("新增实体不能为 null"));
        this.client.api(BasicConstant.Agent.CREATE_BATCH)
            .params(dto)
            .perform()
            .failException(() -> new AgentClientException("操作失败"));
    }

    /**
     * 创建批处理
     * 插入（批量）
     *
     * @param dtos      dtos
     * @param batchSize 插入批次数量
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public void createBatch(Collection<D> dtos, int batchSize) throws AgentClientException {
        log.warn("不支持自定义 batchSize， 可直接使用 createBatch(Collection<D> dto)");
        createBatch(dtos);
    }

    /**
     * 创建忽略
     * 插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param <I> parameter
     * @param dto dto
     * @return 是否成功 boolean
     * @throws AgentClientException agent client exception
     * @since 1.0.0
     */
    public <I extends Serializable> I createIgnore(@NotNull D dto) throws AgentClientException {
        Assertions.notNull(dto, () -> new AgentClientException("新增实体不能为 null"));
        Assertions.isFalse(this.hasIdValue(dto), () -> new AgentClientException("新增数据时不能传入 id"));

        //noinspection unchecked
        return (I) this.client.api(BasicConstant.Agent.CREATE_IGNORE)
            .params(dto)
            .perform()
            .failException(() -> new AgentClientException(this.hasIdValue(dto) ? "更新操作失败" : "新增操作失败"))
            .andReturn()
            .expect(this.getIdType(dto))
            .orElseThrow(AgentClientException::new);
    }

    /**
     * 创建忽略批
     * 插入 (批量) ,插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param dtos dats
     * @throws AgentClientException agent client exception
     * @since 1.0.0
     */
    public void createIgnoreBatch(Collection<D> dtos) throws AgentClientException {
        Assertions.notEmpty(dtos, () -> new AgentClientException("新增实体不能为 null"));
        this.client.api(BasicConstant.Agent.CREATE_IGNORE_BATCH)
            .params(dtos)
            .perform()
            .failException(() -> new AgentClientException("操作失败"));
    }

    /**
     * 创建忽略批
     * 插入 (批量) ,插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param dtos      dats
     * @param batchSize 批次大小
     * @throws AgentClientException agent client exception
     * @since 1.0.0
     */
    public void createIgnoreBatch(Collection<D> dtos, int batchSize) throws AgentClientException {
        log.warn("不支持自定义 batchSize， 可直接使用 createIgnoreBatch(Collection<D> dto)");
        createIgnoreBatch(dtos);
    }

    /**
     * 创建替代
     * 表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param <I> parameter
     * @param dto dto
     * @return 是否成功 boolean
     * @throws AgentClientException agent client exception
     * @since 1.0.0
     */
    public <I extends Serializable> I createReplace(@NotNull D dto) throws AgentClientException {
        Assertions.notNull(dto, () -> new AgentClientException("新增实体不能为 null"));
        Assertions.isFalse(this.hasIdValue(dto), () -> new AgentClientException("新增数据时不能传入 id"));

        //noinspection unchecked
        return (I) this.client.api(BasicConstant.Agent.CREATE_REPLACE)
            .params(dto)
            .perform()
            .failException(() -> new AgentClientException(this.hasIdValue(dto) ? "更新操作失败" : "新增操作失败"))
            .andReturn()
            .expect(this.getIdType(dto))
            .orElseThrow(AgentClientException::new);
    }

    /**
     * 创建取代批
     * 插入 (批量) ,表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param dtos dats
     * @throws AgentClientException agent client exception
     * @since 1.0.0
     */
    public void createReplaceBatch(Collection<D> dtos) throws AgentClientException {
        Assertions.notEmpty(dtos, () -> new AgentClientException("新增实体不能为 null"));
        this.client.api(BasicConstant.Agent.CREATE_REPLACE_BATCH)
            .params(dtos)
            .perform()
            .failException(() -> new AgentClientException("操作失败"));
    }

    /**
     * 创建取代批
     * 插入 (批量) ,表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param dtos      dats
     * @param batchSize 批次大小
     * @throws AgentClientException agent client exception
     * @since 1.0.0
     */
    public void createReplaceBatch(Collection<D> dtos, int batchSize) throws AgentClientException {
        log.warn("不支持自定义 batchSize， 可直接使用 createReplaceBatch(Collection<D> dto)");
        createReplaceBatch(dtos);
    }

    /**
     * 创建或更新
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param <I> parameter
     * @param dto dto
     * @return the boolean
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public <I extends Serializable> I createOrUpdate(@NotNull D dto) throws AgentClientException {
        Assertions.notNull(dto, () -> new AgentClientException("新增/跟新 实体不能为 null"));
        //noinspection unchecked
        return (I) this.client.api(BasicConstant.Agent.CREATE_UPDATE)
            .params(dto)
            .perform()
            .failException(() -> new AgentClientException(this.hasIdValue(dto) ? "更新操作失败" : "新增操作失败"))
            .andReturn()
            .expect(this.getIdType(dto))
            .orElseThrow(AgentClientException::new);
    }

    /**
     * 创建或更新批
     *
     * @param dtos dto
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void createOrUpdateBatch(Collection<D> dtos) throws AgentClientException {
        Assertions.notEmpty(dtos, () -> new AgentClientException("批量更新实体不能为 null"));
        this.client.api(BasicConstant.Agent.CREATE_UPDATE_BATCH)
            .params(dtos)
            .perform()
            .failException(() -> new AgentClientException("操作失败"));
    }

    /**
     * 创建或更新批
     * 批量修改插入
     *
     * @param dtos      dtos
     * @param batchSize 每次的数量
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public void createOrUpdateBatch(Collection<D> dtos, int batchSize) throws AgentClientException {
        log.warn("不支持自定义 batchSize， 可直接使用 createOrUpdateBatch(Collection<D> dto)");
        createOrUpdateBatch(dtos);
    }

    /**
     * 更新
     * 通过 D 更新数据
     *
     * @param dto entity
     * @throws AgentClientException agent client exception
     * @since 1.7.0
     */
    public void update(@NotNull D dto) throws AgentClientException {
        Assertions.isTrue(this.hasIdValue(dto), () -> new AgentClientException("更新数据时必须传入 id"));
        this.createOrUpdate(dto);
    }

    /**
     * 批处理更新
     *
     * @param dto dto
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public void updateBatch(Collection<D> dto) throws AgentClientException {
        Assertions.notEmpty(dto, () -> new AgentClientException("批量更新实体不能为 null"));
        this.client.api(BasicConstant.Agent.UPDATE_BATCH)
            .params(dto)
            .perform()
            .failException(() -> new AgentClientException("操作失败"));
    }

    /**
     * 批处理更新
     * 根据ID 批量更新
     *
     * @param dtos      dtos
     * @param batchSize 更新批次数量
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public void updateBatch(Collection<D> dtos, int batchSize) throws AgentClientException {
        log.warn("不支持自定义 batchSize， 可直接使用 updateBatch(Collection<D> dto)");
        updateBatch(dtos);
    }

    /**
     * asyn保存或更新
     * Asyn save or update
     * todo-dong4j : (2021.10.31 21:02) [未完成]
     *
     * @param dto dto
     * @return the completable future
     * @throws AgentClientException agent client exception
     * @since 2.0.0
     */
    @NotNull
    public CompletableFuture<ResultActions> asynSaveOrUpdate(@NotNull D dto) throws AgentClientException {
        return this.client.api(BasicConstant.Agent.CREATE_UPDATE)
            .params(dto)
            .asyncPerform();
    }

    /**
     * 删除
     * 通过 id 删除数据
     *
     * @param <I> parameter
     * @param id  id
     * @throws AgentClientException agent client exception
     * @since 1.7.0
     */
    public <I extends Serializable> void delete(@NotNull I id) throws AgentClientException {
        Assertions.notNull(id, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        this.client.api(BasicConstant.Agent.DELETE)
            .pathVariable(id)
            .perform()
            .failException(AgentClientException.class);
    }

    /**
     * 删除
     * 批量删除
     *
     * @param <I> parameter
     * @param ids ids
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public <I extends Serializable> void delete(Collection<I> ids) throws AgentClientException {
        Assertions.isFalse(CollectionUtils.isEmpty(ids), () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        this.client.api(BasicConstant.Agent.DELETE_BATCH)
            .params(ids)
            .perform()
            .failException(AgentClientException.class);
    }

    /**
     * 删除
     * Delete
     *
     * @param columnMap column map
     * @throws AgentClientException agent client exception
     * @since 2.0.0
     */
    public void delete(@NotNull Map<String, Object> columnMap) throws AgentClientException {
        Assertions.isFalse(CollectionUtils.isEmpty(columnMap), () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        this.client.api(BasicConstant.Agent.DELETE_BY_CONDITION)
            .params(columnMap)
            .perform()
            .failException(AgentClientException.class);
    }

    /**
     * 计数
     * 查询总记录数
     *
     * @return the int
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public int counts() throws AgentClientException {
        return this.client.api(BasicConstant.Agent.COUNTS)
            .perform()
            .failException(AgentClientException.class)
            .andReturn()
            .expect(Integer.class)
            .orElse(0);
    }

    /**
     * 计数
     * 查询总记录数
     *
     * @param query 查询
     * @return the int
     * @throws AgentClientException agent client exception
     * @since 2.1.0
     */
    public int counts(@NotNull Q query) throws AgentClientException {
        return this.client.api(BasicConstant.Agent.COUNTS_BY_QUERY)
            .params(query)
            .perform()
            .failException(AgentClientException.class)
            .andReturn()
            .expect(Integer.class)
            .orElse(0);
    }

    /**
     * 找到
     * 通过 id 查询 D 实体
     *
     * @param <I> parameter
     * @param id  id
     * @return the t
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public <I extends Serializable> D find(@NotNull I id) throws AgentClientException {
        Assertions.notNull(id, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        return this.client.api(BasicConstant.Agent.FIND)
            .pathVariable(id)
            .perform()
            .failException(AgentClientException.class)
            .andReturn()
            .expect(this.getDtoClass())
            .orElse(null);
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
        return this.client.api(BasicConstant.Agent.FIND_BY_QUERY)
            .params(query)
            .perform()
            .failException(AgentClientException.class)
            .andReturn()
            .expect(this.getDtoClass())
            .orElse(null);
    }

    /**
     * 找到
     * 查询（根据ID 批量查询）
     *
     * @param <I> parameter
     * @param ids 主键ID列表
     * @return the list
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public <I extends Serializable> List<D> find(Collection<I> ids) throws AgentClientException {
        List<D> list = this.client.api(BasicConstant.Agent.FIND_BATCH)
            .params(ids)
            .perform()
            .failException(AgentClientException.class)
            .andReturn()
            .expect(new TypeReference<List<D>>() {})
            .orElse(Collections.emptyList());
        // 泛型被擦除, 返回的数据为  LinkedHashMap, 需要再次转换
        return list.stream().map(d -> JsonUtils.parse(JsonUtils.toJson(d), this.getDtoClass())).collect(Collectors.toList());
    }

    /**
     * 得到
     * 根据 query 查询单条数据
     *
     * @param query query
     * @return 返回 optionl 让业务端自行处理
     * @throws AgentClientException agent client exception
     * @since 2.0.0
     */
    @NotNull
    public AgentOptional<D> get(@NotNull Q query) throws AgentClientException {
        return this.client.api(BasicConstant.Agent.FIND_BY_QUERY)
            .params(query)
            .perform()
            .failException(AgentClientException.class)
            .andReturn()
            .expect(this.getDtoClass());
    }


    /**
     * 找到所有
     * 查询所有的 D 数据
     *
     * @return the list
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public List<D> find() throws AgentClientException {
        List<D> list = this.client.api(BasicConstant.Agent.FIND_ALL)
            .perform()
            .failException(AgentClientException.class)
            .andReturn()
            .expect(new TypeReference<List<D>>() {})
            .orElse(Collections.emptyList());
        // 泛型被擦除, 返回的数据为  LinkedHashMap, 需要再次转换
        return list.stream().map(d -> JsonUtils.parse(JsonUtils.toJson(d), this.getDtoClass())).collect(Collectors.toList());
    }

    /**
     * 页面
     * 通过参数查询 D 分页数据
     *
     * @param query 查询参数
     * @return the page
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public IPage<D> page(@NotNull Q query)
        throws AgentClientException {
        Assertions.notNull(query, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        return this.client.api(BasicConstant.Agent.PAGE)
            .params(query)
            .perform()
            .print(Level.DEBUG)
            .failException(AgentClientException.class)
            .andReturn()
            .expect(new TypeReference<Page<D>>() {})
            .orElse(new Page<>()).convert(d -> JsonUtils.parse(JsonUtils.toJson(d), this.getDtoClass()));
    }

    /**
     * 列表
     * 通过参数查询 D list 数据
     *
     * @param query 查询参数
     * @return the list
     * @throws AgentClientException agent client exception
     * @since 1.8.0
     */
    public List<D> list(@NotNull Q query)
        throws AgentClientException {
        Assertions.notNull(query, () -> new AgentClientException(AgentClientErrorCodes.PARAM_VERIFY_ERROR));
        return this.client.api(BasicConstant.Agent.LIST)
            .params(query)
            .perform()
            .failException(AgentClientException.class)
            .andReturn()
            .expect(new TypeReference<List<D>>() {})
            .orElse(Collections.emptyList())
            .stream()
            .map(d -> JsonUtils.parse(JsonUtils.toJson(d), this.getDtoClass()))
            .collect(Collectors.toList());
    }

    /**
     * 保存与回调
     * Save with callback
     *
     * @param dto      dto
     * @param callback callback
     * @since 2.0.0
     */
    public void saveWithCallback(@NotNull D dto, ResultCallback callback) {
        Assertions.notNull(callback, () -> new AgentClientException("回调接口必传"));
        Assertions.isFalse(this.hasIdValue(dto), () -> new AgentClientException("新增数据时不能传入 id"));
        this.saveOrUpdateWithCallback(dto, callback);
    }

    /**
     * 保存或更新回调
     * Save or update with callback
     *
     * @param dto      dto
     * @param callback callback
     * @since 2.0.0
     */
    public void saveOrUpdateWithCallback(@NotNull D dto, ResultCallback callback) {
        this.client.api(BasicConstant.Agent.CREATE_UPDATE)
            .params(dto)
            .perform()
            .callback(callback)
            .andReturn()
            .expect(this.getIdType(dto))
            .orElseThrow(AgentClientException::new);
    }


    /**
     * 得到dto类
     * 获取当前泛型参数类型
     *
     * @return Class reference class d
     * @since 2.0.0
     */
    @SuppressWarnings(value = {"unchecked", "checkstyle:Indentation"})
    Class<D> getDtoClass() {
        return (Class<D>) CLASS_TYPE_CACHE.computeIfAbsent(this.getClass().getName(),
                                                           k -> ClassUtils.getSuperClassT(this.getClass(), 2));
    }

    /**
     * 获取id类型
     * 获取 BaseDTO 第一个泛型(id 类型)
     *
     * @param <I> parameter
     * @param dto dto
     * @return the id type
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    <I extends Serializable> Class<I> getIdType(@NotNull D dto) {
        return (Class<I>) ClassUtils.getSuperClassT(dto.getClass(), 0);
    }

    /**
     * 有id值
     * 判断是否存在 id 值
     *
     * @param dto dto
     * @return the boolean
     * @since 2.0.0
     */
    boolean hasIdValue(@NotNull D dto) {
        Method getId = ReflectionUtils.findMethod(this.getDtoClass(), IBaseEntity.GET_ID);
        return getId != null && !Objects.isNull(ReflectionUtils.invokeMethod(getId, dto));
    }

}
