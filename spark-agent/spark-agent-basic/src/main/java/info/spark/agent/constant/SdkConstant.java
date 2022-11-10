package info.spark.agent.constant;

import info.spark.starter.basic.util.StringPool;

import lombok.experimental.UtilityClass;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.18 20:25
 * @since 1.6.0
 */
@UtilityClass
public class SdkConstant {
    /** 签名 Header */
    public static final String CLOUDAPI_X_AGENT_SIGNATURE = "X-Agent-Signature";
    /** 所有参与签名的Header */
    public static final String CLOUDAPI_X_AGENT_SIGNATURE_HEADERS = "X-Agent-Signature-Headers";
    /** CLOUDAPI_LF */
    public static final String CLOUDAPI_LF = StringPool.NEWLINE;
    /** 参与签名的系统Header前缀,只有指定前缀的Header才会参与到签名中 */
    public static final String CLOUDAPI_CA_HEADER_TO_SIGN_PREFIX_SYSTEM = "X-Agent-";
    /** COMMA */
    public static final String COMMA = StringPool.COMMA;
    /** header 中参与签名的字段 */
    public static final String DEFAULT_CLOUDAPI_X_AGENT_SIGNATURE_HEADERS =
        "X-Agent-Version,"
        + "X-Agent-Appid,"
        + "X-Agent-Api,"
        + "X-Agent-Stage,"
        + "X-Agent-Timestamp,"
        + "X-Agent-Nonce,"
        + "X-Agent-Group,"
        + "X-Agent-Host,"
        + "X-Agent-Mock";
}
