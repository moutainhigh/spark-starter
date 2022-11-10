package sample.es;

import info.spark.starter.es.service.BaseElasticService;

import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.13 19:16
 * @since 1.5.0
 */
@Slf4j
class BaseElasticServiceTest extends SampleElasticsearchApplicationTest {
    /** Base elastic service */
    @Resource
    private BaseElasticService<?> baseElasticService;

    /**
     * Create index
     *
     * @since 1.5.0
     */
    @Test
    void createIndex() {
    }

    /**
     * Index exist
     *
     * @throws Exception exception
     * @since 1.5.0
     */
    @Test
    void indexExist() throws Exception {
        log.info("{}", this.baseElasticService.indexExist("person"));
    }

    /**
     * Is exists index
     *
     * @since 1.5.0
     */
    @Test
    void isExistsIndex() {
    }

    /**
     * Build setting
     *
     * @since 1.5.0
     */
    @Test
    void buildSetting() {
    }

    /**
     * Insert or update one
     *
     * @since 1.5.0
     */
    @Test
    void insertOrUpdateOne() {
    }

    /**
     * Insert batch
     *
     * @since 1.5.0
     */
    @Test
    void insertBatch() {
    }

    /**
     * Delete batch
     *
     * @since 1.5.0
     */
    @Test
    void deleteBatch() {
    }

    /**
     * Search
     *
     * @since 1.5.0
     */
    @Test
    void search() {
    }

    /**
     * Delete index
     *
     * @since 1.5.0
     */
    @Test
    void deleteIndex() {
    }

    /**
     * Delete by query
     *
     * @since 1.5.0
     */
    @Test
    void deleteByQuery() {
    }
}
