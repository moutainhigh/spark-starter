package sample.pay.service;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.util.ThreadUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import sample.pay.SamplePayApplicationTest;
import sample.pay.entity.form.CallbackForm;
import sample.pay.enums.PaymentStatus;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:35
 * @since 1.0.0
 */
@Slf4j
@AutoConfigureMockMvc
class PayServiceTest extends SamplePayApplicationTest {

    /** Pay service */
    @Resource
    public PayService payService;
    /** Mvc */
    @Resource
    private MockMvc mockMvc;

    /** 使用 serialPolicy 自定义序列化/反序列化 */
    @CreateCache(expire = 10000, cacheType = CacheType.REMOTE, name = "pay:callback:", serialPolicy = "bean:jacksonValueSerialPolicy")
    private Cache<String, CallbackForm> payCallbackCache;

    /**
     * Test pay
     *
     * @since 1.0.0
     */
    @Test
    void test_pay() {
        new Thread(() -> PayServiceTest.this.payService.pay()).start();

        ThreadUtils.sleep(3000);
        new Thread(this::post).start();

        ThreadUtils.join();
    }

    /**
     * Post
     *
     * @since 1.0.0
     */
    @SneakyThrows
    private void post() {
        // 模拟第三方接收到支付请求后, 把参数通过回调传给我们
        String transactionNo = StringUtils.getUid();

        CallbackForm form = this.payCallbackCache.get("test");
        form.setTransactionNo(transactionNo);
        form.setPaymentStatus(PaymentStatus.PAY);

        // 调用我们提供的回调接口
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/callback")
                                                       .content(JsonUtils.toJsonAsBytes(form))
                                                       .characterEncoding(Charsets.UTF_8_NAME)
                                                       .accept(MediaType.APPLICATION_JSON)
                                                       .contentType(MediaType.APPLICATION_JSON))
            // 添加断言
            .andExpect(MockMvcResultMatchers.status().isOk())
            // 添加执行
            .andDo(MockMvcResultHandlers.print())
            // 添加返回
            .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        log.info("response: {}", json);
        Result result = JsonUtils.parse(json, Result.class);

        Assertions.assertEquals(2000, result.getCode());
    }

}
