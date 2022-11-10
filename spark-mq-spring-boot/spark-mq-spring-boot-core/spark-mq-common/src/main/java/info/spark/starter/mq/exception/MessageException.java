package info.spark.starter.mq.exception;

import info.spark.starter.basic.exception.BasicException;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 15:48
 * @since 1.5.0
 */
public class MessageException extends BasicException {
    /** serialVersionUID */
    private static final long serialVersionUID = -6498727260647427447L;

    /**
     * Message exception
     *
     * @since 1.5.0
     */
    public MessageException() {
        super();
    }

    /**
     * Message exception
     *
     * @param message message
     * @since 1.5.0
     */
    public MessageException(String message) {
        super(message);
    }

    /**
     * Message exception
     *
     * @param cause cause
     * @since 2.1.0
     */
    public MessageException(Throwable cause) {
        super(cause);
    }

    /**
     * Message exception
     *
     * @param message message
     * @param cause   cause
     * @since 2.1.0
     */
    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
