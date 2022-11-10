package info.spark.feign.adapter.exception;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:17
 * @since 1.0.0
 */
public class InternalException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = -6221629532770917447L;

    /**
     * Instantiates a new Internal exception.
     *
     * @param msg the msg
     * @since 1.0.0
     */
    public InternalException(String msg) {
        super(msg);
    }
}
