package info.spark.agent.exception;

import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 18:05
 * @since 1.0.0
 */
public class AgentServiceException extends BaseException {
    /** serialVersionUID */
    private static final long serialVersionUID = -4922188950899034546L;

    /**
     * Instantiates a new Base exception.
     *
     * @since 1.0.0
     */
    public AgentServiceException() {
        super();
        this.resultCode = BaseCodes.FAILURE;
    }

    /**
     * Instantiates a new Base exception.
     *
     * @param cause cause
     * @since 1.0.0
     */
    public AgentServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Base exception.
     *
     * @param msg the msg
     * @since 1.0.0
     */
    public AgentServiceException(String msg) {
        super(msg);
        this.resultCode = BaseCodes.FAILURE;
    }

    /**
     * msg 占位符替换
     *
     * @param msg  msg
     * @param args args
     * @since 1.0.0
     */
    public AgentServiceException(String msg, Object... args) {
        super(msg, args);
    }

    /**
     * Instantiates a new Business exception.
     *
     * @param resultCode the result code
     * @since 1.0.0
     */
    public AgentServiceException(IResultCode resultCode) {
        super(resultCode);
    }

    /**
     * Base exception
     *
     * @param msg   msg
     * @param cause cause
     * @since 1.0.0
     */
    public AgentServiceException(String msg, Throwable cause) {
        super(msg, cause);
        this.resultCode = BaseCodes.FAILURE;
    }

    /**
     * Instantiates a new Business exception.
     *
     * @param resultCode the response enum
     * @param args       the args
     * @param message    the message
     * @since 1.0.0
     */
    public AgentServiceException(IResultCode resultCode, Object[] args, String message) {
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
    public AgentServiceException(IResultCode resultCode, Object[] args, String message, Throwable cause) {
        super(resultCode, args, message, cause);
    }
}
