package info.spark.starter.cache.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheValueHolder;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.util.TimeoutUtils;
import info.spark.starter.cache.KeyExpirationListenerAdapter;
import info.spark.starter.cache.RedisMessageSubscriber;
import info.spark.starter.cache.service.AbstractCacheService;
import info.spark.starter.core.function.CheckedConsumer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.19 12:07
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings(value = {"checkstyle:MethodLimit", "PMD:MethodLimit"})
public class RedisCacheServiceImpl extends AbstractCacheService {

    /** Redis template */
    @Getter
    private final RedisTemplate<String, Object> redisTemplate;
    /** Key expiration listener adapter */
    private final KeyExpirationListenerAdapter keyExpirationListenerAdapter;

    /**
     * Instantiates a new Redis service.
     *
     * @param cache                        cache
     * @param redisTemplate                the redis template
     * @param keyExpirationListenerAdapter key expiration listener adapter
     * @since 1.0.0
     */
    @Contract(pure = true)
    public RedisCacheServiceImpl(Cache<String, Object> cache,
                                 RedisTemplate<String, Object> redisTemplate,
                                 KeyExpirationListenerAdapter keyExpirationListenerAdapter) {
        super(cache);

        this.redisTemplate = redisTemplate;
        this.keyExpirationListenerAdapter = keyExpirationListenerAdapter;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为 null
     * @return 时间(秒) 返回0代表为永久有效;如果该key已经过期,将返回"-2";
     * @since 1.0.0
     */
    @Override
    public Long getExpire(String key) {
        return this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     * @since 1.0.0
     */
    @Override
    public Boolean set(String key, Object value, Long time) {
        try {
            if (time > 0) {
                this.redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                this.set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("【redis: 普通缓存放入并设置时间-异常】", e);
            return false;
        }

    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     * @since 1.0.0
     */
    @Override
    public Boolean set(String key, Object value) {
        try {
            this.redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("【redis: 普通缓存放入-异常】", e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @param consumer consumer
     * @return true成功 false 失败
     * @since 1.0.0
     */
    @Override
    public Boolean set(String key, Object value, Long time, CheckedConsumer<String> consumer) {
        this.addHander(key, consumer);
        return this.set(key, value, time);
    }

    /**
     * 普通缓存放入
     *
     * @param key      键
     * @param value    值
     * @param consumer consumer
     * @return true成功 false失败
     * @since 1.0.0
     */
    @Override
    public Boolean set(String key, Object value, CheckedConsumer<String> consumer) {
        this.addHander(key, consumer);
        return this.set(key, value);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key         键
     * @param value       值
     * @param time        时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @param consumer    consumer
     * @param checkExists check exists
     * @return true成功 false 失败
     * @since 1.9.0
     */
    @Override
    public Boolean set(String key, Object value, Long time, CheckedConsumer<String> consumer, boolean checkExists) {
        this.addHander(key, consumer, checkExists);
        return this.set(key, value, time);
    }

    /**
     * 普通缓存放入
     *
     * @param key         键
     * @param value       值
     * @param consumer    consumer
     * @param checkExists check exists
     * @return true成功 false失败
     * @since 1.9.0
     */
    @Override
    public Boolean set(String key, Object value, CheckedConsumer<String> consumer, boolean checkExists) {
        this.addHander(key, consumer, checkExists);
        return this.set(key, value);
    }


    /**
     * Add hander
     *
     * @param key      key
     * @param consumer consumer
     * @since 1.5.0
     */
    private void addHander(String key, CheckedConsumer<String> consumer) {
        this.addHander(key, consumer, true);
    }

    /**
     * Add hander
     *
     * @param key         key
     * @param consumer    consumer
     * @param checkExists check exists
     * @since 1.9.0
     */
    private void addHander(String key, CheckedConsumer<String> consumer, boolean checkExists) {
        this.keyExpirationListenerAdapter.addHander(key, () -> {
                                                        try {
                                                            consumer.accept(key);
                                                        } catch (Throwable throwable) {
                                                            log.error(key + " 过期逻辑执行失败.", throwable);
                                                        }
                                                    },
                                                    checkExists);
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return long long
     * @since 1.0.0
     */
    @Override
    public Long incr(String key, Long delta) {
        Assertions.isTrue(delta > 0, "递增因子必须大于 0");
        return this.redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return long long
     * @since 1.0.0
     */
    @Override
    public Long decr(String key, Long delta) {
        Assertions.isTrue(delta > 0, "递减因子必须大于 0");
        return this.redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 获取缓存
     *
     * @param <T>   the type parameter
     * @param key   redis的key
     * @param clazz value的class类型
     * @return value的实际对象 t
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object obj = key == null ? null : this.redisTemplate.opsForValue().get(key);

        if (obj == null) {
            return null;
        }

        if (obj instanceof CacheValueHolder) {
            return ((CacheValueHolder<T>) obj).getValue();
        }

        return (T) obj;
    }

    /**
     * 获取泛型
     *
     * @param key 键
     * @return 值 object
     * @since 1.0.0
     */
    @Override
    public Object get(String key) {
        return this.get(key, Object.class);
    }

    /**
     * Hset
     *
     * @param key   key
     * @param field field
     * @param value value
     * @return the boolean
     * @since 1.6.0
     */
    @Override
    public Boolean hset(@NotNull String key, @NotNull String field, @NotNull Object value) {
        try {
            this.redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            log.error("写入缓存异常, key: {}, field:{}", key, field);
            return false;
        }

    }

    /**
     * Hset all
     *
     * @param key key
     * @param map map
     * @return the boolean
     * @since 2.0.0
     */
    @Override
    public Boolean hsetAll(String key, Map<?, ?> map) {
        try {
            this.redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("写入Map缓存异常, key: {}", key);
            return false;
        }
    }

    /**
     * Hentries
     *
     * @param key key
     * @return the map
     * @since 2.0.0
     */
    @Override
    public Map<Object, Object> hentries(@NotNull String key) {
        Map<Object, Object> objectEntries = this.redisTemplate.opsForHash().entries(key);
        if (objectEntries.isEmpty()) {
            return new HashMap<>(0);
        }
        Map<Object, Object> entries = new HashMap<>(16);
        objectEntries.forEach((k, v) -> {
            if (v instanceof CacheValueHolder) {
                entries.put(k, ((CacheValueHolder<?>) v).getValue());
            } else {
                entries.put(k, v);
            }
        });
        return entries;
    }

    /**
     * Hget
     *
     * @param key   key
     * @param field field
     * @return the object
     * @since 1.6.0
     */
    @Override
    public Object hget(@NotNull String key, @NotNull String field) {
        return this.hget(key, field, Object.class);
    }

    /**
     * Hget
     *
     * @param <T>   parameter
     * @param key   key
     * @param field field
     * @param clazz clazz
     * @return the t
     * @since 1.6.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T hget(@NotNull String key, @NotNull String field, @NotNull Class<T> clazz) {
        Object obj = this.redisTemplate.opsForHash().get(key, field);
        if (obj == null) {
            return null;
        }
        if (obj instanceof CacheValueHolder) {
            return (T) ((CacheValueHolder<?>) obj).getValue();
        }

        return (T) obj;
    }

    /**
     * Hlen
     *
     * @param key key
     * @return the long
     * @since 1.6.0
     */
    @Override
    public Long hlen(@NotNull String key) {
        return this.redisTemplate.opsForHash().size(key);
    }

    /**
     * Hkeys
     *
     * @param key key
     * @return the set
     * @since 1.6.0
     */
    @Override
    public Set<Object> hkeys(@NotNull String key) {
        return this.redisTemplate.opsForHash().keys(key);
    }

    /**
     * Hvals
     *
     * @param key key
     * @return the list
     * @since 1.6.0
     */
    @Override
    public List<Object> hvals(@NotNull String key) {
        return this.hvals(key, Object.class);
    }

    /**
     * Hvals
     *
     * @param <T>   parameter
     * @param key   key
     * @param clazz clazz
     * @return the list
     * @since 1.6.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> hvals(@NotNull String key, @NotNull Class<T> clazz) {
        List<Object> objectList = this.redisTemplate.opsForHash().values(key);

        if (CollectionUtils.isEmpty(objectList)) {
            return new ArrayList<>();
        }
        List<T> list = new ArrayList<>();
        for (Object o : objectList) {
            if (o instanceof CacheValueHolder) {
                list.add(((CacheValueHolder<T>) o).getValue());
            } else {
                list.add((T) o);
            }
        }
        return list;
    }

    /**
     * Hexists
     *
     * @param key   key
     * @param field field
     * @return the boolean
     * @since 1.6.0
     */
    @Override
    public Boolean hexists(@NotNull String key, @NotNull String field) {
        return this.redisTemplate.opsForHash().hasKey(key, field);
    }


    /**
     * Hincr
     *
     * @param key   key
     * @param field field
     * @param delta delta
     * @return the long
     * @since 2.0.0
     */
    @Override
    public Long hincr(@NotNull String key, String field, long delta) {
        Assertions.isTrue(delta > 0, "递增因子必须大于 0");
        return this.redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * Hdecr
     *
     * @param key   key
     * @param field field
     * @param delta delta
     * @return the long
     * @since 2.0.0
     */
    @Override
    public Long hdecr(@NotNull String key, String field, long delta) {
        Assertions.isTrue(delta > 0, "递增因子必须大于 0");
        return this.redisTemplate.opsForHash().increment(key, field, -delta);
    }

    /**
     * Hdel
     *
     * @param key   key
     * @param filed filed
     * @return the long
     * @since 1.6.0
     */
    @Override
    @SuppressWarnings("all")
    public Long hdel(@NotNull String key, @NotNull String... filed) {
        return this.redisTemplate.opsForHash().delete(key, filed);
    }

    /**
     * Lpush
     *
     * @param key   key
     * @param value value
     * @return the boolean
     * @since 1.6.0
     */
    @Override
    public Boolean lpush(@NotNull String key, @NotNull Object value) {
        try {
            this.redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
        return true;
    }

    /**
     * Rpush
     *
     * @param key   key
     * @param value value
     * @return the boolean
     * @since 1.6.0
     */
    @Override
    public Boolean rpush(@NotNull String key, @NotNull Object value) {
        try {
            this.redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
        return true;
    }

    /**
     * Llen
     *
     * @param key key
     * @return the long
     * @since 1.6.0
     */
    @Override
    public Long llen(@NotNull String key) {
        return this.redisTemplate.opsForList().size(key);
    }

    /**
     * Lpop
     *
     * @param key key
     * @return the object
     * @since 1.6.0
     */
    @Override
    public Object lpop(@NotNull String key) {
        return this.lpop(key, Object.class);
    }

    /**
     * Lpop
     *
     * @param <T>   parameter
     * @param key   key
     * @param clazz clazz
     * @return the t
     * @since 1.6.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T lpop(@NotNull String key, @NotNull Class<T> clazz) {
        Object obj = this.redisTemplate.opsForList().leftPop(key);
        if (obj == null) {
            return null;
        }

        if (obj instanceof CacheValueHolder) {
            return (T) ((CacheValueHolder<?>) obj).getValue();
        }

        return (T) obj;
    }

    /**
     * Rpop
     *
     * @param key key
     * @return the object
     * @since 1.6.0
     */
    @Override
    public Object rpop(@NotNull String key) {
        return this.rpop(key, Object.class);
    }

    /**
     * Rpop
     *
     * @param <T>   parameter
     * @param key   key
     * @param clazz clazz
     * @return the t
     * @since 1.6.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T rpop(@NotNull String key, @NotNull Class<T> clazz) {
        Object obj = this.redisTemplate.opsForList().rightPop(key);
        if (obj == null) {
            return null;
        }

        if (obj instanceof CacheValueHolder) {
            return (T) ((CacheValueHolder<?>) obj).getValue();
        }

        return (T) obj;
    }

    /**
     * Sadd
     *
     * @param key   key
     * @param value value
     * @return the boolean
     * @since 1.6.0
     */
    @Override
    public Boolean sadd(@NotNull String key, @NotNull Object... value) {
        try {
            this.redisTemplate.opsForSet().add(key, value);
        } catch (Exception e) {
            log.error("写入缓存异常, key: {}. ", key);
            return false;
        }
        return true;
    }

    /**
     * Scard
     *
     * @param key key
     * @return the long
     * @since 1.6.0
     */
    @Override
    public Long scard(@NotNull String key) {
        return this.redisTemplate.opsForSet().size(key);
    }

    /**
     * Srem
     *
     * @param key   key
     * @param value value
     * @return the long
     * @since 1.6.0
     */
    @Override
    public Long srem(@NotNull String key, Object... value) {
        return this.redisTemplate.opsForSet().remove(key, value);
    }

    /**
     * Spop
     *
     * @param key key
     * @return the object
     * @since 1.6.0
     */
    @Override
    public Object spop(@NotNull String key) {
        return this.spop(key, Object.class);
    }

    /**
     * Spop
     *
     * @param <T>   parameter
     * @param key   key
     * @param clazz clazz
     * @return the t
     * @since 1.6.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T spop(@NotNull String key, Class<T> clazz) {
        Object obj = this.redisTemplate.opsForSet().pop(key);
        if (obj == null) {
            return null;
        }

        if (obj instanceof CacheValueHolder) {
            return (T) ((CacheValueHolder<?>) obj).getValue();
        }

        return (T) obj;
    }

    /**
     * Smembers
     *
     * @param key key
     * @return the set
     * @since 1.6.0
     */
    @Override
    public Set<Object> smembers(String key) {
        return this.smembers(key, Object.class);
    }

    /**
     * Smembers
     *
     * @param <T>   parameter
     * @param key   key
     * @param clazz clazz
     * @return the set
     * @since 1.6.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> smembers(String key, Class<T> clazz) {
        Set<Object> objects = this.redisTemplate.opsForSet().members(key);
        if (CollectionUtils.isEmpty(objects)) {
            return new HashSet<>();
        }
        Set<T> sets = new HashSet<>();

        for (Object o : objects) {
            if (o instanceof CacheValueHolder) {
                sets.add(((CacheValueHolder<T>) o).getValue());
            } else {
                sets.add((T) o);
            }
        }
        return sets;
    }

    /**
     * Zadd
     *
     * @param key   key
     * @param score score
     * @param value value
     * @return the boolean
     * @since 1.6.0
     */
    @Override
    public Boolean zadd(String key, Double score, Object value) {
        return this.redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * Zcard
     *
     * @param key key
     * @return the long
     * @since 1.6.0
     */
    @Override
    public Long zcard(String key) {
        return this.redisTemplate.opsForZSet().size(key);
    }

    /**
     * Zscore
     *
     * @param key   key
     * @param value value
     * @return the double
     * @since 1.6.0
     */
    @Override
    public Double zscore(String key, Object value) {
        return this.redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * Zrem
     *
     * @param key   key
     * @param value value
     * @return the long
     * @since 1.6.0
     */
    @Override
    public Long zrem(String key, Object... value) {
        return this.redisTemplate.opsForZSet().remove(key, value);
    }

    /**
     * Publish
     *
     * @param topic topic
     * @param o     o
     * @since 1.7.0
     */
    @Override
    public void publish(String topic, Object o) {
        this.redisTemplate.convertAndSend(RedisMessageSubscriber.TOPIC_PREFIX + topic, o);
    }

    /**
     * Scan
     *
     * @param key key
     * @return the set
     * @since 2.0.0
     */
    @SneakyThrows
    @Override
    public Set<String> scan(@NotNull String key) {
        return TimeoutUtils.process(() -> this.redisTemplate.execute((RedisCallback<Set<String>>) connect -> {
            Cursor<byte[]> cursor = connect.scan(new ScanOptions.ScanOptionsBuilder()
                                                     .match(key)
                                                     .count(10000).build());
            Set<String> hashSet = new HashSet<>();
            while (cursor.hasNext()) {
                hashSet.add(new String(cursor.next()));
            }
            return hashSet;
        }), 3000L, TimeUnit.MILLISECONDS);
    }

    /**
     * Range by score
     *
     * @param key    key
     * @param min    min
     * @param max    max
     * @param offset offset
     * @param count  count
     * @return the set
     * @since 2.1.0
     */
    public Set<Object> rangeByScore(String key, double min, double max, long offset, long count) {
        return this.redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }
}
