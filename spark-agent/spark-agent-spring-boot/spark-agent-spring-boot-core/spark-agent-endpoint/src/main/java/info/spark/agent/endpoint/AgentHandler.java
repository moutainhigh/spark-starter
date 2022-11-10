package info.spark.agent.endpoint;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.08 11:00
 * @since 1.5.0
 */
@SuppressWarnings("checkstyle:ParameterNumber")
public interface AgentHandler {

    /**
     * Handle response entity
     *
     * @param api             API名称
     * @param version         API版本
     * @param appid           产品标识
     * @param timestamp       时间戳 毫秒
     * @param sign            签名值
     * @param nonce           请求唯一标识
     * @param signatureHeader signature header
     * @param charset         请求的编码方式
     * @param method          method
     * @param data            请求包
     * @return the response entity
     * @since 1.0.0
     */
    byte[] handle(String api,
                  String version,
                  String appid,
                  Long timestamp,
                  String sign,
                  String nonce,
                  String signatureHeader,
                  String charset,
                  @NotNull HttpMethod method,
                  byte[] data);
}
