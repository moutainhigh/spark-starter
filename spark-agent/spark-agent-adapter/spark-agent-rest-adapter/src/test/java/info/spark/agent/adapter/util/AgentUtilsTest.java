package info.spark.agent.adapter.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.30 17:39
 * @since 2.0.0
 */
@Slf4j
class AgentUtilsTest {

    @Test
    void test_1() {
        byte[] body = "1000040922012007564".getBytes(StandardCharsets.UTF_8);
        String data1 = Base64.getUrlEncoder().encodeToString(body);
        String data2 = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(body);

        log.info("\n{}\n{}", data1, data2);
    }

}
