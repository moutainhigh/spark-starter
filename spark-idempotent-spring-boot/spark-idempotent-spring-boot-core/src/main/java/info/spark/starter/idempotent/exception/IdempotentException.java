package info.spark.starter.idempotent.exception;

import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.22 15:30
 * @since 1.6.0
 * @since 1.6.0
 */
public class IdempotentException extends BaseException {

    private static final long serialVersionUID = 2427480391620890081L;

    /**
     * Idempotent exception
     *
     * @param message message
     * @since 1.6.0
     */
    public IdempotentException(String message) {
        super(message);
    }
}
