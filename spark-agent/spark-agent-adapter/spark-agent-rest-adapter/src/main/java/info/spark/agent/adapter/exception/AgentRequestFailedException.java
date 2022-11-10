package info.spark.agent.adapter.exception;

import info.spark.starter.basic.exception.BasicException;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.22 00:37
 * @since 1.7.0
 */
public class AgentRequestFailedException extends BasicException {

    private static final long serialVersionUID = 2799346636818636276L;

    /**
     * Agent request failed exception
     *
     * @param code    code
     * @param message message
     * @since 1.7.0
     */
    public AgentRequestFailedException(String code, String message) {
        super(code, message);
    }
}
