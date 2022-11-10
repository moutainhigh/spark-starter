package info.spark.starter.schedule.exception;

import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.23 15:35
 * @since 1.4.0
 */
public class ScheduleException extends BaseException {

    /** serialVersionUID */
    private static final long serialVersionUID = -7899994350628870322L;

    /**
     * Properties exception
     *
     * @param msg  msg
     * @param args args
     * @since 1.0.0
     */
    public ScheduleException(String msg, Object... args) {
        super(msg, args);
    }

    /**
     * Properties exception
     *
     * @param cause cause
     * @since 1.0.0
     */
    public ScheduleException(Throwable cause) {
        super(cause);
    }
}
