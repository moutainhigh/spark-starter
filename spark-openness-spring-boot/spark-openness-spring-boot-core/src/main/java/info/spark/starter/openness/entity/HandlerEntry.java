package info.spark.starter.openness.entity;

import info.spark.starter.util.StringUtils;
import info.spark.starter.openness.constant.Constant;
import info.spark.starter.openness.exception.OpennessErrorCodes;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.08.19 11:57
 * @since 1.9.0
 */
@Data
public class HandlerEntry implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = -6385231918394958124L;

    /** Sign */
    private String sign;
    /** Client id */
    private String clientId;
    /** Nonce */
    private String nonce;
    /** Timestamp */
    private String timestamp;

    /**
     * Transformation
     *
     * @param request request
     * @return the handler entry
     * @since 1.9.0
     */
    public static HandlerEntry transformation(HttpServletRequest request) {
        HandlerEntry handlerEntry = new HandlerEntry();
        handlerEntry.setSign(extracted(request, Constant.HEADER_SIGN, true));
        handlerEntry.setClientId(extracted(request, Constant.HEADER_CLIENT_ID, true));
        handlerEntry.setNonce(extracted(request, Constant.HEADER_NONCE, true));
        handlerEntry.setTimestamp(extracted(request, Constant.HEADER_TIMESTAMP, true));
        return handlerEntry;
    }

    /**
     * Extracted
     *
     * @param request request
     * @param name    name
     * @param must    must
     * @return the string
     * @since 1.9.0
     */
    private static String extracted(HttpServletRequest request, String name, boolean must) {
        if (must) {
            OpennessErrorCodes.HEADER_NOT_FOUND.isFalse(StringUtils.isBlank(request.getHeader(name)),
                                                        StringUtils.format("header 不存在 {}", name));
        }
        return request.getHeader(name);
    }
}
