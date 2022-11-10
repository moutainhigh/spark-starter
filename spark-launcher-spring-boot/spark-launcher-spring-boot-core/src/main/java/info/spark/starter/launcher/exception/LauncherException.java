package info.spark.starter.launcher.exception;

import info.spark.starter.basic.exception.BasicException;

/**
 * <p>Description: 启动器相关异常 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.17 10:35
 * @since 1.7.0
 */
public class LauncherException extends BasicException {

    private static final long serialVersionUID = -815914023239408247L;

    /**
     * Dns cache manipulator exception
     *
     * @param message message
     * @since 1.7.0
     */
    public LauncherException(String message) {
        super(message);
    }

    /**
     * Dns cache manipulator exception
     *
     * @param message message
     * @param cause   cause
     * @since 1.7.0
     */
    public LauncherException(String message, Throwable cause) {
        super(message, cause);
    }
}
