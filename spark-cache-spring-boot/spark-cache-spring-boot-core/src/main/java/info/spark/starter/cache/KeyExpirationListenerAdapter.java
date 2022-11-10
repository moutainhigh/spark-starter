package info.spark.starter.cache;

import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.exception.ServiceInternalException;
import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.basic.util.StringUtils;
import info.spark.starter.common.event.BaseEventHandler;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.08 16:27
 * @since 1.5.0
 */
@Slf4j
public class KeyExpirationListenerAdapter extends BaseEventHandler<RedisKeyExpiredEvent<String>> {

    /** Handers */
    private static final Map<String, KeyExpirationHander> HANDER_MAP = new ConcurrentHashMap<>();

    /**
     * Add hander
     *
     * @param key    key
     * @param hander hander
     * @since 1.5.0
     */
    public void addHander(String key, KeyExpirationHander hander) {
        this.addHander(key, hander, true);
    }

    /**
     * Add hander
     *
     * @param key         key
     * @param hander      hander
     * @param checkExists check exists
     * @since 1.9.0
     */
    public void addHander(String key, KeyExpirationHander hander, boolean checkExists) {
        Assertions.notBlank(key, "key 不能为空字符串或 null");
        if (checkExists) {
            Assertions.isNull(HANDER_MAP.get(key), () -> {
                String message = StrFormatter.format("已存在同名 key: [{}] 过期处理器: [{}]",
                                                     key,
                                                     HANDER_MAP.get(key).getClass().getName());
                return new ServiceInternalException(message);
            });
        } else {
            // 如果不检查则先删除原来的 handler
            HANDER_MAP.remove(key);
        }
        HANDER_MAP.put(key, hander);
    }

    /**
     * Handler.
     *
     * @param event the event
     * @since 1.5.0
     */
    @Override
    @EventListener
    public void handler(@NotNull RedisKeyExpiredEvent<String> event) {
        log.trace("redis key expired event: {}", event);
        String key = new String(event.getId());
        if (StringUtils.isNotBlank(event.getKeyspace())) {
            key = event.getKeyspace() + StringPool.COLON + key;
        }
        KeyExpirationHander keyExpirationHander = HANDER_MAP.get(key);
        if (keyExpirationHander != null) {
            keyExpirationHander.hander();
            HANDER_MAP.remove(key);
        }
    }
}
