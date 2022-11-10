package info.spark.starter.cache.service.impl;

import com.alicp.jetcache.Cache;
import info.spark.starter.cache.service.AbstractCacheService;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.22 17:33
 * @since 1.6.0
 * @since 1.6.0
 */
public class LinkedHashMapCacheServiceImpl extends AbstractCacheService {
    /**
     * Abstract cache service
     *
     * @param cache cache
     * @since 1.6.0
     */
    public LinkedHashMapCacheServiceImpl(Cache<String, Object> cache) {
        super(cache);
    }
}
