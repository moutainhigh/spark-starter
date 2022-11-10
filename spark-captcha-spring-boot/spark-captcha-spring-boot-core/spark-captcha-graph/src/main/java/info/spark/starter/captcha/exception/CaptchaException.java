package info.spark.starter.captcha.exception;

import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:30
 * @since 1.0.0
 */
public class CaptchaException extends BaseException {

    /** serialVersionUID */
    private static final long serialVersionUID = 4137379170256213171L;

    /**
     * Instantiates a new Kaptcha exception.
     *
     * @since 1.0.0
     */
    public CaptchaException() {
        super();
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
    public CaptchaException(IResultCode resultCode, Object[] args, String message) {
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
    public CaptchaException(IResultCode resultCode, Object[] args, String message, Throwable cause) {
        super(resultCode, args, message, cause);
    }

}
