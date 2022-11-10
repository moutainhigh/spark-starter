package v4.info.spark.feign.adapter;

import info.spark.feign.adapter.annotation.EnableFeignClients;
import info.spark.starter.test.SparkTest;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 非 Spring Boot 项目 starter 模块测试写法:
 * 1. 使用 @EnableFeignClients 开启 @FeignClient 注解扫描;
 * 2. 使用 @TestPropertySource 指定测试的配置文件;
 * 3. 使用 @ContextConfiguration 加载 spring 容器, 这里直接使用配置文件, 也可使用 FeignClientAdapterConfiguration.class,
 * 2 者是等价的 (@Import(value = FeignClientAdapterConfiguration.class));
 * </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.29 16:43
 * @since 1.0.0
 */
@Slf4j
@SparkTest
@EnableFeignClients("info.spark.feign.adapter.client")
@TestPropertySource("classpath:application.yml")
@ContextConfiguration(locations = {"classpath:spark-starter-feign-adapter.xml"})
public class BaseTestApplication {
}
