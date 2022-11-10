package info.spark.starter.security.exception;

import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:25
 * @since 1.0.0
 */
public class SecurityException extends BaseException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Security exception
     *
     * @param msg msg
     * @since 1.0.0
     */
    public SecurityException(String msg) {
        super(msg);
    }

    /**
     * Security exception
     *
     * @param resultCode result code
     * @since 1.0.0
     */
    public SecurityException(IResultCode resultCode) {
        super(resultCode);
    }


    /**
     * Security exception
     *
     * @param resultCode result code
     * @param args       args
     * @param message    message
     * @since 1.0.0
     */
    public SecurityException(IResultCode resultCode, Object[] args, String message) {
        super(resultCode, args, message);
    }


    /**
     * Security exception
     *
     * @param resultCode result code
     * @param args       args
     * @param message    message
     * @param cause      cause
     * @since 1.0.0
     */
    public SecurityException(IResultCode resultCode, Object[] args, String message, Throwable cause) {
        super(resultCode, args, message, cause);
    }

}
