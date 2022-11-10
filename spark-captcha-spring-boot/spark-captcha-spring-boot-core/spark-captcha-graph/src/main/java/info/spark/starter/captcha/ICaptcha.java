package info.spark.starter.captcha;

import info.spark.starter.captcha.entity.Captcha;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description: 验证码组件接口</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:04
 * @since 1.0.0
 */
public interface ICaptcha {

    /**
     * 渲染验证码
     *
     * @return 验证码内容 captcha
     * @since 1.0.0
     */
    Captcha render();

    /**
     * Render io output stream.
     *
     * @return the output stream
     * @since 1.0.0
     */
    @NotNull
    Captcha renderIo();

    /**
     * 校对验证码,默认超时15分钟（900s）
     *
     * @param uuid the uuid
     * @param code 需要验证的字符串
     * @since 1.0.0
     */
    void validate(String uuid, String code);

    /**
     * 是否开启验证码功能
     * 如果应用设置未设置 {@link info.spark.starter.captcha.autoconfigure.CaptchaProperties#type}, 则直接返回 false
     * 如果设置为 ture, 会检查当前用户登录失败的次数, 如果在配置的时间内累计登录次数达到阈值, 则返回 true, 即需要验证码
     *
     * @param request request
     * @return the boolean
     * @since 1.0.0
     */
    @SuppressWarnings("all")
    boolean showCaptcha(HttpServletRequest request);
}
