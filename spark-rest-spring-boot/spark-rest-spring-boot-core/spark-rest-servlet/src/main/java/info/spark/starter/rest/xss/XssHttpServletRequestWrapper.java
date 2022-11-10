package info.spark.starter.rest.xss;

import com.google.common.collect.Sets;

import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.util.StringUtils;
import info.spark.starter.core.util.WebUtils;
import info.spark.starter.rest.support.CacheRequestEnhanceWrapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletInputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: xss 过滤处理, 在 cache request 的基础上对 xss 进行处理 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.11 17:00
 * @since 1.0.0
 */
@Slf4j
public class XssHttpServletRequestWrapper extends CacheRequestEnhanceWrapper {
    /** html过滤 */
    private static final HtmlFilter HTML_FILTER = new HtmlFilter();

    /** SQL_KEY */
    private static final String SQL_KEY = "and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+";
    /** NOT_ALLOWED_KEY_WORDS */
    private static final Set<String> NOT_ALLOWED_KEY_WORDS = Sets.newHashSet();
    /** REPLACED_STRING */
    private static final String REPLACED_STRING = "INVALID";

    static {
        String[] keyStr = SQL_KEY.split("\\|");
        NOT_ALLOWED_KEY_WORDS.addAll(Arrays.asList(keyStr));
    }

    /**
     * Instantiates a new Xss http servlet request wrapper.
     *
     * @param request the request
     * @since 1.0.0
     */
    XssHttpServletRequestWrapper(ContentCachingRequestWrapper request) {
        super(request);
    }

    /**
     * Gets input stream *
     *
     * @return the input stream
     * @throws IOException io exception
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("checkstyle:ReturnCount")
    public ServletInputStream getInputStream() {

        // 为空,直接返回
        if (null == super.getHeader(HttpHeaders.CONTENT_TYPE)) {
            return super.getInputStream();
        }

        // 非 json 类型,直接返回
        if (!BasicConstant.JSON.equals(MediaType.valueOf(super.getHeader(HttpHeaders.CONTENT_TYPE)).getSubtype())) {
            return super.getInputStream();
        }

        // 为空,直接返回
        String requestStr = WebUtils.getRequestStr(this.cachingRequestWrapper, this.body);
        if (StringUtils.isBlank(requestStr)) {
            return super.getInputStream();
        }

        requestStr = xssEncode(requestStr);

        return WebUtils.getCacheInputStream(requestStr.getBytes(Charsets.UTF_8));
    }

    /**
     * Gets header *
     *
     * @param name name
     * @return the header
     * @since 1.0.0
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(xssEncode(name));
        if (StringUtils.isNotBlank(value)) {
            value = xssEncode(value);
        }
        return value;
    }

    /**
     * Gets parameter *
     *
     * @param name name
     * @return the parameter
     * @since 1.0.0
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(xssEncode(name));
        if (StringUtils.isNotBlank(value)) {
            value = xssEncode(value);
        }
        return value;
    }

    /**
     * Get parameter values string [ ]
     *
     * @param name name
     * @return the string [ ]
     * @since 1.0.0
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] parameters = super.getParameterValues(name);
        if (parameters == null || parameters.length == 0) {
            return null;
        }

        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = xssEncode(parameters[i]);
        }
        return parameters;
    }

    /**
     * Gets parameter map *
     *
     * @return the parameter map
     * @since 1.0.0
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new LinkedHashMap<>();
        Map<String, String[]> parameters = super.getParameterMap();
        for (String key : parameters.keySet()) {
            String[] values = parameters.get(key);
            for (int i = 0; i < values.length; i++) {
                values[i] = xssEncode(values[i]);
            }
            map.put(key, values);
        }
        return map;
    }

    /**
     * Xss encode string
     *
     * @param input input
     * @return the string
     * @since 1.0.0
     */
    private static String xssEncode(String input) {
        return HTML_FILTER.filter(cleanSqlKeyWords(input));
    }

    /**
     * sql 注入过滤
     *
     * @param value value
     * @return the string
     * @since 1.0.0
     */
    private static String cleanSqlKeyWords(String value) {
        String paramValue = value;

        for (String keyword : NOT_ALLOWED_KEY_WORDS) {
            boolean lengthMatched = paramValue.length() > keyword.length() + 4;
            boolean containsMatched = paramValue.contains(" " + keyword)
                                      || paramValue.contains(keyword + " ")
                                      || paramValue.contains(" " + keyword + " ");

            if (lengthMatched && containsMatched) {
                paramValue = StringUtils.replace(paramValue, keyword, REPLACED_STRING);
            }
        }
        return paramValue;
    }

}
