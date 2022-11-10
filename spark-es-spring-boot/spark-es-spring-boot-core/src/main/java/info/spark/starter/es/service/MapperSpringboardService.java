package info.spark.starter.es.service;

import com.google.common.collect.Lists;

import info.spark.starter.util.CollectionUtils;
import info.spark.starter.es.annotation.Exposed;
import info.spark.starter.es.entity.document.BaseElasticEntity;
import info.spark.starter.es.entity.exception.BossElasticException;
import info.spark.starter.es.enums.ErrorType;
import info.spark.starter.es.exception.EsErrorCodes;
import info.spark.starter.es.mapper.structure.Mapper;
import info.spark.starter.support.Page;

import org.frameworkset.elasticsearch.ElasticSearchException;
import org.frameworkset.elasticsearch.client.ClientInterface;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 跳板 使用BaseElasticService 中的方法会代理到本类中，进行处理后调用对应的service </p>
 * springboard
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 15:03
 * @since 1.7.1
 */
@Slf4j
public class MapperSpringboardService extends AbstractElasticService {

    private static final long serialVersionUID = -7518891935006701520L;
    /** Elastic index client */
    @Resource
    private ElasticIndexService elasticIndexService;
    /** Elastic crud service */
    @Resource
    private ElasticCrudService elasticCrudService;

    /**
     * Save update
     *
     * @param <T>    parameter
     * @param mapper mapper
     * @param bean   bean
     * @return the object
     * @since 1.7.1
     */
    @Exposed
    <T extends BaseElasticEntity<?>> boolean insert(Mapper mapper, T bean) {
        try {
            this.elasticCrudService.insert(bean);
            return true;
        } catch (ElasticSearchException exception) {
            BossElasticException exceptionEntity = BossElasticException.parseException(exception);
            if (exceptionEntity.getError().getType().equals(ErrorType.INDEX_NOT_FOUND_EXCEPTION)) {
                log.warn("索引 {} 不存在，将使用注解mapping进行自动创建索引", mapper.getIndex());
                boolean index = this.elasticIndexService.createIndex(mapper.getIndex(), mapper.getMapping());
                if (index) {
                    log.debug("索引 {} 自动创建成功 mapping 脚本为 {}", mapper.getIndex(), mapper.getMapping());
                    this.elasticCrudService.insert(bean);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Save update many
     *
     * @param <T>    parameter
     * @param mapper mapper
     * @param beans  beans
     * @return the object
     * @since 1.7.1
     */
    @Exposed
    <T extends BaseElasticEntity<?>> boolean insertMany(Mapper mapper, List<T> beans) {
        if (CollectionUtils.isEmpty(beans)) {
            return true;
        }
        LinkedList<T> linkedList = Lists.newLinkedList(beans);
        this.insert(mapper, linkedList.pollFirst());
        if (linkedList.size() > 1) {
            this.elasticCrudService.insertMany(linkedList);
        }
        return true;
    }

    /**
     * Delete
     *
     * @param mapper mapper
     * @param id     id
     * @return the object
     * @since 1.7.1
     */
    @Exposed
    Object delete(Mapper mapper, Serializable... id) {
        this.elasticCrudService.delete(mapper.getIndex(), Arrays.stream(id).map(String::valueOf).toArray(String[]::new));
        return true;
    }

    /**
     * Gets one *
     *
     * @param <T>    实体
     * @param <I>    id
     * @param mapper mapper
     * @param id     id
     * @return the one
     * @since 1.7.1
     */
    @Exposed
    <I extends Serializable, T extends BaseElasticEntity<I>> T getOne(Mapper mapper, Serializable id) {
        //noinspection unchecked
        return (T) this.elasticCrudService.getOne(mapper.getIndex(), mapper.getType(), String.valueOf(id));
    }


    /**
     * findAll
     *
     * @param <T>    parameter
     * @param mapper mapper
     * @return the List
     * @since 1.7.1
     */
    @Exposed
    <T extends BaseElasticEntity<?>> List<T> findAll(Mapper mapper) {
        //noinspection unchecked
        return (List<T>) this.elasticCrudService.findAll(mapper.getIndex(), mapper.getType());
    }

    /**
     * Gets by field *
     *
     * @param <T>    parameter
     * @param mapper mapper
     * @param filed  filed
     * @param value  value
     * @param like   like
     * @return the by field
     * @since 1.7.1
     */
    @Exposed
    <T extends BaseElasticEntity<?>> T getByField(Mapper mapper, String filed, Object value, boolean like) {
        //noinspection unchecked
        return (T) this.elasticCrudService.getByField(mapper.getIndex(), mapper.getType(), filed, value, like);
    }

    /**
     * Gets by field *
     *
     * @param <T>    parameter
     * @param mapper mapper
     * @param filed  filed
     * @param value  value
     * @param page   page
     * @param like   like
     * @return the by field
     * @since 1.7.1
     */
    @Exposed
    <T extends BaseElasticEntity<?>> Page<T> getListByField(Mapper mapper, String filed, Object value, @Nullable Page<?> page,
                                                            boolean like) {
        //noinspection unchecked
        return (Page<T>) this.elasticCrudService.getListByField(mapper.getIndex(), mapper.getType(), filed, value, page, like);
    }

    /**
     * Update by document
     *
     * @param <T>    parameter
     * @param mapper mapper
     * @param entity entity
     * @return the boolean
     * @since 1.8.0
     */
    @Exposed
    <T extends BaseElasticEntity<?>> boolean update(Mapper mapper, T entity) {
        EsErrorCodes.DATA_NOT_FOUNT.notNull(entity);
        EsErrorCodes.PRIMARY_KEY_EMPTY_VALUE.notNull(entity.getId());

        return this.elasticCrudService.update(mapper.getIndex(), Lists.newArrayList(entity));
    }

    /**
     * Update by documents
     *
     * @param <T>    parameter
     * @param mapper mapper
     * @param entity entity
     * @return the boolean
     * @since 1.8.0
     */
    @Exposed
    <T extends BaseElasticEntity<?>> boolean updateMany(Mapper mapper, List<T> entity) {
        EsErrorCodes.DATA_NOT_FOUNT.notNull(entity);
        EsErrorCodes.PRIMARY_KEY_EMPTY_VALUE.isTrue(entity.stream().map(BaseElasticEntity::getId).count() == entity.size());

        return this.elasticCrudService.update(mapper.getIndex(), Lists.newArrayList(entity));
    }

    /**
     * 获取 bboss 的 ClientInterface 客户端，在service在调用操作
     *
     * @param mapper mapper
     * @return the base client
     * @since 1.8.0
     */
    @Exposed
    ClientInterface getBaseClient(Mapper mapper) {
        return this.getClient(mapper.getMapperXml());
    }
}
