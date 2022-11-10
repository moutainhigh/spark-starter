package info.spark.starter.openness.constant;

import info.spark.starter.basic.constant.BasicConstant;

/**
 * <p>Description:  </p>
 *
 * @author liujintao
 * @version 1.6.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.24 09:49
 * @since 1.6.0
 */
public class Constant {
    /** PARAM_CLIENT_ID */
    public static final String HEADER_CLIENT_ID = BasicConstant.HEADER_CLIENT_ID;
    /** HEADER_SIGN */
    public static final String HEADER_SIGN = "X-Client-Sign";
    /** SIGN */
    public static final String PARAM_SIGN = "sign";
    /** TIMESTAMP: 客户端发起请求的时间 */
    public static final String HEADER_TIMESTAMP = "timestamp";
    /** NONCE: 随机数,用于open API 放重放 */
    public static final String HEADER_NONCE = "nonce";

}
