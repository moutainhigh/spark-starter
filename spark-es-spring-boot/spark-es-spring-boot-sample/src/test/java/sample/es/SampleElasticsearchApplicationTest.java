package sample.es;

import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.test.SparkTest;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.13 18:12
 * @since 1.5.0
 */
@Slf4j
@SparkTest(classes = {SampleElasticsearchApplication.class})
class SampleElasticsearchApplicationTest {

    /** Client */
    @Resource
    private RestHighLevelClient client;
    /** INDEX */
    private static final String INDEX = "test_index";

    /**
     * 创建索引
     *
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Test
    void testCreateIndex() throws IOException {

        // 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(INDEX);
        // 客户端执行: 创建索引的请求
        CreateIndexResponse createIndexResponse = this.client.indices().create(request, RequestOptions.DEFAULT);
        log.info("index: {}", createIndexResponse.index());
    }

    /**
     * 判断索引是否存在
     *
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("tracer_log_run_20200713");
        boolean exists = this.client.indices().exists(request, RequestOptions.DEFAULT);
        log.info("{}", exists);
    }

    /**
     * 删除索引
     *
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("tracer_log_run_20200718");
        AcknowledgedResponse response = this.client.indices().delete(request, RequestOptions.DEFAULT);
        log.info("{}", response.isAcknowledged());
    }

    /**
     * 测试添加一个文档
     *
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Test
    void testAddDocument() throws IOException {

        User user = new User("dong4j", 30);

        String userJson = JsonUtils.toJson(user);

        IndexRequest request = new IndexRequest("person");
        request.source(userJson, XContentType.JSON);
        IndexResponse indexResponse = this.client.index(request, RequestOptions.DEFAULT);

        log.info("{}", indexResponse.getResult());
    }

    /**
     * 测试获取文档: 判断是否存在指定的文档
     *
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Test
    void tesIsExists() throws IOException {
        GetRequest Request = new GetRequest("person", "WwbXR3MBDu3lnASzFsRh");
        boolean exists = this.client.exists(Request, RequestOptions.DEFAULT);
        log.info("{}", exists);
    }

    /**
     * 获取一个文档信息
     *
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Test
    void testGetDocument() throws IOException {
        GetRequest Request = new GetRequest("person", "WwbXR3MBDu3lnASzFsRh");
        GetResponse documentFields = this.client.get(Request, RequestOptions.DEFAULT);
        log.info("{}", documentFields);
        log.info("{}", documentFields.getSourceAsString());
    }

    /**
     * 更新一个文档信息
     *
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Test
    void updateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("person", "WwbXR3MBDu3lnASzFsRh");
        User user = new User("dong2go", 108);
        updateRequest.doc(JsonUtils.toJson(user), XContentType.JSON);
        UpdateResponse updateResponse = this.client.update(updateRequest, RequestOptions.DEFAULT);
        log.info("{}", updateResponse.getResult());
    }

    /**
     * 删除一个文档
     *
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Test
    void deleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("person", "WwbXR3MBDu3lnASzFsRh");
        DeleteResponse deleteResponse = this.client.delete(deleteRequest, RequestOptions.DEFAULT);

        log.info("{}", deleteResponse);
    }

    /**
     * 批量操作
     *
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Test
    void bulkInsert() throws IOException {
        ArrayList<Object> userList = new ArrayList<>();
        userList.add(new User("dong4j1", 19));
        userList.add(new User("dong4j2", 19));
        userList.add(new User("dong4j3", 19));

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        for (Object o : userList) {
            bulkRequest.add(new IndexRequest("person").source(JsonUtils.toJson(o), XContentType.JSON));
        }
        BulkResponse bulkResponse = this.client.bulk(bulkRequest, RequestOptions.DEFAULT);
        // 是否失败, false代表成功
        log.info("{}", bulkResponse.hasFailures());
    }

    /**
     * 查询
     *
     * @throws IOException io exception
     * @since 1.5.0
     */
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("person");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "dong4j");
        sourceBuilder.query(termQueryBuilder);

        // 查询所有
        MatchAllQueryBuilder allQueryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(allQueryBuilder);

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = this.client.search(searchRequest, RequestOptions.DEFAULT);

        log.info("{}", JsonUtils.toJson(searchResponse.getHits()));
        System.out.println("========================");
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            log.info("{}", hit.getSourceAsMap());
        }

    }
}
