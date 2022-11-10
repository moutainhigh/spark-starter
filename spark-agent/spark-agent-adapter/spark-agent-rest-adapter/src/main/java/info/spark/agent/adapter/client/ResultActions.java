package info.spark.agent.adapter.client;

import info.spark.agent.adapter.entity.AgentRecord;
import info.spark.agent.adapter.exception.AgentRequestFailedException;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.exception.BasicException;

import org.slf4j.event.Level;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.07 23:05
 * @since 1.6.0
 */
public interface ResultActions {

    /**
     * 请求日志记录
     *
     * @param recordService record service
     * @return the result actions
     * @since 1.6.0
     * @deprecated 请使用 {@link ResultActions#record}
     */
    @Deprecated
    default ResultActions andRecord(RecordService recordService) {
        return this.record(recordService);
    }

    /**
     * 请求日志记录
     *
     * @param recordService record service
     * @return the result actions
     * @since 1.7.0
     */
    default ResultActions record(RecordService recordService) {
        return this;
    }

    /**
     * 请求日志记录
     *
     * @param consumer consumer
     * @return the result actions
     * @since 1.7.1
     */
    default ResultActions record(Consumer<AgentRecord> consumer) {
        return this;
    }

    /**
     * 日志输出请求结果, 默认为 debug
     *
     * @return the agent result
     * @since 1.7.0
     */
    default ResultActions print() {
        return this.print(Level.DEBUG);
    }

    /**
     * 日志输出请求结果
     *
     * @param level 日志输出等级
     * @return the agent result
     * @since 1.7.0
     */
    default ResultActions print(Level level) {
        return this;
    }

    /**
     * Callback
     *
     * @param callback callback
     * @return the result actions
     * @since 1.7.0
     */
    default ResultActions callback(ResultCallback callback) {
        return this;
    }

    /**
     * 请求结果失败时将抛出 {@link AgentRequestFailedException} 异常, 业务端可捕获此异常进行其他处理.
     *
     * @return the result actions
     * @throws AgentRequestFailedException 请求失败时抛出
     * @since 1.7.0
     */
    default ResultActions failException() {
        return this;
    }

    /**
     * result.success 为 false 时抛出自定义异常
     *
     * @param exceptionSupplier 自定义异常, 异常消息可自定义
     * @return the result actions
     * @since 1.6.0
     */
    default ResultActions failException(Supplier<? extends BasicException> exceptionSupplier) {
        return this;
    }

    /**
     * result.success 为 false 时抛出自定义异常
     *
     * @param exceptionClass 自定义异常, 异常消息为 result 的 message 和 code
     * @return the result actions
     * @since 1.6.0
     */
    default ResultActions failException(Class<? extends BasicException> exceptionClass) {
        return this;
    }

    /**
     * mock 返回结果 (当调用失败时返回 mock 的数据)
     *
     * @param mockResult mock 的数据
     * @return the result actions
     * @since 1.7.0
     */
    default ResultActions mock(Result<?> mockResult) {
        return this;
    }

    /**
     * 结果返回
     *
     * @return the agent result
     * @since 1.6.0
     */
    AgentResult andReturn();
}
