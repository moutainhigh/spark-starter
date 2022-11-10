package info.spark.starter.es.service;

import com.google.common.collect.Lists;

import info.spark.starter.es.entity.document.BaseElasticEntity;
import info.spark.starter.es.support.ElasticsearchUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.es.exception.EsErrorCodes;
import info.spark.starter.support.Page;

import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.client.ClientInterfaceNew;
import org.frameworkset.elasticsearch.entity.ESDatas;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 15:03
 * @since 1.7.1
 */
@Slf4j
public class ElasticCrudService extends AbstractElasticService {

    /** serialVersionUID */
    private static final long serialVersionUID = 6243328327626150651L;
    /** Elastic index client */
    @Resource
    private ElasticIndexService elasticIndexService;

    /**
     * 新增/更新一个文档
     *
     * @param <T>  parameter
     * @param bean bean
     * @return the object
     * @since 1.7.1
     */
    public <T extends BaseElasticEntity<?>> Object insert(T bean) {
        EsErrorCodes.DATA_NOT_FOUNT.notNull(bean);
        return this.insert(ElasticsearchUtils.getIndexFromBean(bean.getClass()), bean);
    }

    /**
     * Add document
     *
     * @param <T>   parameter
     * @param index index
     * @param bean  bean
     * @return the object
     * @since 1.7.1
     */
    public <T extends BaseElasticEntity<?>> Object insert(String index, T bean) {
        EsErrorCodes.DATA_NOT_FOUNT.notNull(bean);
        EsErrorCodes.PRIMARY_KEY_EMPTY_VALUE.notNull(bean.getId(), "主键不能为空!");

        ClientInterface clientUtil = this.getClient();
        return clientUtil.addDocument(index, bean);
    }

    /**
     * 批量 添加或更新 实体必须指定 @EsIndex
     *
     * @param <T>   parameter
     * @param beans beans
     * @return the object
     * @since 1.7.1
     */
    public <T extends BaseElasticEntity<?>> Object insertMany(List<T> beans) {
        ClientInterface clientUtil = this.getClient();
        return clientUtil.addDocuments(beans);
    }

    /**
     * 新增/更新一个文档
     * 根据elasticsearch.xml中指定的日期时间格式，生成对应时间段的索引表名称
     *
     * @param <T>       parameter
     * @param indexName index name
     * @param bean      bean
     * @return object object
     * @since 1.7.1
     */
    public <T extends BaseElasticEntity<?>> Object addDataDocument(String indexName, T bean) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        return clientUtil.addDateDocument(indexName, bean);
    }

    /**
     * 批量 新增/更新文档 因为有时间 index 前缀，必须带有index name
     * 根据elasticsearch.xml中指定的日期时间格式，生成对应时间段的索引表名称
     *
     * @param <T>       parameter
     * @param indexName index name
     * @param bean      bean
     * @return object object
     * @since 1.7.1
     */
    public <T extends BaseElasticEntity<?>> Object addDataDocuments(String indexName, List<T> bean) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        return clientUtil.addDateDocuments(indexName, bean);
    }

    /**
     * 删除一个文档
     *
     * @param indexName index name
     * @param id        id
     * @return object object
     * @since 1.7.1
     */
    public Object delete(String indexName, String... id) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        clientUtil.deleteDocuments(indexName, ClientInterfaceNew._doc, id);
        return true;
    }

    /**
     * 获取一个文档
     *
     * @param <T>       parameter
     * @param indexName index name
     * @param beanType  bean type
     * @param id        id
     * @return document document
     * @since 1.7.1
     */
    public <T extends BaseElasticEntity<?>> T getOne(String indexName, Class<T> beanType, String id) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        return clientUtil.getDocument(indexName, id, beanType);
    }

    /**
     * 获取一个文档
     *
     * @param <T>       parameter
     * @param indexName index name
     * @param beanType  bean type
     * @param filed     filed
     * @param value     value
     * @param like      like
     * @return document document
     * @since 1.7.1
     */
    public <T extends BaseElasticEntity<?>> T getByField(String indexName, Class<T> beanType, String filed, Object value, boolean like) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        if (like) {
            return clientUtil.getDocumentByFieldLike(indexName, filed, value, beanType);
        }
        return clientUtil.getDocumentByField(indexName, filed, value, beanType);
    }

    /**
     * Find all
     *
     * @param <T>       parameter
     * @param indexName index name
     * @param beanType  bean type
     * @return the list
     * @since 1.7.1
     */
    public <T extends BaseElasticEntity<?>> List<T> findAll(String indexName, Class<T> beanType) {
        return Optional
            .ofNullable(this.getClient().searchAllParallel(indexName, beanType, 4))
            .map(ESDatas::getDatas)
            .orElse(Lists.newArrayList());
    }

    /**
     * Update by document
     *
     * @param <T>        parameter
     * @param indexName  index name
     * @param entityList entity list
     * @return the boolean
     * @since 1.8.0
     */
    public <T extends BaseElasticEntity<?>> boolean update(String indexName, List<T> entityList) {
        if (null != entityList) {
            String result = this.getClient().updateDocuments(indexName, entityList);
            log.info("ES updateByDocument 结果为：{}", result);
            return StringUtils.isNotBlank(result) && result.contains("\"failed\":0");
        }
        return false;
    }

    /**
     * 获取文档
     *
     * @param <T>       parameter
     * @param indexName index name
     * @param beanType  bean type
     * @param filed     filed
     * @param value     value
     * @param page      page
     * @param like      like
     * @return document document
     * @since 1.7.1
     */
    @SuppressWarnings("ParameterNumber")
    public <T extends BaseElasticEntity<?>> Page<T> getListByField(String indexName,
                                                                   Class<T> beanType,
                                                                   String filed,
                                                                   Object value,
                                                                   @Nullable Page<?> page,
                                                                   boolean like) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        int size = null == page ? 10 : (int) page.getSize();
        int from = null == page ? 0 : (int) ((page.getCurrent() - 1) * page.getSize());
        ESDatas<T> esData;
        if (like) {
            esData = clientUtil.searchListByFieldLike(indexName, filed, value, beanType, from, size);
        } else {
            esData = clientUtil.searchListByField(indexName, filed, value, beanType, from, size);
        }
        Page<T> result = new Page<>();
        result.setCurrent(Optional.ofNullable(page).map(Page::getCurrent).orElse(1L));
        result.setPages(Optional.ofNullable(page).map(Page::getPages).orElse(10L));
        result.setTotal(Optional.ofNullable(esData).map(ESDatas::getTotalSize).orElse(0L));
        result.setRecords(Optional.ofNullable(esData).map(ESDatas::getDatas).orElse(Lists.newArrayList()));
        return result;
    }
}
