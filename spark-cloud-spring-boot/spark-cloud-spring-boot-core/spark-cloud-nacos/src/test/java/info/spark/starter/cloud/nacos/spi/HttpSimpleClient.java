package info.spark.starter.cloud.nacos.spi;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.impl.Limiter;
import com.alibaba.nacos.client.utils.ParamUtil;
import com.alibaba.nacos.common.constant.HttpHeaderConsts;
import com.alibaba.nacos.common.utils.IoUtils;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.alibaba.nacos.common.utils.UuidUtils;
import com.alibaba.nacos.common.utils.VersionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

/**
 * Http tool
 *
 * @author Nacos
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.13 18:32
 * @since 1.0.0
 */
public class HttpSimpleClient {

    /**
     * Http get
     *
     * @param url           url
     * @param headers       headers
     * @param paramValues   param values
     * @param encoding      encoding
     * @param readTimeoutMs read timeout ms
     * @param isSSL         is ssl
     * @return the http result
     * @throws IOException io exception
     * @since 1.5.0
     */
    @SneakyThrows
    static public HttpResult httpGet(String url, List<String> headers, List<String> paramValues,
                                     String encoding, long readTimeoutMs, boolean isSSL) throws IOException {
        String encodedContent = encodingParams(paramValues, encoding);
        url += (null == encodedContent) ? "" : ("?" + encodedContent);
        if (Limiter.isLimit(MD5Utils.md5Hex((url + encodedContent).getBytes(StandardCharsets.UTF_8)))) {
            return new HttpResult(NacosException.CLIENT_OVER_THRESHOLD,
                                  "More than client-side current limit threshold");
        }

        // SecurityManager sm = new SecurityManager();
        // System.setSecurityManager(sm);
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(Math.max(ParamUtil.getConnectTimeout(), 100));
            conn.setReadTimeout((int) readTimeoutMs);
            List<String> newHeaders = getHeaders(url, headers, paramValues);
            setHeaders(conn, newHeaders, encoding);

            conn.connect();

            int respCode = conn.getResponseCode();
            String resp;

            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = IoUtils.toString(conn.getInputStream(), encoding);
            } else {
                resp = IoUtils.toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, conn.getHeaderFields(), resp);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 发送GET请求.
     *
     * @param url           url
     * @param headers       headers
     * @param paramValues   param values
     * @param encoding      encoding
     * @param readTimeoutMs read timeout ms
     * @return the http result
     * @throws IOException io exception
     * @since 1.5.0
     */
    static public HttpResult httpGet(String url, List<String> headers, List<String> paramValues, String encoding,
                                     long readTimeoutMs) throws IOException {
        return httpGet(url, headers, paramValues, encoding, readTimeoutMs, false);
    }

    /**
     * 发送POST请求.
     *
     * @param url           url
     * @param headers       请求Header, 可以为null
     * @param paramValues   参数, 可以为null
     * @param encoding      URL编码使用的字符集
     * @param readTimeoutMs 响应超时
     * @param isSSL         是否https
     * @return http result
     * @throws IOException io exception
     * @since 1.5.0
     */
    @SneakyThrows
    static public HttpResult httpPost(String url, List<String> headers, List<String> paramValues,
                                      String encoding, long readTimeoutMs, boolean isSSL) throws IOException {
        String encodedContent = encodingParams(paramValues, encoding);
        encodedContent = (null == encodedContent) ? "" : encodedContent;
        if (Limiter.isLimit(MD5Utils.md5Hex((url + encodedContent).getBytes(StandardCharsets.UTF_8)))) {
            return new HttpResult(NacosException.CLIENT_OVER_THRESHOLD,
                                  "More than client-side current limit threshold");
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(Math.max(ParamUtil.getConnectTimeout(), 3000));
            conn.setReadTimeout((int) readTimeoutMs);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            List<String> newHeaders = getHeaders(url, headers, paramValues);
            setHeaders(conn, newHeaders, encoding);

            conn.getOutputStream().write(encodedContent.getBytes(encoding));

            int respCode = conn.getResponseCode();
            String resp;

            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = IoUtils.toString(conn.getInputStream(), encoding);
            } else {
                resp = IoUtils.toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, conn.getHeaderFields(), resp);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    /**
     * 发送POST请求.
     *
     * @param url           url
     * @param headers       请求Header, 可以为null
     * @param paramValues   参数, 可以为null
     * @param encoding      URL编码使用的字符集
     * @param readTimeoutMs 响应超时
     * @return http result
     * @throws IOException io exception
     * @since 1.5.0
     */
    static public HttpResult httpPost(String url, List<String> headers, List<String> paramValues, String encoding,
                                      long readTimeoutMs) throws IOException {
        return httpPost(url, headers, paramValues, encoding, readTimeoutMs, false);
    }

    /**
     * Http delete
     *
     * @param url           url
     * @param headers       headers
     * @param paramValues   param values
     * @param encoding      encoding
     * @param readTimeoutMs read timeout ms
     * @param isSSL         is ssl
     * @return the http result
     * @throws IOException io exception
     * @since 1.5.0
     */
    @SneakyThrows
    static public HttpResult httpDelete(String url, List<String> headers, List<String> paramValues,
                                        String encoding, long readTimeoutMs, boolean isSSL) throws IOException {
        String encodedContent = encodingParams(paramValues, encoding);
        url += (null == encodedContent) ? "" : ("?" + encodedContent);
        if (Limiter.isLimit(MD5Utils.md5Hex((url + encodedContent).getBytes(StandardCharsets.UTF_8)))) {
            return new HttpResult(NacosException.CLIENT_OVER_THRESHOLD,
                                  "More than client-side current limit threshold");
        }

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(Math.max(ParamUtil.getConnectTimeout(), 100));
            conn.setReadTimeout((int) readTimeoutMs);
            List<String> newHeaders = getHeaders(url, headers, paramValues);
            setHeaders(conn, newHeaders, encoding);

            conn.connect();

            int respCode = conn.getResponseCode();
            String resp;

            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = IoUtils.toString(conn.getInputStream(), encoding);
            } else {
                resp = IoUtils.toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, conn.getHeaderFields(), resp);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Http delete
     *
     * @param url           url
     * @param headers       headers
     * @param paramValues   param values
     * @param encoding      encoding
     * @param readTimeoutMs read timeout ms
     * @return the http result
     * @throws IOException io exception
     * @since 1.5.0
     */
    static public HttpResult httpDelete(String url, List<String> headers, List<String> paramValues, String encoding,
                                        long readTimeoutMs) throws IOException {
        return httpGet(url, headers, paramValues, encoding, readTimeoutMs, false);
    }

    /**
     * Sets headers *
     *
     * @param conn     conn
     * @param headers  headers
     * @param encoding encoding
     * @since 1.5.0
     */
    @SneakyThrows
    static private void setHeaders(HttpURLConnection conn, List<String> headers, String encoding) {
        if (null != headers) {
            for (Iterator<String> iter = headers.iterator(); iter.hasNext(); ) {
                conn.addRequestProperty(iter.next(), iter.next());
            }
        }
        conn.addRequestProperty(HttpHeaderConsts.CLIENT_VERSION_HEADER, VersionUtils.version);
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);

        String ts = String.valueOf(System.currentTimeMillis());
        String token = MD5Utils.md5Hex((ts + ParamUtil.getAppKey()).getBytes(StandardCharsets.UTF_8));
        conn.addRequestProperty(Constants.CLIENT_APPNAME_HEADER, ParamUtil.getAppName());
        conn.addRequestProperty(Constants.CLIENT_REQUEST_TS_HEADER, ts);
        conn.addRequestProperty(Constants.CLIENT_REQUEST_TOKEN_HEADER, token);
    }

    /**
     * Gets headers *
     *
     * @param url         url
     * @param headers     headers
     * @param paramValues param values
     * @return the headers
     * @throws IOException io exception
     * @since 1.5.0
     */
    private static List<String> getHeaders(String url, List<String> headers, List<String> paramValues)
        throws IOException {
        List<String> newHeaders = new ArrayList<>();
        newHeaders.add("exConfigInfo");
        newHeaders.add("true");
        newHeaders.add("RequestId");
        newHeaders.add(UuidUtils.generateUuid());
        if (headers != null) {
            newHeaders.addAll(headers);
        }
        return newHeaders;
    }

    /**
     * Encoding params
     *
     * @param paramValues param values
     * @param encoding    encoding
     * @return the string
     * @throws UnsupportedEncodingException unsupported encoding exception
     * @since 1.5.0
     */
    static private String encodingParams(List<String> paramValues, String encoding)
        throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == paramValues) {
            return null;
        }

        for (Iterator<String> iter = paramValues.iterator(); iter.hasNext(); ) {
            sb.append(iter.next()).append("=");
            sb.append(URLEncoder.encode(iter.next(), encoding));
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.13 18:32
     * @since 1.0.0
     */
    static public class HttpResult {
        /** Code */
        final public int code;
        /** Headers */
        final public Map<String, List<String>> headers;
        /** Content */
        final public String content;

        /**
         * Http result
         *
         * @param code    code
         * @param content content
         * @since 1.5.0
         */
        public HttpResult(int code, String content) {
            this.code = code;
            this.headers = null;
            this.content = content;
        }

        /**
         * Http result
         *
         * @param code    code
         * @param headers headers
         * @param content content
         * @since 1.5.0
         */
        public HttpResult(int code, Map<String, List<String>> headers, String content) {
            this.code = code;
            this.headers = headers;
            this.content = content;
        }
    }

}
