package info.spark.agent.adapter.feign;

import com.alibaba.fastjson.JSONObject;
import info.spark.agent.adapter.AgentFeignAdapterTestApplication;
import info.spark.agent.adapter.feign.demo.DemoClient;
import info.spark.starter.util.Tools;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 05:17
 * @since 1.0.0
 */
@Slf4j
class DemoClientTest extends AgentFeignAdapterTestApplication {
    /** Demo client */
    @Resource
    private DemoClient demoClient;

    /**
     * Execute get  @see  info.spark.exmaple.agent.service.TestParamsService
     *
     * @since 1.0.0
     */
    @Test
    void executeGet() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "dong4j");
        // jsonObject.put("date", "2020-01-02 15:17:31");
        // 时间传字符和 Date 都可以, 字符需要使用特定格式 (由 Jackson 反序列化)
        jsonObject.put("date", new Date());

        byte[] body = jsonObject.toJSONString().getBytes();

        Map<String, Object> headerMap = new HashMap<String, Object>(2) {
            private static final long serialVersionUID = 6140962548158964054L;

            {
                this.put("X-Agent-Api", "info.spark.exmaple.agent.service.TestParamsService");
                this.put("X-Agent-Version", "1.0.0");
            }
        };

        log.info("{}", this.demoClient.executeGet(headerMap, Base64.encodeBase64URLSafeString(body)));
    }

    /**
     * Execute post
     *
     * @since 1.0.0
     */
    @Test
    void executePost() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "dong4j");
        jsonObject.put("date", "2020-01-02 15:17:31");
        byte[] body = jsonObject.toJSONString().getBytes();

        Map<String, Object> headerMap = new HashMap<String, Object>(2) {
            private static final long serialVersionUID = -4886842468802283444L;

            {
                this.put("X-Agent-Api", "info.spark.exmaple.agent.service.TestParamsService");
                this.put("X-Agent-Version", "1.0.0");
            }
        };

        log.info("{}", this.demoClient.executePost(headerMap, body));
    }

    /**
     * Test thread *
     *
     * @throws InterruptedException interrupted exception
     * @since 1.0.0
     */
    @Test
    void test_thread() throws InterruptedException {
        Tools.repeat(1000, () -> {new Thread(this::executeGet).start();});
        Thread.currentThread().join();
    }

}
