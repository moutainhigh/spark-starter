package cache.starter;

import info.spark.starter.cache.service.CacheService;
import info.spark.starter.test.SparkTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Resource;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.24 20:08
 * @since 1.7.0
 */
@SparkTest(classes = CacheAutoConfigurationTest.class)
class CacheServiceTest {
    @Resource
    private CacheService cacheService;

    @Test
    @Order(21)
    void test_hset_hget() {

        String key = "currentUser:dong4j";
        String field1 = "payment_admin";
        String field2 = "tenant_admin";

        Map<String, Long> map = new HashMap<>(2);
        map.put("id", 1L);

        Assertions.assertTrue(this.cacheService.hset(key, field1, map));

        Assertions.assertEquals(2, this.cacheService.hlen(key));

        Assertions.assertTrue(this.cacheService.hset(key, field2, map));

        Assertions.assertIterableEquals(new HashSet<>(Arrays.asList("payment_admin", "tenant_admin")),
                                        this.cacheService.hkeys("currentUser:dong4j"));

    }
}

