package info.spark.starter.id.exception;

import info.spark.starter.basic.exception.BasicException;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.29 16:50
 * @since 1.5.0
 */
public class IdWorkerException extends BasicException {

    private static final long serialVersionUID = -4445192684555401929L;

    /**
     * Instantiates a new Base exception.
     *
     * @param msg the msg
     * @since 1.0.0
     */
    public IdWorkerException(String msg) {
        super(msg);
    }
}
