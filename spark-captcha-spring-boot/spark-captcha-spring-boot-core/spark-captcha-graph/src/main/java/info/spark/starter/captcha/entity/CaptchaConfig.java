package info.spark.starter.captcha.entity;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.24 18:19
 * @since 1.0.0
 */
public class CaptchaConfig {

    /**
         * <p>Description: 验证码类型</p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.21 17:18
     * @since 1.0.0
     */
    public enum CaptchaType {
        /** Common type */
        COMMON,
        /** Dynamic type */
        DYNAMIC
    }

    /**
         * <p>Description: 验证码动态检查类型 </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.01 20:26
     * @since 1.0.0
     */
    public enum CheckType {
        /** 请求失败 */
        ABORTED,
        /** 请求频率 */
        FREQUENCY
    }
}
