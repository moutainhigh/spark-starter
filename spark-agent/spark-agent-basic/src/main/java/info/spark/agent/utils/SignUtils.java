package info.spark.agent.utils;

import com.google.common.collect.Maps;

import info.spark.agent.constant.AgentConstant;
import info.spark.agent.constant.HttpConstant;
import info.spark.agent.constant.SdkConstant;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.basic.util.IoUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.basic.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 请求签名 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 10:03
 * @since 1.0.0
 */
@Slf4j
public abstract class SignUtils {

    /**
     * Sign
     *
     * @param method            method
     * @param secret            secret
     * @param headersParams     headers params
     * @param pathWithParameter path with parameter
     * @param data              data
     * @return the string
     * @since 1.6.0
     */
    public static @NotNull String sign(String method,
                                       String secret,
                                       Map<String, String> headersParams,
                                       String pathWithParameter,
                                       byte[] data) {
        try {
            // 将 Request 中的 httpMethod, headers, path, queryParam, formParam 合成一个字符串
            String signString = buildStringToSign(method, headersParams, pathWithParameter, null, null, false);
            // 使用 md5 将 secret 生成密钥, 然后使用 SM4 将入参, header 加密
            String signParams = SecureUtil.signParams(SmUtil.sm4(DigestUtil.md5(secret, Charsets.UTF_8_NAME)),
                                                      null,
                                                      StringPool.AMPERSAND,
                                                      StringPool.COLON,
                                                      true,
                                                      signString,
                                                      IoUtils.toString(data));
            // 得到 32 位摘要签名
            String sign = DigestUtils.md5DigestAsHex(signParams.getBytes(Charsets.UTF_8));

            log.debug("signString: \n{} \nsign: {}", signString, sign);
            return sign;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 Request 中的 httpMethod, headers, path, queryParam, formParam 合成一个字符串用 hmacSha256 算法双向加密进行签名
     *
     * @param secret            secret
     * @param headersParams     headers params
     * @param pathWithParameter path with parameter
     * @param queryParams       query params
     * @param formParam         form param
     * @return the string
     * @since 1.6.0
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static @NotNull String sign(String secret,
                                       Map<String, String> headersParams,
                                       String pathWithParameter,
                                       Map<String, String> queryParams,
                                       Map<Object, Object> formParam) {
        try {
            // 将 Request 中的 httpMethod, headers, path, queryParam, formParam 合成一个字符串
            String signString = buildStringToSign("", headersParams, pathWithParameter, queryParams, formParam, false);
            // 使用 md5 将 secret 生成密钥, 然后使用 SM4 将入参, header 加密
            String signParams = SecureUtil.signParams(SmUtil.sm4(DigestUtil.md5(secret, Charsets.UTF_8_NAME)),
                                                      formParam,
                                                      StringPool.AMPERSAND,
                                                      StringPool.COLON,
                                                      true,
                                                      signString);
            // 得到 32 位摘要签名
            String sign = DigestUtils.md5DigestAsHex(signParams.getBytes(Charsets.UTF_8));

            log.debug("signString: \n{} \nsign: {}", signString, sign);
            return sign;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 将 Request 中的httpMethod, headers, path, queryParam, formParam 合成一个字符串
     *
     * @param method            method
     * @param headerParams      header params
     * @param pathWithParameter path with parameter
     * @param queryParams       query params
     * @param formParams        form params
     * @param verify            verify
     * @return the string
     * @since 1.6.0
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static @NotNull String buildStringToSign(String method,
                                                    @NotNull Map<String, String> headerParams,
                                                    String pathWithParameter,
                                                    Map<String, String> queryParams,
                                                    Map<Object, Object> formParams,
                                                    boolean verify) {

        StringBuilder sb = new StringBuilder();
        sb.append(method).append(SdkConstant.CLOUDAPI_LF);

        // 将 headers 合成一个字符串
        if (verify) {
            sb.append(buildVerifyHeaders(headerParams));
        } else {
            sb.append(buildHeaders(headerParams));
        }

        // 将 path, queryParam, formParam 合成一个字符串
        sb.append(buildResource(pathWithParameter, queryParams, formParams));
        return sb.toString();
    }

    /**
     * 将 path, queryParam, formParam 合成一个字符串
     *
     * @param pathWithParameter path with parameter
     * @param queryParams       query params
     * @param formParams        form params
     * @return the string
     * @since 1.6.0
     */
    private static @NotNull String buildResource(String pathWithParameter,
                                                 Map<String, String> queryParams,
                                                 Map<Object, Object> formParams) {

        return pathWithParameter
               + "\n"
               + MapUtil.sortJoin(queryParams, StringPool.AMPERSAND, StringPool.COLON, true)
               + "\n"
               + MapUtil.sortJoin(formParams, StringPool.AMPERSAND, StringPool.COLON, true);
    }

    /**
     * 将 headers 合成一个字符串
     * 需要注意的是, HTTP头需要按照字母排序加入签名字符串
     * 同时所有加入签名的头的列表, 需要用逗号分隔形成一个字符串, 加入一个新HTTP头@ "X-Agent-Signature-Headers"
     *
     * @param headers headers
     * @return the string
     * @since 1.6.0
     */
    private static @NotNull String buildHeaders(Map<String, String> headers) {
        //使用 TreeMap, 默认按照字母排序
        Map<String, String> headersToSign = new TreeMap<>();

        if (headers != null) {
            StringBuilder signHeadersStringBuilder = new StringBuilder();

            int flag = 0;
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getKey().startsWith(SdkConstant.CLOUDAPI_CA_HEADER_TO_SIGN_PREFIX_SYSTEM)) {
                    if (flag != 0) {
                        signHeadersStringBuilder.append(",");
                    }
                    flag++;
                    signHeadersStringBuilder.append(header.getKey());
                    headersToSign.put(header.getKey(), header.getValue());
                }
            }

            // 同时所有加入签名的头的列表, 需要用逗号分隔形成一个字符串, 加入一个新HTTP头@"X-Agent-Signature-Headers"
            headers.put(SdkConstant.CLOUDAPI_X_AGENT_SIGNATURE_HEADERS, signHeadersStringBuilder.toString());
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : headersToSign.entrySet()) {
            sb.append(e.getKey()).append(':').append(e.getValue()).append(SdkConstant.CLOUDAPI_LF);
        }
        return sb.toString();
    }

    /**
     * Build verify headers
     *
     * @param headers headers
     * @return the string
     * @since 1.6.0
     */
    private static @NotNull String buildVerifyHeaders(@NotNull Map<String, String> headers) {
        String signHeads = headers.get(SdkConstant.CLOUDAPI_X_AGENT_SIGNATURE_HEADERS);
        if (StringUtils.isEmpty(signHeads)) {
            signHeads = SdkConstant.DEFAULT_CLOUDAPI_X_AGENT_SIGNATURE_HEADERS;
        }

        Map<String, String> headersToSign = new TreeMap<>();

        String[] signHeadArray = signHeads.split(SdkConstant.COMMA);

        for (String sh : signHeadArray) {
            if (headers.containsKey(sh.trim())) {
                headersToSign.put(sh.trim(), headers.get(sh.trim()));
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : headersToSign.entrySet()) {
            sb.append(e.getKey()).append(':').append(e.getValue()).append(SdkConstant.CLOUDAPI_LF);
        }
        return sb.toString();
    }

    /**
     * 从 http 请求里面提取用于签名的字段
     *
     * @param request request
     * @return map map
     * @since 1.6.0
     */
    public static @NotNull Map<String, String> extraHeaders(@NotNull HttpServletRequest request) {
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(18);
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_ACCEPT, request.getHeader(HttpConstant.CLOUDAPI_HTTP_HEADER_ACCEPT));
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_MD5, request.getHeader(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_MD5));
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE, request.getHeader(HttpConstant.CLOUDAPI_HTTP_HEADER_CONTENT_TYPE));
        headers.put(HttpConstant.CLOUDAPI_HTTP_HEADER_DATE, request.getHeader(HttpConstant.CLOUDAPI_HTTP_HEADER_DATE));

        headers.put(AgentConstant.X_AGENT_VERSION, request.getHeader(AgentConstant.X_AGENT_VERSION));
        headers.put(AgentConstant.X_AGENT_STAGE, request.getHeader(AgentConstant.X_AGENT_STAGE));
        headers.put(AgentConstant.X_AGENT_APPID, request.getHeader(AgentConstant.X_AGENT_APPID));
        headers.put(AgentConstant.X_AGENT_TIMESTAMP, request.getHeader(AgentConstant.X_AGENT_TIMESTAMP));
        headers.put(AgentConstant.X_AGENT_TOKEN, request.getHeader(AgentConstant.X_AGENT_TOKEN));
        headers.put(AgentConstant.X_AGENT_NONCE, request.getHeader(AgentConstant.X_AGENT_NONCE));
        headers.put(AgentConstant.X_AGENT_GROUP, request.getHeader(AgentConstant.X_AGENT_GROUP));
        headers.put(AgentConstant.X_AGENT_API, request.getHeader(AgentConstant.X_AGENT_API));
        headers.put(AgentConstant.X_AGENT_HOST, request.getHeader(AgentConstant.X_AGENT_HOST));
        headers.put(AgentConstant.X_AGENT_MOCK, request.getHeader(AgentConstant.X_AGENT_MOCK));
        headers.put(AgentConstant.X_AGENT_SIGNATURE, request.getHeader(AgentConstant.X_AGENT_SIGNATURE));
        headers.put(AgentConstant.X_AGENT_SIGNATURE_HEADERS, request.getHeader(AgentConstant.X_AGENT_SIGNATURE_HEADERS));
        headers.put(AgentConstant.X_AGENT_CHARSET, request.getHeader(AgentConstant.X_AGENT_CHARSET));
        return headers;
    }
}
