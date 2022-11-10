package info.spark.agent.adapter.util;

import info.spark.agent.constant.AgentConstant;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.StringUtils;

import java.net.URL;
import java.util.Base64;
import java.util.Objects;

import cn.hutool.core.util.ReUtil;
import lombok.experimental.UtilityClass;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.24 18:46
 * @since 1.7.1
 */
@UtilityClass
public class AgentUtils {
    /** formatter:off 域名 (http://domain 或者 domain 或者 domain:port) */
    @SuppressWarnings("checkstyle:LineLength")
    private static final String DOMAIN = "^(?=^.{3,255}$)(http(s)?:\\/\\/)?(www\\.)?[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+(:\\d+)*(\\/\\w+\\.\\w+)*$";
    /** ip+port */
    @SuppressWarnings("checkstyle:LineLength")
    private static final String IP_PORT = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5]):([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$";

    /**
     * Gets data length *
     *
     * @param object object
     * @return the data length
     * @since 1.7.1
     */
    public Integer getDataLength(Object object) {
        String data = object2String(object);
        return data.length();
    }

    /**
     * Object 2 string
     *
     * @param params params
     * @return the string
     * @since 1.7.1
     */
    public String object2String(Object params) {
        byte[] body = JsonUtils.toJsonAsBytes(JsonUtils.EMPTY_ARRAY);
        if (params != null) {
            body = JsonUtils.toJsonAsBytes(params);
        }

        String data = Base64.getUrlEncoder().encodeToString(body);
        data = data.replace("\n", "");
        data = data.replace("\r", "");

        return data;
    }

    /**
     * Add protocol
     *
     * @param endpoint endpoint
     * @return the string
     * @since 1.7.1
     */
    public String addProtocol(String endpoint) {
        if (StringUtils.isBlank(endpoint)) {
            return endpoint;
        }
        if (!endpoint.contains(AgentConstant.PROTOCOL_PREFIX)) {
            endpoint = AgentConstant.HTTP_PROTOCOL + endpoint;
        }

        return endpoint;
    }

    /**
     * 检查 endpoint 格式是否正确
     *
     * @param endpoint endpoint
     * @return the string
     * @since 1.7.1
     */
    public String checkEndpointPattern(String endpoint) {
        if (StringUtils.isBlank(endpoint)) {
            return endpoint;
        }
        endpoint = AgentUtils.addProtocol(endpoint);
        String finalEndpoint = endpoint;
        // url 非法将直接抛出异常
        URL url = Assertions.wrapper(() -> new URL(finalEndpoint), "不是合法的 endpoint: [" + finalEndpoint + "]");
        String authority = Objects.requireNonNull(url).getAuthority();
        // 验证是否为域名或 ip:port 的格式
        Assertions.isTrue(ReUtil.isMatch(DOMAIN, authority) || ReUtil.isMatch(IP_PORT, authority),
                          "endpoint 验证失败, 不是有效的 endpoint: [" + authority + "]");
        return endpoint;
    }
}
