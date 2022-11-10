package info.spark.starter.es.service;

import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.base.AbstractBaseEntity;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.12 16:13
 * @since 1.5.0
 */
@Slf4j
public abstract class BaseElasticService<T extends AbstractBaseEntity<?>> {

    /** Rest high level client */
    private final RestHighLevelClient restHighLevelClient;

    /**
     * Base elastic service
     *
     * @param restHighLevelClient rest high level client
     * @since 1.5.0
     */
    @Contract(pure = true)
    public BaseElasticService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * Create index
     *
     * @param idxName 索引名称
     * @param idxsql  索引描述
     * @since 1.5.0
     */
    public void createIndex(String idxName, String idxsql) {
        try {
            if (this.indexExist(idxName)) {
                log.error("idxName: [{}] 已经存在, idxSql: [{}]", idxName, idxsql);
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(idxName);
            this.buildSetting(request);
            request.mapping(idxsql, XContentType.JSON);
            CreateIndexResponse res = this.restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            if (!res.isAcknowledged()) {
                throw new RuntimeException("初始化失败");
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * index 是否存在
     *
     * @param idxName index名
     * @return boolean boolean
     * @throws Exception exception
     * @since 1.5.0
     */
    public boolean indexExist(String idxName) throws Exception {
        GetIndexRequest request = new GetIndexRequest(idxName);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        return !this.restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * index 是否存在
     *
     * @param idxName index名
     * @return boolean boolean
     * @throws Exception exception
     * @since 1.5.0
     */
    public boolean isExistsIndex(String idxName) throws Exception {
        return this.restHighLevelClient.indices().exists(new GetIndexRequest(idxName), RequestOptions.DEFAULT);
    }

    /**
     * 设置分片
     *
     * @param request request
     * @since 1.5.0
     */
    public void buildSetting(@NotNull CreateIndexRequest request) {
        request.settings(Settings.builder()
                             .put("index.number_of_shards", 3)
                             .put("index.number_of_replicas", 2));
    }

    /**
     * Insert or update one
     *
     * @param idxName index
     * @param entity  对象
     * @since 1.5.0
     */
    public void insertOrUpdateOne(String idxName, @NotNull T entity) {
        IndexRequest request = new IndexRequest(idxName);
        log.debug("Data : entity=[{}]", JsonUtils.toJson(entity));
        request.source(JsonUtils.toJson(entity), XContentType.JSON);
        try {
            this.restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量插入数据
     *
     * @param idxName index
     * @param list    带插入列表
     * @since 1.5.0
     */
    public void insertBatch(String idxName, @NotNull List<T> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(idxName)
                                             .source(JsonUtils.toJson(item), XContentType.JSON)));
        try {
            this.restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量删除
     *
     * @param idxName index
     * @param idList  待删除列表
     * @since 1.5.0
     */
    public void deleteBatch(String idxName, @NotNull Collection<T> idList) {
        BulkRequest request = new BulkRequest();
        idList.forEach(item -> request.add(new DeleteRequest(idxName, item.toString())));
        try {
            this.restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Search
     *
     * @param idxName index
     * @param builder 查询参数
     * @param c       结果类对象
     * @return the list
     * @since 1.5.0
     */
    public List<T> search(String idxName, SearchSourceBuilder builder, Class<T> c) {
        SearchRequest request = new SearchRequest(idxName);
        request.source(builder);
        try {
            SearchResponse response = this.restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JsonUtils.parse(hit.getSourceAsString(), c));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除index
     *
     * @param index idx name
     * @return void boolean
     * @since 1.5.0
     */
    public boolean deleteIndex(String index) {
        try {
            if (this.indexExist(index)) {
                log.error("index: [{}] 已经存在", index);
                return false;
            }
            AcknowledgedResponse delete = this.restHighLevelClient
                .indices()
                .delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
            return delete.isAcknowledged();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * IndexName by query
     *
     * @param idxName idx name
     * @param builder builder
     * @since 1.5.0
     */
    public void deleteByQuery(String idxName, QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(idxName);
        request.setQuery(builder);
        // 设置批量操作数量, 最大为 10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            BulkByScrollResponse bulkByScrollResponse = this.restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
            log.info("deleted: [{}]", bulkByScrollResponse.getStatus().getDeleted());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
