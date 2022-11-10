package info.spark.starter.mq;

import info.spark.starter.test.SparkTest;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.23 11:29
 * @since 1.5.0
 */
@Slf4j
@SparkTest(classes = KafkaInitialConfiguration.class)
class KafkaInitialConfigurationTest {
    /** Admin client */
    @Resource
    private AdminClient adminClient;

    /**
     * Test select topic info
     *
     * @throws ExecutionException   execution exception
     * @throws InterruptedException interrupted exception
     * @since 1.5.0
     */
    @Test
    public void testSelectTopicInfo() throws ExecutionException, InterruptedException {
        DescribeTopicsResult result = this.adminClient.describeTopics(Collections.singletonList("topic.quick.initial"));
        result.all().get().forEach((k, v) -> log.info("k: {} v: {}", k, v.toString()));
    }

}
