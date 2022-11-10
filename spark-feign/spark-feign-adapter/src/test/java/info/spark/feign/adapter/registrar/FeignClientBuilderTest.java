package info.spark.feign.adapter.registrar;


import info.spark.feign.adapter.client.FeignClient7;
import info.spark.starter.test.SparkTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.30 13:54
 * @since 1.0.0
 */
@Slf4j
@SparkTest
@TestPropertySource("classpath:application.yml")
@ContextConfiguration(locations = {"classpath:spark-starter-feign-adapter.xml"})
class FeignClientBuilderTest {
    /** Application context */
    @Resource
    private ApplicationContext applicationContext;

    /**
     * 使用 builder 创建的实例不是单例的
     *
     * @since 1.0.0
     */
    @Test
    void test_1() {
        FeignClientBuilder builder = new FeignClientBuilder(this.applicationContext);
        FeignClient7 feignClient71 = builder.forType(FeignClient7.class, "").build();
        FeignClient7 feignClient72 = builder.forType(FeignClient7.class, "").build();
        log.info("[{}]", feignClient71);
        Assertions.assertNotSame(feignClient71, feignClient72);
    }
}
