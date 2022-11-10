package info.spark.starter.mq.consumer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.mq.MessageConstant;
import info.spark.starter.mq.entity.AbstractMessage;
import info.spark.starter.mq.entity.Distribute;
import info.spark.starter.mq.support.ConsumerCacheMaps;
import info.spark.starter.mq.support.MessageDispatcher;
import info.spark.starter.mq.support.MessageProcessManager;
import info.spark.starter.util.StringUtils;

import org.slf4j.MDC;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 消息处理基类, 用于分发消息 </p>
 * <p>
 * todo-dong4j : (2020.07.15 17:54) [处理 traceId]
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 16:02
 * @since 1.5.0
 */
@Slf4j
@SuppressWarnings("rawtypes")
public abstract class AbstractConsumer implements MessageDispatcher, DisposableBean {

    /** 如果没有找到被 @MessageHandler 标识的 bean, 将不会初始化 */
    private ExecutorService pool;
    /** lock */
    public static ReentrantLock lock = new ReentrantLock();
    /** Message process manager */
    private final MessageProcessManager messageProcessManager = new MessageProcessManager();

    /**
     * spring 实例化 BaseMessageConsumer 子类时, 将调用此构造获取所有 messageHandler
     *
     * @since 1.5.0
     */
    protected AbstractConsumer() {

        if (this.messageProcessManager.handlerSize() > 0) {
            // 初始化线程池
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("consumer-%d").build();
            // I/O 密集型操作核心线程数 = 2N + 1, CUP 密集型操作核心线程数 = N + 1
            int corePoolSize = 2 * Runtime.getRuntime().availableProcessors() + 1;
            this.pool = new ThreadPoolExecutor(corePoolSize,
                                               Math.max(1024, corePoolSize),
                                               120L,
                                               TimeUnit.MILLISECONDS,
                                               new LinkedBlockingQueue<>(2048),
                                               namedThreadFactory,
                                               new ThreadPoolExecutor.AbortPolicy());
        } else {
            log.warn("未找到任何被 @MessageHandler 标识的 AbstractMessageHandler 子类");
        }

    }

    /**
     * 将消息分发到具体的处理器处理, string 类型的消息需要从消息体中包含 type, traceId 和 content
     *
     * @param message the message
     * @since 1.5.0
     */
    @Override
    public void dispatcher(String message) {
        log.debug("received message: {}", message);
        if (this.pool != null && !this.pool.isShutdown()) {
            this.pool.execute(() -> {
                try {
                    JsonNode jsonNode = JsonUtils.readTree(message);
                    String type = jsonNode.path(MessageConstant.MESSAGE_TYPE).asText();
                    String traceId = jsonNode.path(MessageConstant.MESSAGE_TRACE_ID).asText();
                    String content = jsonNode.path(MessageConstant.MESSAGE_CONTENT).asText();

                    if (StringUtils.isAnyBlank(type, content)) {
                        log.debug("消息被忽略: type 或 content 为空");
                        return;
                    }

                    // 从消息体中取出 traceId, 设置到当前线程中
                    MDC.put(BasicConstant.TRACE_ID, traceId);
                    this.messageProcessManager.loadMessageHandler(type).handle(content);
                    MDC.remove(BasicConstant.TRACE_ID);
                } catch (Exception e) {
                    log.error("", e);
                }
            });
        }
    }

    /**
     * Dispatcher
     *
     * @param distribute  distribute
     * @param callback    callback
     * @since 1.7.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public void dispatcher(Distribute distribute, Consumer<Boolean> callback) {
        log.debug("received message: {}", distribute.getMessage());
        if (this.pool != null && !this.pool.isShutdown()) {
            this.pool.execute(() -> {
                try {
                    Map<String, Object> paramMap = JsonUtils.parse(distribute.getMessage(), Map.class);
                    AbstractMessageNotifyHandler handler =
                        this.messageProcessManager.loadMessageNotifyHandler(paramMap, distribute);
                    if (handler != null) {
                        log.debug("找到的`handler`为: {}", handler);
                        callback.accept(handler.handler(JsonUtils.toJson(paramMap.get(ConsumerCacheMaps.ConsumerKeys.MESSAGE_SOURCE)),
                                                        distribute.getMessageExtAdapter()));
                    } else {
                        log.warn("未找到 handler 消息将不再处理。");
                        callback.accept(true);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    callback.accept(false);
                }
            });
        }
    }

    /**
     * 将消息分发到具体的处理器处理, 实体类型的消息需要从 header 中获取 type 和 traceId
     *
     * @param <T>     parameter
     * @param message message
     * @since 1.5.0
     */
    @Override
    public <T extends AbstractMessage> void dispatcher(T message) {
        if (this.pool != null && !this.pool.isShutdown()) {
            this.pool.execute(() -> {
                try {
                    // 从消息体中取出 traceId, 设置到当前线程中
                    // MDC.put(BasicConstant.TRACE_ID, jsonNode.path(BasicConstant.TRACE_ID).asText());
                    @SuppressWarnings("unchecked")
                    AbstractMessageHandler<T> abstractMessageHandler =
                        (AbstractMessageHandler<T>) this.messageProcessManager.loadMessageHandler(message.getClass().getName());
                    abstractMessageHandler.handle(message);
                    // MDC.remove(BasicConstant.TRACE_ID);
                } catch (Exception e) {
                    log.error("", e);
                }
            });
        }
    }

    /**
     * 测试 topic 发送消息时需要显式设置 topic = test_topic, 或者直接在配置文件中设置
     */
    public static final String TEST_TOPIC = "test_topic";
    /** TEST_ACKNOWLEDGE_TOPIC */
    public static final String TEST_ACKNOWLEDGE_TOPIC = "test_acknowledge_topic";

    /**
     * 测试用
     *
     * @param message the message
     * @since 1.5.0
     */
    protected void test(String message) {

    }

    /**
     * Destroy
     *
     * @throws Exception exception
     * @since 1.5.0
     */
    @Override
    public void destroy() throws Exception {
        this.pool.shutdown();
    }
}
