package info.spark.starter.idempotent.service;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description:  </p>
 *
 * @author liujintao
 * @version 1.0.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.21 10:16
 * @since 1.0.0
 */
public interface TokenService {
    /** HEADER_TOKEN_NAME */
    String HEADER_TOKEN_NAME = "X-Idempotent-Token";
    /** PARAM_TOKEN_NAME */
    String PARAM_TOKEN_NAME = "idempotentToken";
    /** TOKEN_NAME */
    String TOKEN_NAME = "idempotent:token:";

    /**
     * Create token
     *
     * @return the string
     * @since 1.0.0
     */
    String createToken();

    /**
     * Check token
     *
     * @param request request
     * @throws Exception exception
     * @since 1.0.0
     */
    void checkToken(HttpServletRequest request) throws Exception;
}
