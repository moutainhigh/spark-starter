package info.spark.agent.adapter.enums;

import info.spark.agent.adapter.AgentBundle;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author zhubo
 * @version 1.5.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.30 16:28
 * @since 1.5.0
 */
@Getter
@AllArgsConstructor
public enum AgentClientErrorCodes implements Serializable {
    /** Param verify error agent client error codes */
    PARAM_VERIFY_ERROR(4100, AgentBundle.message("sdk.param.verify.error")),
    /** No service name error agent client error codes */
    NO_SERVICE_NAME_ERROR(10200, AgentBundle.message("no.service.name.error")),
    /** Parameter errorr agent client error code */
    PARAMETER_TYPE_ERRORR(10201, AgentBundle.message("not.support.both.params.and.pathVariable.errorr")),
    /** Method parameter errorr agent client error codes */
    METHOD_PARAMETER_ERRORR(10203, AgentBundle.message("only.get.and.delete.support.path.params")),
    /** Post parameter errorr agent client error codes */
    POST_PARAMETER_ERRORR(10204, AgentBundle.message("post.params.error")),
    /** Result type errorr agent client error codes */
    RESULT_TYPE_ERRORR(10205, AgentBundle.message("result.type.error"));

    /** ERROR_CODE_PREFIX */
    public static final String ERROR_CODE_PREFIX = "B.C-";
    /** DEFAULT_ERROR_CODE */
    public static final String DEFAULT_ERROR_CODE = ERROR_CODE_PREFIX + "5000";
    /** DEFAULT_MESSAGE */
    public static final String DEFAULT_MESSAGE = "Agent Client 错误";

    /** 错误类型码 */
    private final Integer code;
    /** 错误类型描述信息 */
    private final String message;
}
