package info.spark.starter.openness.exception;

import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description: </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.08.19 17:55
 * @since 1.9.0
 */
public class OpennessException extends BaseException {
    /** serialVersionUID */
    private static final long serialVersionUID = 28675230180858500L;

    /**
     * Openness exception
     *
     * @since 1.9.0
     */
    public OpennessException() {
        super();
    }

    /**
     * Openness exception
     *
     * @param msg msg
     * @since 1.9.0
     */
    public OpennessException(String msg) {
        super(msg);
    }

    /**
     * Openness exception
     *
     * @param msg  msg
     * @param args args
     * @since 1.9.0
     */
    public OpennessException(String msg, Object... args) {
        super(msg, args);
    }

    /**
     * Openness exception
     *
     * @param resultCode result code
     * @since 1.9.0
     */
    public OpennessException(IResultCode resultCode) {
        super(resultCode);
    }

    /**
     * Openness exception
     *
     * @param resultCode result code
     * @param args       args
     * @param message    message
     * @since 1.9.0
     */
    public OpennessException(IResultCode resultCode, Object[] args, String message) {
        super(resultCode, args, message);
    }

    /**
     * Openness exception
     *
     * @param resultCode result code
     * @param args       args
     * @param message    message
     * @param cause      cause
     * @since 1.9.0
     */
    public OpennessException(IResultCode resultCode, Object[] args, String message, Throwable cause) {
        super(resultCode, args, message, cause);
    }
}
