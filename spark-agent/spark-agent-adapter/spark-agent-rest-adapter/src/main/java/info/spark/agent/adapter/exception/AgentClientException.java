package info.spark.agent.adapter.exception;

import info.spark.agent.adapter.enums.AgentClientErrorCodes;
import info.spark.starter.basic.exception.BasicException;
import info.spark.starter.basic.support.StrFormatter;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.13 22:00
 * @since 1.0.0
 */
public class AgentClientException extends BasicException {
    /** serialVersionUID */
    private static final long serialVersionUID = 8342701486948855516L;

    /**
     * Instantiates a new Base exception.
     *
     * @since 1.5.1
     */
    public AgentClientException() {
        super(AgentClientErrorCodes.DEFAULT_ERROR_CODE, AgentClientErrorCodes.DEFAULT_MESSAGE);
    }

    /**
     * Instantiates a new Base exception.
     *
     * @param msg the msg
     * @since 1.5.1
     */
    public AgentClientException(String msg) {
        super(AgentClientErrorCodes.DEFAULT_ERROR_CODE, msg);
    }

    /**
     * Auth starter exception
     *
     * @param code code
     * @param msg  msg
     * @since 1.5.1
     */
    public AgentClientException(int code, String msg) {
        super(AgentClientErrorCodes.ERROR_CODE_PREFIX + code, msg);
    }

    /**
     * Auth client exception
     *
     * @param code 前缀 + code(int)
     * @param msg  msg
     * @since 1.6.0
     */
    public AgentClientException(String code, String msg) {
        super(code, msg);
    }

    /**
     * 直接使用枚举中的错误信息
     *
     * @param errorCode auth starter code
     * @since 1.5.1
     */
    public AgentClientException(@NotNull AgentClientErrorCodes errorCode) {
        super(AgentClientErrorCodes.ERROR_CODE_PREFIX + errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 使用 message 替换枚举中的 message 占位符
     *
     * @param errorCode error code
     * @param args      args
     * @since 1.6.0
     */
    public AgentClientException(@NotNull AgentClientErrorCodes errorCode, Object... args) {
        super(AgentClientErrorCodes.ERROR_CODE_PREFIX + errorCode.getCode(), StrFormatter.mergeFormat(errorCode.getMessage(), args));
    }

    /**
     * Auth starter exception
     *
     * @param errorCode auth starter code
     * @param cause     cause
     * @since 1.6.0
     */
    public AgentClientException(@NotNull AgentClientErrorCodes errorCode, Throwable cause) {
        super(AgentClientErrorCodes.ERROR_CODE_PREFIX + errorCode.getCode(), errorCode.getMessage(), cause);
    }

    /**
     * Instantiates a new Instance exception.
     *
     * @param cause the cause
     * @since 1.5.1
     */
    public AgentClientException(Throwable cause) {
        super(AgentClientErrorCodes.DEFAULT_ERROR_CODE, AgentClientErrorCodes.DEFAULT_MESSAGE, cause);
    }

    /**
     * Instantiates a new Instance exception.
     *
     * @param message the message
     * @param cause   the cause
     * @since 1.5.1
     */
    public AgentClientException(String message, Throwable cause) {
        super(AgentClientErrorCodes.DEFAULT_ERROR_CODE, message, cause);
    }

}
