package info.spark.starter.captcha.enhancer;

import info.spark.starter.captcha.CaptchaCache;
import info.spark.starter.captcha.LocalMemoryCaptchaCache;
import info.spark.starter.captcha.entity.CaptchaEnhancerEntity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.21 19:14
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractCaptchaCheckEnhancer implements CaptchaCheckEnhancer {

    /** Limit */
    protected final Integer limit;

    /** Captchacache */
    @Setter
    @Getter
    protected CaptchaCache captchacache = new LocalMemoryCaptchaCache(CaptchaEnhancerEntity.builder()
                                                                          .captchaExpiresTime(60L)
                                                                          .requestCount(10)
                                                                          .requestCountExpiresTime(240L)
                                                                          .requestFailedCount(5)
                                                                          .requestFailedCountExpiresTime(120L)
                                                                          .build());

    /**
     * Abstract captcha check enhancer
     *
     * @param limit limit
     * @since 1.0.0
     */
    @Contract(pure = true)
    public AbstractCaptchaCheckEnhancer(Integer limit) {
        this.limit = limit;
    }

    /**
     * 接口请求失败次数检查, 达到阈值返回 true
     *
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean check(@NotNull HttpServletRequest request) {
        return this.captchacache.getCount(request.getRequestURI() + request.getRemoteUser()) > this.limit;
    }

    /**
     * Increment *
     *
     * @param key key
     * @since 1.0.0
     */
    protected void increment(String key) {
        this.captchacache.increment(key);
    }
}
