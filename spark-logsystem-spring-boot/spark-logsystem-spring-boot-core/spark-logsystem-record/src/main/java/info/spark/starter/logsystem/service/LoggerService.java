package info.spark.starter.logsystem.service;

import info.spark.starter.logsystem.publisher.BusinessLogPublisher;

import org.jetbrains.annotations.Contract;

/**
 * <p>Description: 业务日志发送服务 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.22 16:29
 * @since 1.0.0
 */
public class LoggerService {

    /**
     * Info *
     *
     * @param id   id
     * @param data data
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static void info(String id, String data) {
        BusinessLogPublisher.publishEvent("info", id, data);
    }

    /**
     * Debug *
     *
     * @param id   id
     * @param data data
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static void debug(String id, String data) {
        BusinessLogPublisher.publishEvent("debug", id, data);
    }

    /**
     * Warn *
     *
     * @param id   id
     * @param data data
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static void warn(String id, String data) {
        BusinessLogPublisher.publishEvent("warn", id, data);
    }

    /**
     * Error *
     *
     * @param id   id
     * @param data data
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static void error(String id, String data) {
        BusinessLogPublisher.publishEvent("error", id, data);
    }
}
