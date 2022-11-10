package info.spark.starter.captcha.enhancer;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Description: 验证码增强接口 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.21 19:14
 * @since 1.0.0
 */
public interface CaptchaCheckEnhancer {

    /**
     * Check boolean
     *
     * @param request request
     * @return the boolean true: 需要验证码, false: 不需要验证码
     * @since 1.0.0
     */
    boolean check(HttpServletRequest request);

    /**
     * 接口计数器
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @param ex       ex
     * @since 1.0.0
     */
    void counter(@NotNull HttpServletRequest request,
                 @NotNull HttpServletResponse response,
                 @NotNull Object handler,
                 Exception ex);
}
