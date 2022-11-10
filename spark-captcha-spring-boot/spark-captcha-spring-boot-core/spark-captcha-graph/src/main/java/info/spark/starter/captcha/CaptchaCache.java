package info.spark.starter.captcha;

import java.util.concurrent.TimeUnit;

/**
 * <p>Description: 被验证码保护的资源的统计 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.24 16:41
 * @since 1.0.0
 */
public interface CaptchaCache {

    /**
     * Increment
     *
     * @param key key
     * @since 1.0.0
     */
    void increment(String key);

    /**
     * Get int
     *
     * @param key key
     * @return the int
     * @since 1.0.0
     */
    Integer getCount(String key);

    /**
     * Delete
     *
     * @param key key
     * @since 1.0.0
     */
    void deleteCount(String key);

    /**
     * Gets captcha *
     *
     * @param uuid uuid
     * @return the captcha
     * @since 1.0.0
     */
    String getCaptcha(String uuid);

    /**
     * Sets captcha *
     *
     * @param uuid       uuid
     * @param captcha    captcha
     * @param expireTime expire time
     * @param timeUnit   time unit
     * @since 1.0.0
     */
    void setCaptcha(String uuid, String captcha, Long expireTime, TimeUnit timeUnit);

    /**
     * Delete captcha *
     *
     * @param uuid uuid
     * @since 1.0.0
     */
    void deleteCaptcha(String uuid);
}
