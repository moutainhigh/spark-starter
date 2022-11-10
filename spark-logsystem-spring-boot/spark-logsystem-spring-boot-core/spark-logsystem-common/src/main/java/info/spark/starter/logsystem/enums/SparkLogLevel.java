package info.spark.starter.logsystem.enums;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: 请求日志级别</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:19
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum SparkLogLevel {
    /**
     * No logs.
     */
    NONE(0),

    /**
     * Logs request and response lines.
     * <p>Example:
     * <pre>{@code
     * --> POST /greeting http/1.1 (3-byte body)
     * <-- 200 OK (22ms, 6-byte body)
     * }**</pre>
     */
    BASIC(1),

    /**
     * Logs request and response lines and their respective headers.
     * <p>Example:
     * <pre>{@code
     * --> POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     * --> END POST
     * <-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     * <-- END HTTP
     * }**</pre>
     */
    HEADERS(2),

    /**
     * Logs request and response lines and their respective headers and bodies (if present).
     * <p>Example:
     * <pre>{@code
     * --> POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     * Hi?
     * --> END POST
     * <-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     * Hello!
     * <-- END HTTP
     * }**</pre>
     */
    BODY(3);

    /**
     * 级别
     */
    private final int level;

    /**
     * 当前版本 小于和等于 比较的版本
     *
     * @param level LogLevel
     * @return 是否小于和等于 boolean
     * @since 1.0.0
     */
    @Contract(pure = true)
    public boolean lte(@NotNull SparkLogLevel level) {
        return this.level <= level.level;
    }

}
