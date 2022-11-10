package info.spark.agent.adapter.feign;

import com.alibaba.fastjson.JSONObject;
import info.spark.agent.adapter.AgentFeignAdapterTestApplication;
import info.spark.starter.util.Tools;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.20 13:20
 * @since 1.0.0
 */
@Slf4j
class ThirdClientTest extends AgentFeignAdapterTestApplication {
    /** Third client */
    @Resource
    private ThirdClient thirdClient;

    /**
     * Test 1
     *
     * @since 1.0.0
     */
    @Test
    void test_1() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("plateNo", "冀A-7239K");
        byte[] body = jsonObject.toJSONString().getBytes();

        Map<String, Object> headerMap = new HashMap<String, Object>(2) {
            private static final long serialVersionUID = 6140962548158964054L;

            {
                this.put("X-Agent-Api", "info.spark.center.third.agent.CarAgentService.getCarStatusByNo");
                this.put("X-Agent-Version", "1.0.0");
            }
        };

        String result = this.thirdClient.executeGet(headerMap, Base64.encodeBase64URLSafeString(body)).toString();
        log.info("{}", result);
    }

    /**
     * Test 2
     *
     * @since 1.0.0
     */
    @Test
    void test_2() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("plateNo", "冀A-7239K");
        jsonObject.put("startTime", "2019-12-29 09:00:00");
        jsonObject.put("endTime", "2019-12-30 08:00:00");
        byte[] body = jsonObject.toJSONString().getBytes();

        Map<String, Object> headerMap = new HashMap<String, Object>(2) {
            private static final long serialVersionUID = -4886842468802283444L;

            {
                this.put("X-Agent-Api", "info.spark.center.third.agent.CarAgentService.queryHistoryLocation");
                this.put("X-Agent-Version", "1.0.0");
            }
        };

        String result = this.thirdClient.executePost(headerMap, body).toString();
        log.info("{}", result);
    }

    /**
     * Test get
     *
     * @since 1.0.0
     */
    @Test
    void test_get() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("plateNo", "冀A-937N4");
        jsonObject.put("startTime", "2020-01-21 00:00:01");
        jsonObject.put("endTime", "2020-01-21 23:59:59");
        byte[] body = jsonObject.toJSONString().getBytes();

        Map<String, Object> headerMap = new HashMap<String, Object>(2) {
            private static final long serialVersionUID = -4886842468802283444L;

            {
                this.put("X-Agent-Api", "info.spark.center.third.agent.CarAgentService.queryHistoryLocation");
                this.put("X-Agent-Version", "1.0.0");
            }
        };

        String result = this.thirdClient.executeGet(headerMap, Base64.encodeBase64URLSafeString(body)).toString();
        log.info("{}", result);
    }

    /**
     * Test 3 *
     *
     * @throws InterruptedException interrupted exception
     * @since 1.0.0
     */
    @Test
    void test_3() throws InterruptedException {
        Tools.repeat(1000, () -> {
            new Thread(this::test_1).start();
        });

        Thread.currentThread().join();
    }

    /**
     * Test 4
     *
     * @since 1.0.0
     */
    @Test
    void test_4() {
        this.handlerGetLocationsByApi("冀A-937N4", "2020-01-21 00:00:01", "2020-01-21 23:59:59");
    }

    /**
     * Handler get locations by api *
     *
     * @param plateNo   plate no
     * @param beginTime begin time
     * @param endTime   end time
     * @since 1.0.0
     */
    private void handlerGetLocationsByApi(String plateNo, String beginTime, String endTime) {
        try {
            this.getLocation(plateNo, beginTime, endTime);
        } catch (Exception e) {
            log.error("getByApi error", e);
        }
    }

    /**
     * Gets location *
     *
     * @param plateNo   plate no
     * @param beginTime begin time
     * @param endTime   end time
     * @since 1.0.0
     */
    @SneakyThrows
    private void getLocation(String plateNo, String beginTime, String endTime) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("plateNo", plateNo);
        jsonObject.put("startTime", beginTime);
        jsonObject.put("endTime", endTime);
        byte[] body = jsonObject.toJSONString().getBytes();

        Map<String, Object> headerMap = new HashMap<String, Object>(2) {
            private static final long serialVersionUID = -4886842468802283444L;

            {
                this.put("X-Agent-Api", "info.spark.center.third.agent.CarAgentService.queryHistoryLocation");
                this.put("X-Agent-Version", "1.0.0");
            }
        };

        String result = this.thirdClient.executePost(headerMap, body).toString();
        log.debug("{}", result);
    }
}
