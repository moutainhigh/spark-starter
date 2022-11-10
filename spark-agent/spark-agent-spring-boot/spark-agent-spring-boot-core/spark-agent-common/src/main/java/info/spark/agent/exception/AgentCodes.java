package info.spark.agent.exception;

import info.spark.agent.AgentBundle;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.18 22:55
 * @since 1.6.0
 */
@Getter
@AllArgsConstructor
public enum AgentCodes implements AgentExceptionAssert {

    /** Param error agent codes */
    PARAM_ERROR(40000, AgentBundle.message("agent.code.params.error")),
    /** Save or update error agent codes */
    SAVE_OR_UPDATE_ERROR(40002, AgentBundle.message("agent.code.save.or.update.failure")),
    /** Data error agent codes */
    DATA_ERROR(40005, AgentBundle.message("agent.code.data.error")),
    /** Signature absent agent codes */
    SIGNATURE_ABSENT(44444, AgentBundle.message("agent.code.signature.absent")),
    /** Signature error agent codes */
    SIGNATURE_ERROR(44445, AgentBundle.message("agent.code.signature.error")),
    /** Client id absent agent codes */
    CLIENT_ID_ABSENT(44446, AgentBundle.message("agent.code.client.id.absent")),
    /** Client id absent agent codes */
    REQUEST_NONCE_INVALID(44447, AgentBundle.message("agent.code.request.nonce.invalid")),
    /** Invoker timeout agent codes */
    INVOKER_TIMEOUT(44448, AgentBundle.message("agent.code.request.timeout")),
    /** Invoker error agent codes */
    INVOKER_ERROR(44449, AgentBundle.message("agent.code.request.error")),
    /** Expand ids absent agent codes */
    EXPAND_IDS_ABSENT(44450, AgentBundle.message("agent.code.expand.ids.absent"));
    /** 返回码 */
    private final Integer code;
    /** 返回消息 */
    private final String message;
}
