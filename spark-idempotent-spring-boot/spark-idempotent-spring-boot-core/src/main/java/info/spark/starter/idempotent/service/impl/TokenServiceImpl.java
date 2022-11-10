package info.spark.starter.idempotent.service.impl;

import info.spark.starter.cache.service.CacheService;
import info.spark.starter.idempotent.service.TokenService;
import info.spark.starter.util.DateUtils;
import info.spark.starter.util.ObjectUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.idempotent.exception.IdempotentException;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description:  </p>
 *
 * @author liujintao
 * @version 1.0.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.21 10:18
 * @since 1.0.0
 */
public class TokenServiceImpl implements TokenService {

    /** Cache service */
    private final CacheService cacheService;
    /** Expire */
    private final Long expire;

    /**
     * Token service
     *
     * @param cacheService cache service
     * @param expire       expire
     * @since 1.0.0
     */
    @Contract(pure = true)
    public TokenServiceImpl(CacheService cacheService, Long expire) {
        this.cacheService = cacheService;
        this.expire = expire;
    }

    /**
     * Create token
     * token = xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
     * key = idempotent:token:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
     *
     * @return the string
     * @since 1.0.0
     */
    @Override
    public String createToken() {
        String token = StringUtils.getUid();
        String key = TOKEN_NAME + token;
        this.cacheService.set(key, DateUtils.formatDate(new Date()), this.expire);
        return token;
    }

    /**
     * Check token
     *
     * @param request request
     * @throws Exception exception
     * @since 1.0.0
     */
    @Override
    public void checkToken(@NotNull HttpServletRequest request) {
        String token = request.getHeader(HEADER_TOKEN_NAME);
        token = StringUtils.isBlank(token) ? request.getParameter(PARAM_TOKEN_NAME) : token;

        if (StringUtils.isBlank(token)) {
            throw new IdempotentException(StringUtils.format("header 不存在 {} 或 param 不存在 {}",
                                                             HEADER_TOKEN_NAME,
                                                             PARAM_TOKEN_NAME));
        }

        String key = TOKEN_NAME + token;
        if (ObjectUtils.isNull(this.cacheService.get(key))) {
            throw new IdempotentException("重复请求");
        }
        this.cacheService.del(key);
    }
}
