package info.spark.starter.mongo.exception;

import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.04 10:09
 * @since 1.0.0
 */
public class MongoException extends BaseException {
    /** serialVersionUID */
    private static final long serialVersionUID = 4842002397836842293L;

    /**
     * Mongo exception
     *
     * @since 1.0.0
     */
    public MongoException() {
        super();
    }

    /**
     * Mongo exception
     *
     * @param message the message
     * @since 1.0.0
     */
    public MongoException(String message) {
        super(message);
    }

    /**
     * Mongo exception
     *
     * @param message the message
     * @param cause   the cause
     * @since 1.0.0
     */
    public MongoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * msg 占位符替换
     *
     * @param msg  msg
     * @param args args
     * @since 1.0.0
     */
    public MongoException(String msg, Object... args) {
        super(msg, args);
    }

    /**
     * Mongo exception
     *
     * @param cause the cause
     * @since 1.0.0
     */
    public MongoException(Throwable cause) {
        super(cause);
    }
}
