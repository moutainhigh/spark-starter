package info.spark.starter.es.exception;

import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.15 13:21
 * @since 1.0.0
 */
public class EsException extends BaseException {
    /** serialVersionUID */
    private static final long serialVersionUID = 28675230180858500L;

    /**
     * Common exception
     *
     * @since 1.0.0
     */
    public EsException() {
        super();
        this.resultCode = BaseCodes.FAILURE;
    }

    /**
     * Common exception
     *
     * @param msg msg
     * @since 1.0.0
     */
    public EsException(String msg) {
        super(msg);
        this.resultCode = BaseCodes.FAILURE;
    }

    /**
     * Payment exception
     *
     * @param msg  msg
     * @param args args
     * @since 1.0.0
     */
    public EsException(String msg, Object... args) {
        super(msg, args);
    }

    /**
     * Common exception
     *
     * @param resultCode result code
     * @since 1.0.0
     */
    public EsException(IResultCode resultCode) {
        super(resultCode);
    }

    /**
     * Common exception
     *
     * @param resultCode result code
     * @param args       args
     * @param message    message
     * @since 1.0.0
     */
    public EsException(IResultCode resultCode, Object[] args, String message) {
        super(resultCode, args, message);
    }

    /**
     * Common exception
     *
     * @param resultCode result code
     * @param args       args
     * @param message    message
     * @param cause      cause
     * @since 1.0.0
     */
    public EsException(IResultCode resultCode, Object[] args, String message, Throwable cause) {
        super(resultCode, args, message, cause);
    }
}
