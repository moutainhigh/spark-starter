package info.spark.agent.constant;

import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.basic.constant.ConfigKey;

import lombok.experimental.UtilityClass;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 20:24
 * @since 1.0.0
 */
@UtilityClass
public class AgentConstant {
    /** ROOT_ENDPOINT */
    public static final String ROOT_ENDPOINT = ConfigKey.AgentConfigKey.AGENT_SUFFIX;
    /** X_AGENT_API */
    public static final String X_AGENT_API = "X-Agent-Api";
    /** X_AGENT_VERSION */
    public static final String X_AGENT_VERSION = "X-Agent-Version";
    /** X_AGENT_STAGE */
    public static final String X_AGENT_STAGE = "X-Agent-Stage";
    /** X_AGENT_APPID */
    public static final String X_AGENT_APPID = "X-Agent-Appid";

    /**
     * 由业务端放入 header
     *
     * @since 1.8.0
     */
    public static final String X_AGENT_TENANTID = "X-Agent-Tenantid";
    /** X_AGENT_SDK */
    public static final String X_AGENT_SDK = "X-Agent-SDK";
    /** X_AGENT_APPNAME */
    public static final String X_AGENT_APPNAME = "X-Agent-Appname";
    /** X_AGENT_TIMESTAMP */
    public static final String X_AGENT_TIMESTAMP = "X-Agent-Timestamp";
    /** X_AGENT_TIMEOUT */
    public static final String X_AGENT_TIMEOUT = "X-Agent-Timeout";
    /** X_AGENT_TOKEN */
    public static final String X_AGENT_TOKEN = "X-Agent-Token";
    /** 请求放重放Nonce,15分钟内保持唯一,建议使用UUID */
    public static final String X_AGENT_NONCE = "X-Agent-Nonce";
    /** X_AGENT_GROUP */
    public static final String X_AGENT_GROUP = "X-Agent-Group";
    /** X_AGENT_HOST */
    public static final String X_AGENT_HOST = "X-Agent-Host";
    /** X_AGENT_MOCK */
    public static final String X_AGENT_MOCK = "X-Agent-Mock";
    /** 使用 endpoint 标识 */
    public static final String X_AGENT_ENDPOINT = "X-Agent-Endpoint";
    /** X_AGENT_SIGNATURE */
    public static final String X_AGENT_SIGNATURE = "X-Agent-Signature";
    /** X_AGENT_SIGNATURE_TYPE */
    public static final String X_AGENT_SIGNATURE_TYPE = "X-Agent-Signature-Type";
    /** 当前请求是否需要签名, 客户端写入, 当拦截器处理完成后删除 */
    public static final String X_AGENT_NEED_SIGNATURE = "X-Agent-Need-Signature";
    /** X_AGENT_SIGNATURE_HEADERS */
    public static final String X_AGENT_SIGNATURE_HEADERS = "X-Agent-Signature-Headers";
    /** X_AGENT_CHARSET */
    public static final String X_AGENT_CHARSET = "X-Agent-Charset";
    /** X_AGENT_REQUEST_ID */
    public static final String X_AGENT_REQUEST_ID = "X-Agent-Request-Id";
    /** TMP_METHOD_SERVICE */
    public static final String TMP_METHOD_SERVICE = "service";
    /** TMP_METHOD_HANDLER */
    public static final String TMP_METHOD_HANDLER = "handler";
    /** get 请求 url data 参数名 */
    public static final String GET_PARAM_NAME = "data";
    /** DATA_VALUE */
    public static final String DATA_VALUE = BasicConstant.RESULT_WRAPPER_VALUE_KEY;
    /** DATA_TYPE */
    public static final String DATA_TYPE = "type";
    /** REPLY_CACHE_NAME */
    public static final String REPLY_CACHE_NAME = "REPLY_CACHE_KEY";
    /** DEFAULT_API_VERSION */
    public static final String DEFAULT_API_VERSION = "1.0.0";
    /** CLIENT_METHOD_API */
    public static final String CLIENT_METHOD_API = "api";
    /** 直接使用 request 接口 (info.spark.agent.adapter.registrar.AgentClientProxy#invoke) */
    public static final String CLIENT_METHOD_REQUEST = "request";
    /** CLIENT_METHOD_SETENDPOINT */
    public static final String CLIENT_METHOD_SET_ENDPOINT = "setEndpoint";
    /** CLIENT_METHOD_GET_ENDPOINT */
    public static final String CLIENT_METHOD_GET_ENDPOINT = "getEndpoint";
    /** CLIENT_METHOD_SET_TENANTID */
    public static final String CLIENT_METHOD_SET_TENANTID = "setTenantId";
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
    /** DEFAULT_VERSION */
    public static final String DEFAULT_VERSION = "1.0.0";
    /** 日志路由 value */
    public static final String AGENT_SERVICE = "AGENT_SERVICE";
    /** EXECUTE_AGENT_SERVICE_ID */
    public static final String EXECUTE_AGENT_SERVICE_ID = "executeAgentServiceId";
    /** 配置文件中的前缀 */
    public static final String PREPERTIES_PREFIX = "spark.gateway.endpoint.";
}
