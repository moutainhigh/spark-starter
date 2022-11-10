package info.spark.feign.adapter.constant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import lombok.experimental.UtilityClass;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.16 14:22
 * @since 1.0.0
 */
@UtilityClass
public class FeignAdapter {
    /** CONTENT_TYPE_JSON */
    public static final String CONTENT_TYPE_JSON = HttpHeaders.CONTENT_TYPE + ": " + MediaType.APPLICATION_JSON_VALUE;
    /** CONTENT_TYPE_FORM_URLENCODE */
    public static final String CONTENT_TYPE_FORM_URLENCODE = HttpHeaders.CONTENT_TYPE + ": " + MediaType.APPLICATION_FORM_URLENCODED_VALUE;
    /** ACCEPT_TYPE */
    public static final String ACCEPT_TYPE = HttpHeaders.ACCEPT + ": " + MediaType.APPLICATION_JSON_VALUE;
    /** 注解协议, 默认 feign 原生 */
    public static final String AGREEMENT = "primordial";
    /** JAX-RS 规范 */
    public static final String JAXRS = "jaxrs";
    /** OKHTTP */
    public static final String OKHTTP = "OkHttpClient";
    /** PATH_SEPARATOR */
    public static final String PATH_SEPARATOR = "/";
    /** PROTOCOL_PREFIX */
    public static final String PROTOCOL_PREFIX = "://";
    /** HTTP_PREFIX */
    public static final String HTTP_PREFIX = "http";
    /** HTTPS_PREFIX */
    public static final String HTTPS_PREFIX = "https";
    /** HTTP_PROTOCOL */
    public static final String HTTP_PROTOCOL = HTTP_PREFIX + PROTOCOL_PREFIX;
    /** HTTPS_PROTOCOL */
    public static final String HTTPS_PROTOCOL = HTTPS_PREFIX + PROTOCOL_PREFIX;

}
