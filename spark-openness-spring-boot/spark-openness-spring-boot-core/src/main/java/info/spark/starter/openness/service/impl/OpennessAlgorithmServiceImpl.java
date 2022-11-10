package info.spark.starter.openness.service.impl;

import info.spark.starter.basic.util.StringPool;
import info.spark.starter.openness.entity.SignEntity;
import info.spark.starter.util.StringUtils;
import info.spark.starter.core.util.WebUtils;
import info.spark.starter.openness.constant.Constant;
import info.spark.starter.openness.entity.HandlerEntry;
import info.spark.starter.openness.exception.OpennessErrorCodes;
import info.spark.starter.openness.handler.ISecretAuthHandler;
import info.spark.starter.openness.service.AbstractOpennessAlgorithm;
import info.spark.starter.openness.utils.OpennessSignUtils;
import info.spark.starter.validation.util.RegexUtils;

import org.springframework.http.MediaType;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author zhubo
 * @version 1.6.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.18 17:27
 * @since 1.6.0
 */
@Slf4j
public class OpennessAlgorithmServiceImpl extends AbstractOpennessAlgorithm {
    /** Nonce length */
    private final Integer nonceLength;

    /**
     * Openness algorithm service
     *
     * @param secretAuthHandler secretAuthHandler
     * @param timeInterval      time interval
     * @param nonceLength       nonce length
     * @since 1.6.0
     */
    public OpennessAlgorithmServiceImpl(ISecretAuthHandler secretAuthHandler, Long timeInterval,
                                        Integer nonceLength) {
        super(secretAuthHandler, timeInterval);
        this.nonceLength = nonceLength;
    }

    /**
     * After check
     *
     * @param httpServletRequest http servlet request
     * @param handlerEntry       handler entry
     * @since 1.9.0
     */
    @Override
    protected void afterCheck(HttpServletRequest httpServletRequest, HandlerEntry handlerEntry) {
        OpennessErrorCodes.PARAM_ERROR.isTrue(this.nonceLength.equals(handlerEntry.getNonce().length()),
                                              String.format("请求头：%s，长度不符合限制，应为：%s", Constant.HEADER_NONCE, this.nonceLength));
        OpennessErrorCodes.PARAM_ERROR.isTrue(RegexUtils.match(RegexUtils.NUMBER, handlerEntry.getTimestamp()),
                                              String.format("请求头：%s，格式不正确，应为：时间戳", Constant.HEADER_TIMESTAMP));
        OpennessErrorCodes.PARAM_ERROR.isFalse(StringUtils.isBlank(httpServletRequest.getContentType()),
                                               String.format("请求头：%s，不能为空", "Content-Type"));
    }

    /**
     * Params md 5
     *
     * @param httpServletRequest http servlet request
     * @return the string
     * @since 1.9.0
     */
    private String paramsMd5(HttpServletRequest httpServletRequest) {
        if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(httpServletRequest.getContentType())) {
            return OpennessSignUtils.md5Params(WebUtils.getParameterMap(httpServletRequest), null);
        }
        return OpennessSignUtils.md5Params(WebUtils.getParameterMap(httpServletRequest), WebUtils.getBody(httpServletRequest));
    }

    /**
     * base64 ( AES加密byte[] (HTTP-Method + 参数MD5 +Content-Type + time + nonce + 资源url) )
     *
     * @param httpServletRequest http servlet request
     * @param handlerEntry       handler entry
     * @param secretKey          secret key
     * @return the string
     * @since 1.9.0
     */
    @Override
    public String encryption(HttpServletRequest httpServletRequest, HandlerEntry handlerEntry, String secretKey) {
        SignEntity signEntity = new SignEntity();
        signEntity.setHttpMethodType(httpServletRequest.getMethod());
        signEntity.setContentTypeStr(httpServletRequest.getContentType());
        signEntity.setTimestamp(handlerEntry.getTimestamp());
        signEntity.setNonce(handlerEntry.getNonce());
        signEntity.setUri(Optional.ofNullable(httpServletRequest.getRequestURI()).map(String::toUpperCase).orElse(StringPool.EMPTY));
        signEntity.setParamMd5(this.paramsMd5(httpServletRequest));
        signEntity.setSecretKey(secretKey);
        return OpennessSignUtils.sign(signEntity);

    }
}
