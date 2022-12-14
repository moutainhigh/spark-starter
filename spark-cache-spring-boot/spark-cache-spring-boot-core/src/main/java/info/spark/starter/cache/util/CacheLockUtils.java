package info.spark.starter.cache.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import info.spark.starter.cache.exception.CacheLockException;
import info.spark.starter.cache.support.FastjsonKeyConvertor;
import info.spark.starter.common.context.SpringContext;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author liujintao
 * @version 1.6.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.09.09 16:01
 * @since 1.6.0
 */
@UtilityClass
@Slf4j
public class CacheLockUtils {
    /** Redis lock registry */
    private static RedisLockRegistry redisLockRegistry;
    /** threadLocal */
    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();
    /** delScript */
    private static final String DEL_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then redis.call('del', KEYS[1]) return true end  "
                                             + "return false";
    /** DELAY_SCRIPT */
    private static final String DELAY_SCRIPT = "local current = redis.call('GET', KEYS[1])\n"
                                               + "if current == ARGV[1]\n"
                                               + "  then redis.call('EXPIRE', KEYS[1], ARGV[2])\n"
                                               + "  return true\n"
                                               + "end\n"
                                               + "return false";

    /** schedule */
    @NotNull
    private static final ScheduledExecutorService SCHEDULE =
        TtlExecutors.getTtlScheduledExecutorService(new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r);
            thread.setName("dis-lock-delay-thread");
            return thread;
        }));
    /** tl */
    private static final ThreadLocal<Holder> TL = new TransmittableThreadLocal<>();

    /**
     * Lock
     *
     * @param key     key ????????????
     * @param retries retries ????????????, ????????????
     * @param timeout timeout ????????????????????????, ??? timeout ??????????????????????????????????????????
     * @param unit    unit ??????????????????????????????
     * @return the lock   ?????????: lock.unlock();
     * @throws InterruptedException interrupted exception
     * @since 1.6.0
     */
    public static @NotNull Lock lock(String key, int retries, int timeout, TimeUnit unit) throws InterruptedException {
        if (CacheLockUtils.redisLockRegistry == null) {
            CacheLockUtils.redisLockRegistry = SpringContext.getInstance(RedisLockRegistry.class);
        }
        return lock(CacheLockUtils.redisLockRegistry, key, retries, timeout, unit);
    }

    /**
     * Lock
     *
     * @param redisLockRegistry redis lock registry
     * @param key               key
     * @param retries           retries
     * @param timeout           timeout
     * @param unit              unit
     * @return the lock
     * @throws InterruptedException interrupted exception
     * @since 2.1.0
     */
    @SuppressWarnings("DanglingJavadoc")
    public static @NotNull Lock lock(@NotNull RedisLockRegistry redisLockRegistry,
                                     String key,
                                     int retries,
                                     int timeout,
                                     TimeUnit unit) throws InterruptedException {

        Lock lock = redisLockRegistry.obtain(key);
        boolean lockFlag;
        retries = retries <= 0 ? 1 : retries;
        // ?????? retries ?????????????????????
        for (int i = 0; i < retries; i++) {
            /** @see RedisLockRegistry.RedisLock#tryLock() */
            lockFlag = lock.tryLock(timeout, unit);
            if (lockFlag) {
                return lock;
            }
            Thread.sleep(100L);
        }
        throw new CacheLockException(StringUtils.format("[{}] ?????? [{}] ?????????????????????: [{}] ??????, ??????: [{} {}]",
                                                        Thread.currentThread().getName(),
                                                        retries,
                                                        key,
                                                        retries * timeout,
                                                        unit.name()));
    }

    /**
     * Lock by expire
     *
     * @param key     key
     * @param retries retries ????????????, ????????????
     * @param seconds seconds ????????????, ?????????
     * @return the boolean
     * @throws InterruptedException interrupted exception
     * @since 1.6.0
     */
    public static @NotNull Boolean lockByExpire(String key, int retries, int seconds) throws InterruptedException {
        return lockByExpire(key, retries, seconds * 1000, 30, TimeUnit.SECONDS);
    }

    /**
     * Lock by expire
     *
     * @param key     key
     * @param retries retries ????????????, ????????????
     * @param timeout timeout ????????????, ?????????
     * @param expired expired ????????????, ?????????
     * @return the boolean
     * @throws InterruptedException interrupted exception
     * @since 1.6.0
     */
    public static @NotNull Boolean lockByExpire(String key, int retries, int timeout, int expired) throws InterruptedException {
        return lockByExpire(key, retries, timeout * 1000, expired, TimeUnit.SECONDS);
    }

    /**
     * Lock by expire
     *
     * @param key     key
     * @param seconds seconds ????????????, ?????????
     * @return the boolean
     * @throws InterruptedException interrupted exception
     * @since 1.6.0
     */
    public static @NotNull Boolean lockByExpire(String key, int seconds) throws InterruptedException {
        return lockByExpire(key, 3, seconds * 1000, 30, TimeUnit.SECONDS);
    }

    /**
     * Lock by expire
     *
     * @param key     key
     * @param retries retries ??????, ????????????
     * @param timeout timeout ????????????(??????)
     * @param expired expired ??????????????????(?????? redis ??? key ???????????????)
     * @param unit    unit ??????????????????
     * @return the boolean
     * @throws InterruptedException interrupted exception
     * @since 1.6.0
     */
    public static @NotNull Boolean lockByExpire(String key,
                                                int retries,
                                                int timeout,
                                                int expired,
                                                TimeUnit unit) throws InterruptedException {
        return lockByExpire(key, retries, timeout, expired, unit, false);
    }

    /**
     * Lock by expire
     *
     * @param key     key
     * @param retries retries ??????, ????????????
     * @param timeout timeout ????????????(??????)
     * @param expired expired ??????????????????(?????? redis ??? key ???????????????)
     * @param unit    unit ??????????????????
     * @param delay   ??????????????????????????????
     * @return the boolean
     * @throws InterruptedException interrupted exception
     * @since 1.6.0
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static @NotNull Boolean lockByExpire(String key,
                                                int retries,
                                                int timeout,
                                                int expired,
                                                TimeUnit unit,
                                                boolean delay) throws InterruptedException {
        String value = UUID.randomUUID().toString();
        THREAD_LOCAL.set(value);
        Boolean rs = false;
        retries = retries <= 0 ? 1 : retries;
        // ?????? retries ?????????????????????
        for (int i = 0; i < retries; i++) {
            // ????????????????????????
            long retryTiemout = System.currentTimeMillis() + timeout;
            while (System.currentTimeMillis() < retryTiemout) {
                rs = getRedisTemplate().opsForValue().setIfAbsent(key, value, expired, unit);
                if (rs != null && rs) {
                    break;
                }
                //noinspection BusyWait
                Thread.sleep(100L);
            }
            if (rs != null && rs) {
                if (delay) {
                    startDelay(key, value, expired, unit);
                }
                return true;
            }
        }
        throw new CacheLockException(StringUtils.format("?????? {} ???????????????????????????, ??????: [{} ???]  ",
                                                        retries,
                                                        retries * timeout / 1000));
    }

    /**
     * ??????????????????????????????
     *
     * @param key     key
     * @param value   value
     * @param expired expired
     * @param unit    unit
     * @since 1.8.0
     */
    private static void startDelay(String key, String value, int expired, TimeUnit unit) {
        long timeout;
        if (unit == TimeUnit.MILLISECONDS) {
            timeout = expired;
        } else if (unit == TimeUnit.SECONDS) {
            timeout = (long) expired * 1000;
        } else {
            throw new CacheLockException(StringUtils.format("?????????????????????????????????????????????????????????????????????????????????,key:[{}]", key));
        }
        Task task = new Task(timeout, key, value);
        Holder holder = new Holder();
        TL.set(holder);
        CacheLockUtils.SCHEDULE.schedule(task, timeout * 2 / 3, TimeUnit.MILLISECONDS);
    }

    /**
     * Unlock by expire
     *
     * @param key key
     * @since 1.6.0
     */
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public static void unlockByExpire(String key) {
        String value = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();
        Holder holder = TL.get();
        if (holder != null) {
            holder.setRun(false);
            TL.remove();
        }
        DefaultRedisScript<Boolean> release = (DefaultRedisScript) RedisScript.of(DEL_SCRIPT);
        release.setResultType(Boolean.class);
        getRedisTemplate().execute(release, Collections.singletonList(key), value);
    }

    /**
         * <p>Description: ?????????????????? holder </p>
     *
     * @author liujintao
     * @version 1.8.0
     * @email "mailto:liujintao@gmail.com"
     * @date 2021.05.28 11:57
     * @since 1.8.0
     */
    @Data
    private static class Holder {
        /** Run */
        private volatile boolean run = true;
    }

    /**
         * <p>Description: </p>
     *
     * @author liujintao
     * @version 1.8.0
     * @email "mailto:liujintao@gmail.com"
     * @date 2021.05.28 13:44
     * @since 1.8.0
     */
    private static class Task implements Runnable {
        /** Delay ??????????????????????????? */
        private final long delay;
        /** Timeout */
        private final int timeout;
        /** Key */
        private final String key;
        /** Value */
        private final String value;
        /** Times */
        private final AtomicInteger times = new AtomicInteger(0);
        /** MAX_TIMES */
        private static final int MAX_TIMES = 10;

        /**
         * Task
         *
         * @param timeout timeout ??????
         * @param key     key
         * @param value   value
         * @since 1.8.0
         */
        Task(long timeout, String key, String value) {
            this.delay = timeout * 2 / 3;
            // ????????????
            this.timeout = (int) timeout / 1000;
            this.key = key;
            this.value = value;
        }

        /**
         * Run
         * ?????????????????? ??? run ??? true ??? ?????????????????????????????? MAX_TIMES
         *
         * @since 1.8.0
         */
        @Override
        @SuppressWarnings(value = {"unchecked", "rawtypes"})
        public void run() {
            Holder holder = TL.get();
            if (holder != null && holder.isRun() && this.times.get() < MAX_TIMES) {
                log.info("[{}] ???????????????key:[{}]??????????????????:[{}}", Thread.currentThread().getName(), this.key, this.timeout);
                DefaultRedisScript<Boolean> release = (DefaultRedisScript) RedisScript.of(DELAY_SCRIPT);
                release.setResultType(Boolean.class);
                Boolean execute = getRedisTemplate().execute(release, Collections.singletonList(this.key), this.value, this.timeout);
                if (Boolean.TRUE.equals(execute)) {
                    this.times.getAndIncrement();
                    CacheLockUtils.SCHEDULE.schedule(this, this.delay, TimeUnit.MILLISECONDS);
                } else {
                    log.info("[{}] ???????????????key:[{}]????????????????????????.", Thread.currentThread().getName(), this.key);
                }
            } else {
                log.info("[{}] ???????????????key:[{}]???????????????????????????????????????:[{}]", Thread.currentThread().getName(), this.key, this.times);
            }
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.8.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.05.28 16:02
     * @since 1.9.0
     */
    @ThreadSafe
    private static class RedisTemplateHolder {
        /** Template */
        private static final RedisTemplate<String, Object> TEMPLATE;

        static {
            log.info("RedisTemplateHolder ???????????????...");
            TEMPLATE = new RedisTemplate<>();
            TEMPLATE.setConnectionFactory(SpringContext.getInstance(RedisConnectionFactory.class));
            TEMPLATE.setKeySerializer(FastjsonKeyConvertor.INSTANCE);
            TEMPLATE.setValueSerializer(FastjsonKeyConvertor.INSTANCE);
            TEMPLATE.afterPropertiesSet();
        }
    }

    /**
     * Gets redis template *
     *
     * @return the redis template
     * @since 1.9.0
     */
    private static RedisTemplate<String, Object> getRedisTemplate() {
        return RedisTemplateHolder.TEMPLATE;
    }
}
