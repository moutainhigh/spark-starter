package info.spark.starter.captcha;

/**
 * <p>Description: 自定义验证码接口 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.20 17:48
 * @since 1.0.0
 */
public interface CaptchaCustomizer {

    /**
     * 用于自定义 {@link AbstractCaptcha} 实例的回调
     *
     * @param captcha captcha
     * @since 1.0.0
     */
    void customize(AbstractCaptcha captcha);
}
