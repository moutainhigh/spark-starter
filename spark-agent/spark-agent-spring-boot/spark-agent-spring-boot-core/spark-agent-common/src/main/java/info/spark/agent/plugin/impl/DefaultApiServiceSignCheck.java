package info.spark.agent.plugin.impl;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import info.spark.agent.SecretCacheService;
import info.spark.agent.constant.AgentConstant;
import info.spark.agent.entity.ApiServiceHeader;
import info.spark.agent.entity.ApiServiceRequest;
import info.spark.agent.exception.AgentCodes;
import info.spark.agent.enums.SignType;
import info.spark.agent.exception.AgentServiceException;
import info.spark.agent.plugin.ApiServiceSignCheck;
import info.spark.agent.util.SignUtils;
import info.spark.starter.basic.util.JsonUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.18 20:20
 * @since 1.6.0
 */
@Slf4j
public class DefaultApiServiceSignCheck implements ApiServiceSignCheck {
    /** Secret cache service */
    private final SecretCacheService secretCacheService;
    /** Enable sign check */
    private final boolean enableSignCheck;

    /**
     * Default api service sign check
     *
     * @param secretCacheService secret cache service
     * @param enableSignCheck    enable sign check
     * @since 1.6.0
     */
    @Contract(pure = true)
    public DefaultApiServiceSignCheck(SecretCacheService secretCacheService,
                                      boolean enableSignCheck) {
        this.secretCacheService = secretCacheService;
        this.enableSignCheck = enableSignCheck;
    }

    /**
     * Check
     *
     * @param apiServiceHeader  api service header
     * @param apiServiceRequest api service request
     * @throws AgentServiceException agent service exception
     * @since 1.6.0
     */
    @Override
    public void check(@NotNull ApiServiceHeader apiServiceHeader, ApiServiceRequest apiServiceRequest) throws AgentServiceException {
        if (this.enableSignCheck) {
            // 需要签名的接口必须传 clientId
            String clientId = apiServiceHeader.getHeaders().get(AgentConstant.X_AGENT_APPID);
            AgentCodes.CLIENT_ID_ABSENT.notBlank(clientId);

            // 从缓存中获取对应的 secret, 如果没有则使用 SecretService.load() 加载, 具体逻辑由业务端实现
            String secret = this.secretCacheService.get(clientId);

            SignType type = SignType.PATTERN_MAP;
            String signType = apiServiceHeader.getHeaders().get(AgentConstant.X_AGENT_SIGNATURE_TYPE);
            if (signType != null) {
                type = Enum.valueOf(SignType.class, signType);
            }

            boolean verifySuccess;
            if (type.equals(SignType.PATTERN_MAP)) {
                // 如果不是 json 格式的参数则不计入签名
                Map<Object, Object> formParam = Collections.emptyMap();
                if (JsonUtils.isJson(apiServiceRequest.getMessage())) {
                    try {
                        // 路径参数也会被判定为 json
                        formParam = JsonUtils.toMap(apiServiceRequest.getMessage(), Object.class, Object.class);
                    } catch (Exception e) {
                        if (e.getCause().getClass().isAssignableFrom(MismatchedInputException.class)) {
                            try {
                                List<Object> list = JsonUtils.toList(apiServiceRequest.getMessage(), Object.class);

                                Map<Object, Object> map = new HashMap<>(8);
                                for (int i = 0; i < list.size(); i++) {
                                    map.put(i, list.get(i));
                                }

                                formParam = map;
                            } catch (Exception ex) {
                                // 路径参数时
                                formParam = null;
                            }
                        }
                    }
                }

                verifySuccess = SignUtils.verify(
                    secret,
                    apiServiceHeader.getHeaders(),
                    apiServiceHeader.getPath(),
                    null,
                    formParam);

            } else {
                verifySuccess = SignUtils.verify(apiServiceHeader.getMethod(),
                                                 secret,
                                                 apiServiceHeader.getHeaders(),
                                                 apiServiceHeader.getPath(),
                                                 apiServiceRequest.getMessage());

            }

            AgentCodes.SIGNATURE_ERROR.isTrue(verifySuccess);
        }
    }
}
