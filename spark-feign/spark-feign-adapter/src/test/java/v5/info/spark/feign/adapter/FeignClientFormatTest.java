package v5.info.spark.feign.adapter;

import info.spark.feign.adapter.client.FeignClient1;
import info.spark.feign.adapter.client.FeignClient2;
import info.spark.feign.adapter.client.FeignClient3;
import info.spark.feign.adapter.client.FeignClient4;
import info.spark.feign.adapter.client.FeignClient8;

import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.28 23:09
 * @since 1.0.0
 */
@Slf4j
class FeignClientFormatTest extends BaseTestApplication {
    /** Feign client 1 */
    @Resource
    private FeignClient1 feignClient1;
    /** Feign client 1 1 */
    @Resource
    private FeignClient1 feignClient1_1;
    /** Feign client 2 */
    @Resource
    private FeignClient2 feignClient2;
    /** Feign client 3 */
    @Resource
    private FeignClient3 feignClient3;
    /** Feign client 4 */
    @Resource
    private FeignClient4 feignClient4;
    /** Feign client 8 */
    @Resource
    private FeignClient8 feignClient8;

    /**
     * Test 1
     *
     * @since 1.0.0
     */
    @Test
    void test_1() {
        log.info("[{}]", this.feignClient1 == this.feignClient1_1);
    }
}



