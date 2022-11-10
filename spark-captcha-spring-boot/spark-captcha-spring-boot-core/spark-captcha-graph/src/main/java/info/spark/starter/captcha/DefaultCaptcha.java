package info.spark.starter.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;

import info.spark.starter.captcha.entity.CaptchaConfig;

import org.jetbrains.annotations.Contract;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 谷歌默认验证码组件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:05
 * @since 1.0.0
 */
@Slf4j
public class DefaultCaptcha extends AbstractCaptcha {

    /** Type */
    @Setter
    @Getter
    private CaptchaConfig.CaptchaType type = CaptchaConfig.CaptchaType.COMMON;

    /**
     * Default captcha
     *
     * @param kaptcha    kaptcha
     * @param expireTime expire time
     * @since 1.0.0
     */
    @Contract(pure = true)
    public DefaultCaptcha(DefaultKaptcha kaptcha, Long expireTime) {
        super(kaptcha, expireTime);
    }

    /**
     * Abstract captcha
     *
     * @param kaptcha    kaptcha
     * @param expireTime expire time
     * @param timeUnit   time unit
     * @since 1.0.0
     */
    public DefaultCaptcha(DefaultKaptcha kaptcha, Long expireTime, TimeUnit timeUnit) {
        super(kaptcha, expireTime, timeUnit);
    }

    /**
     * 调用 CaptchaCheckEnhancer 接口检查是否需要显示验证码
     *
     * @param request request
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean showCaptcha(HttpServletRequest request) {
        // 未配置 type 则不开启验证码功能
        if (this.type == null) {
            return false;
        }
        // 普通验证码, 一直显示
        if (this.type.equals(CaptchaConfig.CaptchaType.COMMON)) {
            return true;
        }
        // 动态验证码 (只要某一个检查返回 true 则需要显示验证码)
        return this.checkEnhancers.stream().anyMatch((c) -> c.check(request));
    }

}

