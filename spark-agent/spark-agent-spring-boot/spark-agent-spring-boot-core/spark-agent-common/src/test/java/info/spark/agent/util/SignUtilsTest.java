package info.spark.agent.util;

import com.google.common.collect.Maps;

import info.spark.agent.constant.AgentConstant;
import info.spark.agent.constant.HttpConstant;
import info.spark.agent.constant.SdkConstant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.18 20:39
 * @since 1.6.0
 */
class SignUtilsTest {
    /**
     * 签名
     *
     * @since 1.6.0
     */
    @Test
    void signTestWF() {
        String appid = "DI7Z72CF";
        String appSecrect = "X56SREQC24%R524XX56SREQC24%R524X";

        String method = "POST";

        Map<String, String> headers = Maps.newHashMapWithExpectedSize(16);

        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_HOST, "api.xxx.com");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_DATE, "Tue, 31 Oct 2017 08:14:45 GMT");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE, "application/json");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_ACCEPT, "application/json");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_MD5, "d0e6de25109f62ff250d566ac473fc19");

        headers.put(AgentConstant.X_AGENT_API, "");
        headers.put(AgentConstant.X_AGENT_APPID, appid);
        headers.put(AgentConstant.X_AGENT_GROUP, "");
        headers.put(AgentConstant.X_AGENT_HOST, "");
        headers.put(AgentConstant.X_AGENT_MOCK, "");
        headers.put(AgentConstant.X_AGENT_NONCE, "e807f1fcf82d132f9bb018ca6738a19f");
        headers.put(AgentConstant.X_AGENT_STAGE, "release");
        headers.put(AgentConstant.X_AGENT_TIMESTAMP, "1509438317112");
        headers.put(AgentConstant.X_AGENT_VERSION, "1");

        String path = "/gateway/pay";

        String result = "480265ce3321b038360cad6d69402a59";

        String signResult = SignUtils.sign(appSecrect, headers, path, null, null);

        Assertions.assertEquals(signResult, result);
    }

    /**
     * 签名验证
     *
     * @since 1.6.0
     */
    @Test
    void verifyTestWF() {
        String appid = "DI7Z72CF";
        String appSecrect = "X56SREQC24%R524XX56SREQC24%R524X";

        String method = "POST";

        String path = "/gateway/pay";

        Map<String, String> headers = Maps.newHashMapWithExpectedSize(16);

        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_HOST, "api.xxx.com");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_DATE, "Tue, 31 Oct 2017 08:14:45 GMT");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE, "application/json");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_ACCEPT, "application/json");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_MD5, "d0e6de25109f62ff250d566ac473fc19");

        headers.put(AgentConstant.X_AGENT_API, "");
        headers.put(AgentConstant.X_AGENT_APPID, appid);
        headers.put(AgentConstant.X_AGENT_GROUP, "");
        headers.put(AgentConstant.X_AGENT_HOST, "");
        headers.put(AgentConstant.X_AGENT_MOCK, "");
        headers.put(AgentConstant.X_AGENT_NONCE, "e807f1fcf82d132f9bb018ca6738a19f");
        headers.put(AgentConstant.X_AGENT_STAGE, "release");
        headers.put(AgentConstant.X_AGENT_TIMESTAMP, "1509438317112");
        headers.put(AgentConstant.X_AGENT_VERSION, "1");

        headers.put(SdkConstant.CLOUDAPI_X_AGENT_SIGNATURE, "480265ce3321b038360cad6d69402a59");

        boolean verifyResult = SignUtils.verify(appSecrect, headers, path, null, null);

        Assertions.assertTrue(verifyResult);
    }

    /**
     * Sign test
     *
     * @since 1.6.0
     */
    @Test
    void signTest() {
        String appid = "DI7Z72CF";
        String appSecrect = "X56SREQC24%R524XX56SREQC24%R524X";

        String method = "POST";

        Map<String, String> headers = Maps.newHashMapWithExpectedSize(14);

        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_HOST, "api.xxx.com");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_DATE, "Tue, 31 Oct 2017 08:14:45 GMT");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE, "text/plain; charset=UTF-8");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_ACCEPT, "application/json");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_MD5, "d0e6de25109f62ff250d566ac473fc19");

        headers.put(AgentConstant.X_AGENT_API, "");
        headers.put(AgentConstant.X_AGENT_APPID, appid);
        headers.put(AgentConstant.X_AGENT_GROUP, "");
        headers.put(AgentConstant.X_AGENT_HOST, "");
        headers.put(AgentConstant.X_AGENT_MOCK, "");
        headers.put(AgentConstant.X_AGENT_NONCE, "e807f1fcf82d132f9bb018ca6738a19f");
        headers.put(AgentConstant.X_AGENT_STAGE, "release");
        headers.put(AgentConstant.X_AGENT_TIMESTAMP, "1509438317112");
        headers.put(AgentConstant.X_AGENT_VERSION, "1");

        String path = "/gateway/pay";

        String result = "fb14fd4c69ec789d9fd6b764d736a191";

        String signResult = SignUtils.sign(appSecrect, headers, path, null, null);

        Assertions.assertEquals(signResult, result);
    }

    /**
     * Verify test
     *
     * @since 1.6.0
     */
    @Test
    void verifyTest() {
        String appid = "DI7Z72CF";
        String appSecrect = "X56SREQC24%R524XX56SREQC24%R524X";

        String method = "POST";

        String path = "/gateway/pay";

        Map<String, String> headers = Maps.newHashMapWithExpectedSize(16);

        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_HOST, "api.xxx.com");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_DATE, "Tue, 31 Oct 2017 08:14:45 GMT");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE, "text/plain; charset=UTF-8");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_ACCEPT, "application/json");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_MD5, "d0e6de25109f62ff250d566ac473fc19");

        headers.put(AgentConstant.X_AGENT_API, "");
        headers.put(AgentConstant.X_AGENT_APPID, appid);
        headers.put(AgentConstant.X_AGENT_GROUP, "");
        headers.put(AgentConstant.X_AGENT_HOST, "");
        headers.put(AgentConstant.X_AGENT_MOCK, "");
        headers.put(AgentConstant.X_AGENT_NONCE, "e807f1fcf82d132f9bb018ca6738a19f");
        headers.put(AgentConstant.X_AGENT_STAGE, "release");
        headers.put(AgentConstant.X_AGENT_TIMESTAMP, "1509438317112");
        headers.put(AgentConstant.X_AGENT_VERSION, "1");

        headers.put(SdkConstant.CLOUDAPI_X_AGENT_SIGNATURE, "fb14fd4c69ec789d9fd6b764d736a191");

        boolean verifyResult = SignUtils.verify(appSecrect, headers, path, null, null);

        Assertions.assertTrue(verifyResult);
    }

    /**
     * Sign test 2
     *
     * @since 1.6.0
     */
    @Test
    void signTest2() {
        String appid = "DI7Z72CF";
        String appSecrect = "X56SREQC24%R524XX56SREQC24%R524X";

        String method = "POST";

        Map<String, String> headers = Maps.newHashMapWithExpectedSize(10);

        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_HOST, "api.xxx.com");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_DATE, "Tue, 31 Oct 2017 08:14:45 GMT");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE, "text/plain; charset=UTF-8");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_ACCEPT, "application/json");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_MD5, "d0e6de25109f62ff250d566ac473fc19");

        headers.put(AgentConstant.X_AGENT_APPID, appid);
        headers.put(AgentConstant.X_AGENT_NONCE, "e807f1fcf82d132f9bb018ca6738a19f");
        headers.put(AgentConstant.X_AGENT_STAGE, "release");
        headers.put(AgentConstant.X_AGENT_TIMESTAMP, "1509438317112");
        headers.put(AgentConstant.X_AGENT_VERSION, "1");

        String path = "/gateway/pay";

        String result = "6760059b79bb7985f31e4077deab325a";

        String signResult = SignUtils.sign(appSecrect, headers, path, null, null);

        Assertions.assertEquals(signResult, result);
    }

    /**
     * Verify test 2
     *
     * @since 1.6.0
     */
    @Test
    void verifyTest2() {
        String appid = "DI7Z72CF";
        String appSecrect = "X56SREQC24%R524XX56SREQC24%R524X";

        String method = "POST";

        String path = "/gateway/pay";

        Map<String, String> headers = Maps.newHashMapWithExpectedSize(12);

        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_HOST, "api.xxx.com");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_DATE, "Tue, 31 Oct 2017 08:14:45 GMT");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE, "text/plain; charset=UTF-8");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_ACCEPT, "application/json");
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_MD5, "d0e6de25109f62ff250d566ac473fc19");

        headers.put(AgentConstant.X_AGENT_APPID, appid);
        headers.put(AgentConstant.X_AGENT_NONCE, "e807f1fcf82d132f9bb018ca6738a19f");
        headers.put(AgentConstant.X_AGENT_STAGE, "release");
        headers.put(AgentConstant.X_AGENT_TIMESTAMP, "1509438317112");
        headers.put(AgentConstant.X_AGENT_VERSION, "1");

        headers.put(SdkConstant.CLOUDAPI_X_AGENT_SIGNATURE_HEADERS, "X-Agent-Appid,X-Agent-Nonce,X-Agent-Stage,X-Agent-Timestamp," +
                                                                    "X-Agent-Version");

        headers.put(SdkConstant.CLOUDAPI_X_AGENT_SIGNATURE, "6760059b79bb7985f31e4077deab325a");

        boolean verifyResult = SignUtils.verify(appSecrect, headers, path, null, null);

        Assertions.assertTrue(verifyResult);
    }
}


