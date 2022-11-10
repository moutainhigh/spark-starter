package info.spark.starter.security.enums;

import info.spark.starter.basic.annotation.BusinessLevel;
import info.spark.starter.basic.annotation.ModelSerial;
import info.spark.starter.security.SparkSecurityBundle;
import info.spark.starter.security.assertion.SecurityExceptionAssert;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.19 12:59
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
@ModelSerial(modelName = "A")
public enum SecurityCodes implements SecurityExceptionAssert {

    /** Invalid request security codes */
    @BusinessLevel
    INVALID_REQUEST(40001, SparkSecurityBundle.message("invalid.request")),
    /** Invalid client security codes */
    @BusinessLevel
    INVALID_CLIENT(40002, SparkSecurityBundle.message("invalid.client")),
    /** Invalid grant security codes */
    @BusinessLevel
    INVALID_GRANT(40003, SparkSecurityBundle.message("invalid.grant")),
    /** Invalid scope security codes */
    @BusinessLevel
    INVALID_SCOPE(40004, SparkSecurityBundle.message("invalid.scope")),
    /** Invalid token security codes */
    @BusinessLevel
    INVALID_TOKEN(40005, SparkSecurityBundle.message("invalid.token")),
    /** User not found security codes */
    @BusinessLevel
    USER_NOT_FOUND(40006, SparkSecurityBundle.message("user.not.found")),
    /** User not enable security codes */
    @BusinessLevel
    USER_NOT_ENABLE(40007, SparkSecurityBundle.message("user.not.enable")),
    /** Role not found security codes */
    @BusinessLevel
    ROLE_NOT_FOUND(40008, SparkSecurityBundle.message("role.not.found")),
    /** Insufficient scope security codes */
    @BusinessLevel
    INSUFFICIENT_SCOPE(40013, SparkSecurityBundle.message("insufficient.scope")),
    /** Redirect uri mismatch */
    @BusinessLevel
    REDIRECT_URI_MISMATCH(40020, SparkSecurityBundle.message("redirect.uri.mismatch")),
    /** Access denied security codes */
    @BusinessLevel
    ACCESS_DENIED(40030, SparkSecurityBundle.message("access.denied")),
    /** Method not allowed security codes */
    @BusinessLevel
    METHOD_NOT_ALLOWED(40040, SparkSecurityBundle.message("method.not.allowed")),
    /** Server error security codes */
    @BusinessLevel
    SERVER_ERROR(40050, SparkSecurityBundle.message("server.error")),
    /** Unauthorized client security codes */
    @BusinessLevel
    UNAUTHORIZED_CLIENT(40060, SparkSecurityBundle.message("unauthorized.client")),
    /** Unauthorized security codes */
    @BusinessLevel
    UNAUTHORIZED(40061, SparkSecurityBundle.message("unauthorized")),
    /** Unsupported response type security codes */
    @BusinessLevel
    UNSUPPORTED_RESPONSE_TYPE(40070, SparkSecurityBundle.message("unsupported.response.type")),
    /** Unsupported grant type security codes */
    @BusinessLevel
    UNSUPPORTED_GRANT_TYPE(40071, SparkSecurityBundle.message("unsupported.grant.type")),
    /** Auth aspect error security codes */
    @BusinessLevel
    AUTH_ASPECT_ERROR(40073, SparkSecurityBundle.message("auth.aspect.error")),
    /** Auth request error security codes */
    @BusinessLevel
    AUTH_REQUEST_ERROR(40074, SparkSecurityBundle.message("auth.request.error")),
    /** Ignore url error */
    @BusinessLevel
    IGNORE_URL_ERROR(40078, SparkSecurityBundle.message("ignore.url.error")),
    /** Many login failed count security codes */
    @BusinessLevel
    MANY_LOGIN_FAILED_COUNT(40079, SparkSecurityBundle.message("many.login.failed.count")),
    /** Auth service error security codes */
    @BusinessLevel
    AUTH_SERVICE_ERROR(40080, SparkSecurityBundle.message("auth.service.error"));

    /** 返回码 */
    private final Integer code;
    /** 返回消息 */
    private final String message;
}
