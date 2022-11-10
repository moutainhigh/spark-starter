package info.spark.agent.util;

import info.spark.agent.constant.SdkConstant;
import info.spark.agent.exception.AgentCodes;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.basic.util.IoUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.util.DigestUtils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 请求签名 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 10:03
 * @since 1.0.0
 */
@Slf4j
public class SignUtils extends info.spark.agent.utils.SignUtils {

    /**
     * 签名校验: 直接使用 data 数据进行签名
     *
     * @param method            method
     * @param secret            secret
     * @param headersParams     headers params
     * @param pathWithParameter path with parameter
     * @param data              data
     * @return boolean boolean
     * @since 1.6.0
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static boolean verify(String method,
                                 String secret,
                                 @NotNull Map<String, String> headersParams,
                                 String pathWithParameter,
                                 byte[] data) {

        String signature = headersParams.get(SdkConstant.CLOUDAPI_X_AGENT_SIGNATURE);

        AgentCodes.SIGNATURE_ABSENT.notBlank(signature);

        try {
            // 构造验签字符串
            String toVerify = buildStringToSign(method, headersParams, pathWithParameter, null, null, true);

            String signParams = SecureUtil.signParams(SmUtil.sm4(DigestUtil.md5(secret, Charsets.UTF_8_NAME)),
                                                      null,
                                                      StringPool.AMPERSAND,
                                                      StringPool.COLON,
                                                      true,
                                                      toVerify,
                                                      IoUtils.toString(data));


            boolean sign = DigestUtils.md5DigestAsHex(signParams.getBytes(Charsets.UTF_8)).equals(signature);

            log.debug("toVerify: \n[{}] sign: [{}]", toVerify, sign);
            return sign;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 签名校验: 使用 map 类型的参数, 不使用 method, 因为 agentTemplate 可能会修改原始 method
     *
     * @param secret            secret
     * @param headersParams     headers params
     * @param pathWithParameter path with parameter
     * @param queryParams       query params
     * @param formParam         form param
     * @return boolean boolean
     * @since 1.6.0
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static boolean verify(String secret,
                                 @NotNull Map<String, String> headersParams,
                                 String pathWithParameter,
                                 Map<String, String> queryParams,
                                 Map<Object, Object> formParam) {

        String signature = headersParams.get(SdkConstant.CLOUDAPI_X_AGENT_SIGNATURE);

        if (StringUtils.isBlank(signature)) {
            log.debug(AgentCodes.SIGNATURE_ABSENT.getMessage());
            return false;
        }

        return AgentCodes.SIGNATURE_ERROR.wrapper(() -> {
            // 构造验签字符串
            String toVerify = buildStringToSign("", headersParams, pathWithParameter, queryParams, formParam, true);

            String signParams = SecureUtil.signParams(SmUtil.sm4(DigestUtil.md5(secret, Charsets.UTF_8_NAME)),
                                                      formParam,
                                                      StringPool.AMPERSAND,
                                                      StringPool.COLON,
                                                      true,
                                                      toVerify);


            boolean sign = DigestUtils.md5DigestAsHex(signParams.getBytes(Charsets.UTF_8)).equals(signature);

            log.debug("toVerify: \n[{}] sign: [{}]", toVerify, sign);
            return sign;
        });
    }

}
