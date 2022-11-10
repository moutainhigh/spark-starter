package info.spark.starter.captcha.constant;

import lombok.experimental.UtilityClass;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 21:43
 * @since 1.0.0
 */
@UtilityClass
public class CaptchaConstant {
    /** 验证码 key */
    public static final String VERIFICATION_CODE = "code";
    /** 与验证码关联的 uuid 存入 header 中的 key */
    public static final String VERIFICATION_UUID = "uuid";
    /** 验证码相关接口 */
    public static final String CAPTCHA_URL = "/captcha/*";
}
