package info.spark.agent.cases;

import info.spark.agent.adapter.annotation.EnableAgentClient;
import info.spark.agent.adapter.config.AgentAdapterRestConfiguration;
import info.spark.starter.test.SparkTest;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.17 11:36
 * @since 1.7.1
 */
@Slf4j
@EnableAgentClient("info.spark.agent.client")
@SparkTest(classes = AgentAdapterRestConfiguration.class)
class AgentClientsRegistrarTest {

    @Test
    void test_() {
        log.info("测试 AgentClient 注册逻辑");
    }

}
