package info.spark.starter.sms.exception;

import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.01 10:22
 * @since 1.0.0
 */
public class SmsException extends BaseException {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new Base exception.
     *
     * @since 1.0.0
     */
    public SmsException() {
        super();
        this.resultCode = BaseCodes.FAILURE;
    }

    /**
     * Instantiates a new Base exception.
     *
     * @param msg the msg
     * @since 1.0.0
     */
    public SmsException(String msg) {
        super(msg);
        this.resultCode = BaseCodes.FAILURE;
    }

    /**
     * Instantiates a new Business exception.
     *
     * @param resultCode the result code
     * @since 1.0.0
     */
    public SmsException(IResultCode resultCode) {
        super(resultCode);
    }

    /**
     * Instantiates a new Business exception.
     *
     * @param resultCode the response enum
     * @param args       the args
     * @param message    the message
     * @since 1.0.0
     */
    public SmsException(IResultCode resultCode, Object[] args, String message) {
        super(resultCode, args, message);
    }

    /**
     * Instantiates a new Business exception.
     *
     * @param resultCode the response enum
     * @param args       the args
     * @param message    the message
     * @param cause      the cause
     * @since 1.0.0
     */
    public SmsException(IResultCode resultCode, Object[] args, String message, Throwable cause) {
        super(resultCode, args, message, cause);
    }
}
