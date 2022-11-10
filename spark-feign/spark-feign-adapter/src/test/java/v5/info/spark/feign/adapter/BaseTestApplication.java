package v5.info.spark.feign.adapter;

import info.spark.feign.adapter.annotation.EnableFeignClients;
import info.spark.feign.adapter.config.FeignClientAdapterConfiguration;
import info.spark.starter.test.SparkTest;

import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: Spring Boot 项目 starter 模块测试写法:
 * 1. 使用 @EnableFeignClients 开启 @FeignClient 注解扫描;
 * 2. 使用 @TestPropertySource 指定测试的配置文件;
 * 3. 使用 @SpringBootTest 指定当前模块的配置类, 类似于 spring-application.xml;
 * </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.28 23:16
 * @since 1.0.0
 */
@Slf4j
@SparkTest(classes = FeignClientAdapterConfiguration.class)
@TestPropertySource("classpath:application.yml")
@EnableFeignClients("info.spark.feign.adapter.client")
public class BaseTestApplication {
}
