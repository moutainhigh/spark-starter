package info.spark.starter.captcha;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import info.spark.starter.captcha.entity.CaptchaEnhancerEntity;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.SneakyThrows;

/**
 * <p>Description: 本地缓存, 保存验证码, 记录被保护接口调用次数 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.24 17:10
 * @since 1.0.0
 */
public class LocalMemoryCaptchaCache implements CaptchaCache {

    /**
     * Local memory captcha cache
     *
     * @param properties properties
     * @since 1.0.0
     */
    public LocalMemoryCaptchaCache(@NotNull CaptchaEnhancerEntity properties) {
        CountCache.init(properties.getRequestCountExpiresTime());
        CaptchaCache.init(properties.getCaptchaExpiresTime());
    }

    /**
     * 增加被保护接口调用次数
     *
     * @param key key
     * @since 1.0.0
     */
    @SneakyThrows
    @Override
    public void increment(String key) {
        CountCache.inr(key);
    }

    /**
     * 获取接口被保护次数
     *
     * @param key key
     * @return the int
     * @since 1.0.0
     */
    @SneakyThrows
    @Override
    public Integer getCount(String key) {
        return CountCache.getCount(key);
    }

    /**
     * 删除被保护接口数据
     *
     * @param key key
     * @since 1.0.0
     */
    @Override
    public void deleteCount(String key) {
        CountCache.deleteCount(key);
    }

    /**
     * Gets cache *
     *
     * @param uuid uuid
     * @return the cache
     * @since 1.0.0
     */
    @Override
    public String getCaptcha(String uuid) {
        return CaptchaCache.getCaptchaByUuid(uuid);
    }

    /**
     * Sets captcha *
     *
     * @param uuid       uuid
     * @param captcha    captcha
     * @param expireTime expire time
     * @param timeUnit   time unit
     * @since 1.0.0
     */
    @Override
    public void setCaptcha(String uuid, String captcha, Long expireTime, TimeUnit timeUnit) {
        CaptchaCache.setCaptcha(uuid, captcha);
    }

    /**
     * Deletecaptcha
     *
     * @param uuid uuid
     * @since 1.0.0
     */
    @Override
    public void deleteCaptcha(String uuid) {
        CaptchaCache.deleteCaptcha(uuid);
    }

    /**
         * <p>Description: 被保护接口调用次数统计 </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.24 17:57
     * @since 1.0.0
     */
    private static class CountCache {

        /** key 过期后自动刷新 */
        private static LoadingCache<String, AtomicInteger> cache;

        /**
         * Init *
         *
         * @param ex ex
         * @since 1.0.0
         */
        static void init(Long ex) {
            cache = CacheBuilder.newBuilder().expireAfterWrite(ex, TimeUnit.SECONDS).build(new CacheLoader<String, AtomicInteger>() {
                @Override
                public AtomicInteger load(@NotNull String key) {
                    return new AtomicInteger(0);
                }
            });
        }

        /**
         * Increment *
         *
         * @param key key
         * @since 1.0.0
         */
        @SneakyThrows
        static void inr(String key) {
            cache.get(key).getAndIncrement();
        }

        /**
         * Gets count *
         *
         * @param key key
         * @return the count
         * @since 1.0.0
         */
        @NotNull
        @SneakyThrows
        static Integer getCount(String key) {
            return cache.get(key).get();
        }

        /**
         * Delete *
         *
         * @param key key
         * @since 1.0.0
         */
        static void deleteCount(String key) {
            cache.invalidate(key);
        }

    }

    /**
         * <p>Description: uuid:captcha 的缓存 </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.24 18:00
     * @since 1.0.0
     */
    private static class CaptchaCache {
        /** key 过期后自动刷新 */
        private static LoadingCache<String, String> cache;

        /**
         * Init *
         *
         * @param ex ex
         * @since 1.0.0
         */
        static void init(Long ex) {
            cache = CacheBuilder.newBuilder().expireAfterWrite(ex, TimeUnit.SECONDS).build(new CacheLoader<String, String>() {
                @Override
                public String load(@NotNull String key) {
                    return "";
                }
            });
        }

        /**
         * Increment *
         *
         * @param uuid uuid
         * @return the captcha by uuid
         * @since 1.0.0
         */
        @SneakyThrows
        static String getCaptchaByUuid(String uuid) {
            return cache.get(uuid);
        }


        /**
         * Sets captcha *
         *
         * @param uuid    uuid
         * @param captcha captcha
         * @since 1.0.0
         */
        @SneakyThrows
        static void setCaptcha(String uuid, String captcha) {
            cache.put(uuid, captcha);
        }

        /**
         * Delete *
         *
         * @param uuid uuid
         * @since 1.0.0
         */
        static void deleteCaptcha(String uuid) {
            cache.invalidate(uuid);
        }

    }
}
