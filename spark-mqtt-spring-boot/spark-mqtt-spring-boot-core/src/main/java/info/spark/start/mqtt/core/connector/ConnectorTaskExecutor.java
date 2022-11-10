package info.spark.start.mqtt.core.connector;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  重连连接器</p>
 *
 * @author zhonghaijun
 * @version 2.1.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 13:57
 * @since 2.1.0
 */
@Slf4j
public class ConnectorTaskExecutor {

    /** scheduled */
    public static final ScheduledExecutorService SCHEDULED = new ScheduledThreadPoolExecutor(2, (ThreadFactory) Thread::new);

    /** RECONNECT_TASK */
    public static final Map<String, ScheduledFuture<?>> RECONNECT_TASK = new ConcurrentHashMap<>(2);

    /** RECONNECT_COUNT */
    public static final Map<String, AtomicInteger> RECONNECT_COUNT = new ConcurrentHashMap<>(2);

    /**
     * Submit reconnect
     *
     * @param clientId          client id
     * @param reconnect         reconnect
     * @param maxReconnectCount maxReconnectCount
     * @param maxReconnectDelay max reconnect delay
     * @since 2.1.0
     */
    public static void submitReconnect(String clientId,
                                       Runnable reconnect,
                                       Integer maxReconnectCount,
                                       Integer maxReconnectDelay) {
        //先查询是否有任务存在
        AtomicInteger reConnectCount = RECONNECT_COUNT.get(clientId);
        if (Objects.nonNull(reConnectCount) && reConnectCount.get() <= 0) {
            removeReconnect(clientId);
            return;
        }
        ScheduledFuture<?> scheduledFuture = RECONNECT_TASK.get(clientId);
        if (Objects.isNull(reConnectCount) && Objects.isNull(scheduledFuture)) {
            RECONNECT_COUNT.put(clientId, new AtomicInteger(maxReconnectCount));
            scheduledFuture =
                SCHEDULED.scheduleWithFixedDelay(reconnect, 5, maxReconnectDelay, TimeUnit.SECONDS);
            RECONNECT_TASK.put(clientId, scheduledFuture);
        } else {
            //执行减1
            int count = reConnectCount.getAndDecrement();
            log.debug("mqtt client:{}，connect failure，initiate reconnection，重连次数：{}", clientId, count);
        }

    }

    /**
     * Submit connect
     *
     * @param connect connect
     * @since 2.1.0
     */
    public static void submitConnect(Runnable connect) {
        SCHEDULED.schedule(connect, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * 移除重连任务
     *
     * @param clientId client id
     * @since 2.1.0
     */
    public static void removeReconnect(String clientId) {
        ScheduledFuture<?> scheduledFuture = RECONNECT_TASK.get(clientId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            RECONNECT_TASK.remove(clientId);
            RECONNECT_COUNT.remove(clientId);
        }
    }

}
