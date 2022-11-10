package info.spark.starter.openness.exception;

import info.spark.starter.basic.annotation.ModelSerial;
import info.spark.starter.basic.annotation.SystemLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.08.19 17:55
 * @since 1.9.0
 */
@Getter
@AllArgsConstructor
@ModelSerial("Openness")
public enum OpennessErrorCodes implements OpennessExceptionAssert {
    /** Server error openness error codes */
    @SystemLevel
    SERVER_ERROR(5000, OpennessBundle.message("server.error")),
    /** Header not found openness error codes */
    HEADER_NOT_FOUND(2401, OpennessBundle.message("header.not.found")),
    /** Param error openness error codes */
    PARAM_ERROR(2402, OpennessBundle.message("param.error")),
    /** No permission openness error codes */
    NO_PERMISSION(2403, OpennessBundle.message("no.permission")),
    /** Route error openness error codes */
    ROUTE_ERROR(2404, OpennessBundle.message("route.error")),
    /** Route error openness error codes */
    REQ_EXPIRED(2405, OpennessBundle.message("req.expired")),
    /** Not resubmit the request openness error codes */
    NOT_RESUBMIT_THE_REQUEST(2406, OpennessBundle.message("not.resubmit.the.request")),
    /** Signature error openness error codes */
    SIGNATURE_ERROR(2407, OpennessBundle.message("signature.error"));

    /** Code */
    private final Integer code;
    /** Message */
    private final String message;

}

