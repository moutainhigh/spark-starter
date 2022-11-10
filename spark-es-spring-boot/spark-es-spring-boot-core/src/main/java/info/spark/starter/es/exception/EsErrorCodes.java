package info.spark.starter.es.exception;

import info.spark.starter.basic.annotation.ModelSerial;
import info.spark.starter.basic.annotation.SystemLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.15 13:24
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
@ModelSerial("ES")
public enum EsErrorCodes implements EsExceptionAssert {
    /** Server error message codes */
    @SystemLevel
    SERVER_ERROR(5000, EsBundle.message("server.error")),
    /** INDEX_NOT_FOUND */
    INDEX_NOT_FOUND(15000, EsBundle.message("index.not.found")),
    /** DATA_NOT_FOUNT */
    DATA_NOT_FOUNT(15001, EsBundle.message("data.not.fount")),
    /** PRIMARY_KEY_EMPTY_VALUE */
    PRIMARY_KEY_EMPTY_VALUE(15002, EsBundle.message("primary.key.empty.value")),
    /** PARAM_TYPE_ERROR */
    PARAM_TYPE_ERROR(15003, EsBundle.message("param.type.error")),
    /** METHOD_INVOKE_ERROR */
    METHOD_INVOKE_ERROR(15004, EsBundle.message("method.invoke.error")),
    /** TEMPLATE_INVOKE_ERROR */
    TEMPLATE_INVOKE_ERROR(15005, EsBundle.message("template.invoke.error")),
    /** METHOD_FIND_ERROR */
    METHOD_FIND_ERROR(15006, EsBundle.message("method.find.error")),
    /** Annotation not found es error codes */
    ANNOTATION_NOT_FOUND(15000, EsBundle.message("annotation.not.found"));

    /**
     * Value
     */
    private final Integer code;
    /**
     * Message
     */
    private final String message;

}

