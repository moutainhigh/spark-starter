package info.spark.starter.openness.constant;

import lombok.experimental.UtilityClass;

/**
 * <p>Description:  </p>
 *
 * @author liujintao
 * @version 1.9.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.24 09:49
 * @since 1.6.0
 */
@UtilityClass
public final class Constant {

    /** REDIS_KEY_NONCE */
    public static final String REDIS_KEY_NONCE_PREFIX = "openness-client-key:nonce:";
    /** REDIS_KEY_CLIENT */
    public static final String REDIS_KEY_CLIENT_PREFIX = "openness-client-key:client:";

    /** base64 ( 加密 (HTTP-Method + X-FAPI-MD5 +Content-Type + time + 资源url) ) */
    public static final String HEADER_SIGN = "X-Fapi-Sign";
    /** AccessId，用于获取 secretKey */
    public static final String HEADER_CLIENT_ID = "X-Fapi-ClientId";
    /** NONCE: 随机数,用于open API 放重放 */
    public static final String HEADER_NONCE = "X-Fapi-Nonce";
    /** TIMESTAMP: 客户端发起请求的时间 时间戳 */
    public static final String HEADER_TIMESTAMP = "X-Fapi-Timestamp";

}
