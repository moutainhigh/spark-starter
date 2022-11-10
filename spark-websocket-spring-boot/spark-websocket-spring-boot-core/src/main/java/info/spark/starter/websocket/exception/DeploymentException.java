package info.spark.starter.websocket.exception;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 19:59
 * @since 2022.1.1
 */
public class DeploymentException extends Exception {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * Deployment exception
     *
     * @param message message
     * @since 2022.1.1
     */
    public DeploymentException(String message) {
        super(message);
    }

    /**
     * Deployment exception
     *
     * @param message message
     * @param cause   cause
     * @since 2022.1.1
     */
    public DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
