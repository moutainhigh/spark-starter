package info.spark.starter.retry.exception;

import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.01 10:22
 * @since 1.5.0
 */
public class SparkRetryException extends BaseException {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new Base exception.
     *
     * @param msg the msg
     * @since 1.5.0
     */
    public SparkRetryException(String msg) {
        super(msg);
    }

    /**
     * Spark retry exception
     *
     * @param cause cause
     * @since 1.5.0
     */
    public SparkRetryException(Throwable cause) {
        super(cause);
    }

}
