package info.spark.starter.openness.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;

import info.spark.starter.openness.entity.HandlerEntry;
import info.spark.starter.openness.handler.ISecretAuthHandler;
import info.spark.starter.util.ObjectUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.openness.constant.Constant;
import info.spark.starter.openness.exception.OpennessErrorCodes;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description:  </p>
 *
 * @author zhubo
 * @version 1.9.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.18 15:27
 * @since 1.6.0
 */
public abstract class AbstractOpennessAlgorithm {

    /** Cache */
    private final Cache<String, String> cache;

    /** Secret auth handler */
    private final ISecretAuthHandler secretAuthHandler;
    /** Time interval: 相邻请求的间隔, 默认1000毫秒 */
    private final Long timeInterval;

    /**
     * Abstract openness algorithm
     *
     * @param secretAuthHandler secret auth handler
     * @param timeInterval      time interval
     * @since 2.0.0
     */
    protected AbstractOpennessAlgorithm(ISecretAuthHandler secretAuthHandler, Long timeInterval) {
        this.secretAuthHandler = secretAuthHandler;
        this.timeInterval = timeInterval;
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(100L)
            .expireAfterAccess(this.timeInterval, TimeUnit.SECONDS)
            .removalListener((RemovalListener<String, String>) rn -> {
                // 执行逻辑操作
            })
            .recordStats()
            .build();

    }

    /**
     * 通过 secretKey 对请求，按照规则再次进行签名
     *
     * @param httpServletRequest http servlet request
     * @param handlerEntry       handler entry
     * @param secretKey          secret key
     * @return the string
     * @since 1.9.0
     */
    public abstract String encryption(HttpServletRequest httpServletRequest, HandlerEntry handlerEntry, String secretKey);

    /**
     * 解析得到 请求头 之后，校验
     *
     * @param httpServletRequest http servlet request
     * @param handlerEntry       handler entry
     * @since 1.9.0
     */
    protected abstract void afterCheck(HttpServletRequest httpServletRequest, HandlerEntry handlerEntry);

    /**
     * Check params
     *
     * @param request request
     * @return the handler entry
     * @since 1.9.0
     */
    public HandlerEntry checkParams(HttpServletRequest request) {
        HandlerEntry handlerEntry = HandlerEntry.transformation(request);
        this.afterCheck(request, handlerEntry);
        return handlerEntry;
    }

    /**
     * 验证签名
     *
     * @param httpServletRequest http servlet request
     * @param handlerEntry       handlerEntry
     * @since 1.6.0
     */
    public void checkSign(HttpServletRequest httpServletRequest, HandlerEntry handlerEntry) {
        String secretKey = this.cache.getIfPresent(Constant.REDIS_KEY_CLIENT_PREFIX + handlerEntry.getClientId());

        if (StringUtils.isBlank(secretKey)) {
            secretKey = this.secretAuthHandler.secretKey(handlerEntry.getClientId());
            if (StringUtils.isNotBlank(secretKey)) {
                this.cache.put(Constant.REDIS_KEY_CLIENT_PREFIX + handlerEntry.getClientId(), secretKey);
            }
        }
        // 根据clientSecret 再次对参数，按照规则进行签名，如果一致则通过
        if (!handlerEntry.getSign().equals(this.encryption(httpServletRequest, handlerEntry, secretKey))) {
            throw OpennessErrorCodes.SIGNATURE_ERROR.newException();
        }

    }

    /**
     * 防止重放
     *
     * @param nonce     nonce
     * @param timestamp timestamp
     * @since 1.6.0
     */
    public void preventReplay(String nonce, String timestamp) {
        if (Instant.now().plusMillis(-this.timeInterval).isAfter(Instant.ofEpochMilli(Long.parseLong(timestamp)))) {
            // 请求时间早于 (当前时间 - 允许请求时间间隔) 则认为请求无效
            throw OpennessErrorCodes.REQ_EXPIRED.newException(
                StringUtils.format("nonce={}, timestamp={}, timeInterval={} ms",
                                   nonce, timestamp, this.timeInterval));

        }
        // 判断请求是否为重复的
        if (ObjectUtils.isNotEmpty(this.cache.getIfPresent(Constant.REDIS_KEY_NONCE_PREFIX.concat(nonce)))) {
            // 如果缓存中存在, 则表明已经请求过
            throw OpennessErrorCodes.NOT_RESUBMIT_THE_REQUEST.newException(
                StringUtils.format("nonce={}, timestamp={}, timeInterval={} ms",
                                   nonce, timestamp, this.timeInterval));
        }
        // 将此次请求的唯一标识存入缓存中, 以便下次请求校验重复请求, 过期时间单位为秒
        this.cache.put(Constant.REDIS_KEY_NONCE_PREFIX.concat(nonce), nonce);
    }
}
