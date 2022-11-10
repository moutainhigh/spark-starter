package info.spark.agent.adapter;

import info.spark.agent.adapter.config.FeignClientAgentConfiguration;
import info.spark.feign.adapter.annotation.EnableFeignClients;
import info.spark.starter.test.SparkTest;

import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.16 17:37
 * @since 1.0.0
 */
@Slf4j
@TestPropertySource("classpath:application.yml")
@SparkTest(classes = FeignClientAgentConfiguration.class)
@EnableFeignClients
public class AgentFeignAdapterTestApplication {
}
