package info.spark.starter.es.service;

import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.es.entity.result.EsBool;
import info.spark.starter.es.entity.result.EsIndexResult;
import info.spark.starter.es.exception.EsErrorCodes;

import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.client.ClientInterfaceNew;
import org.frameworkset.elasticsearch.entity.ESIndice;

import java.util.List;

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
public class ElasticIndexService extends AbstractElasticService {

    /** serialVersionUID */
    private static final long serialVersionUID = 3785175095448817897L;

    /**
     * 为index建立别名
     *
     * @param indexName index name
     * @param alias     alias
     * @return the es bool
     * @since 1.7.1
     */
    public boolean addAlias(String indexName, String alias) {
        ClientInterface clientUtil = this.getClient();
        boolean exist = clientUtil.existIndice(indexName);
        if (exist) {
            String result = clientUtil.addAlias(indexName, alias);
            log.info("建立别名{} --> {}, 返回：{}", indexName, alias, result);
            return JsonUtils.parse(result, EsBool.class).getAcknowledged();
        } else {
            log.error("建立别名失败, {}, 索引不存在", indexName);
            return EsBool.FALSE.getAcknowledged();
        }
    }

    /**
     * 为index删除别名（使搜索不到）
     *
     * @param indexName index name
     * @param alias     alias
     * @return the es bool
     * @since 1.7.1
     */
    public boolean removeAlias(String indexName, String alias) {
        ClientInterface clientUtil = this.getClient();
        boolean exist = clientUtil.existIndice(indexName);
        if (exist) {
            String result = clientUtil.removeAlias(indexName, alias);
            log.info("删除别名返回{} --> {}, 返回：{}", indexName, alias, result);
            return JsonUtils.parse(result, EsBool.class).getAcknowledged();
        }
        log.error("索引【{}】不存在（index_not_found_exception）", indexName);
        return EsBool.FALSE.getAcknowledged();
    }

    /**
     * index是否存在
     *
     * @param indexName index name
     * @return the boolean
     * @since 1.7.1
     */
    public boolean existIndex(String indexName) {
        ClientInterface clientUtil = this.getClient();
        return clientUtil.existIndice(indexName);
    }

    /**
     * alias是否存在
     * 返回结构：
     * {
     * "spark_demo" : {
     * "aliases" : {
     * "别名" : { },
     * "别名2" : { }
     * }
     * }
     * }
     *
     * @param indexName index name
     * @param alias     alias
     * @return the object
     * @since 1.7.1
     */
    public boolean existAlias(String indexName, String alias) {
        ClientInterface clientUtil = this.getClient();
        boolean result = clientUtil.existIndice(indexName);
        // 因为是判断别名是否存在，如果连index都没有，不是返回false，而是异常
        EsErrorCodes.INDEX_NOT_FOUND.isTrue(result, indexName);

        String aliasStr = clientUtil.executeHttp(indexName + "/_alias", ClientInterface.HTTP_GET);
        return aliasStr.contains(alias);
    }

    /**
     * type是否存在
     *
     * @param indexName index name
     * @param typeName  type name
     * @return the boolean
     * @since 1.7.1
     */
    public boolean existIndicesType(String indexName, String typeName) {
        ClientInterface clientUtil = this.getClient();
        return clientUtil.existIndiceType(indexName, typeName);
    }

    /**
     * 创建索引index
     * {"acknowledged":true,"shards_acknowledged":true,"index":"demo1"}
     *
     * @param indexName     index name
     * @param mappingScript mapping script
     * @return the boolean
     * @since 1.7.1
     */
    public boolean createIndex(String indexName, String mappingScript) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        if (!clientUtil.existIndice(indexName)) {
            String result = clientUtil.createIndiceMapping(indexName, mappingScript);
            log.info("创建索引成功：" + indexName);
            return JsonUtils.parse(result, EsIndexResult.class).getAcknowledged();
        } else {
            log.error("索引已经存在!");
            return EsBool.FALSE.getAcknowledged();
        }
    }

    /**
     * 新建/修改type的mapping
     *
     * @param indexName index name
     * @param mapping   mapping
     * @return the boolean
     * @since 1.7.1
     */
    public boolean updateIndicesMapping(String indexName, String mapping) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        if (clientUtil.existIndiceType(indexName, ClientInterfaceNew._doc)) {
            log.info(indexName + "索引type(" + ClientInterfaceNew._doc + ")已存在，更新type");
        } else {
            log.info("创建" + indexName + "type：" + ClientInterfaceNew._doc);
        }
        String result = clientUtil.updateIndiceMapping(indexName + "/_mapping", mapping);
        return JsonUtils.parse(result, EsBool.class).getAcknowledged();
    }

    /**
     * 重建索引
     *
     * @param oldIndex old index
     * @param index    index
     * @return boolean boolean
     * @since 1.7.1
     */
    public boolean reindex(String oldIndex, String index) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        if (clientUtil.existIndice(oldIndex) && clientUtil.existIndice(index)) {
            clientUtil.reindex(oldIndex, index);
            return true;
        }
        return false;
    }

    /**
     * 删除index
     *
     * @param indexName index name
     * @return object boolean
     * @since 1.7.1
     */
    public boolean deleteIndex(String indexName) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        clientUtil.dropIndice(indexName);
        return true;
    }

    /**
     * 通过tag模糊匹配索引，获取索引名称
     *
     * @param tag tag
     * @return index name
     * @since 1.7.1
     */
    public String getIndexName(String tag) {
        //创建es客户端
        ClientInterface clientUtil = this.getClient();
        List<ESIndice> indiceList = clientUtil.getIndexes();
        long dataNum = 0L;
        String indexName = "";
        if (indiceList != null && !indiceList.isEmpty()) {
            for (ESIndice indice : indiceList) {
                if (indice.getIndex().contains(tag)) {
                    //若匹配到多个，取最后一个
                    long num = Long.parseLong(indice.getIndex().split("_date")[1]);
                    if (num > dataNum) {
                        dataNum = num;
                        indexName = indice.getIndex();
                    }
                }
            }
        }
        return indexName;
    }

}
