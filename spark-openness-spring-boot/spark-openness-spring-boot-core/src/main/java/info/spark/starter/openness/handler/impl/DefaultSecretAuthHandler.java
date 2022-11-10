package info.spark.starter.openness.handler.impl;

import info.spark.agent.adapter.client.AgentRequest;
import info.spark.agent.adapter.client.AgentTemplate;
import info.spark.starter.auth.constant.AuthConstant;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.openness.handler.ISecretAuthHandler;
import info.spark.starter.util.CollectionUtils;

import org.springframework.http.HttpMethod;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 默认 </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.08.19 16:00
 * @since 1.9.0
 */
@Slf4j
@AllArgsConstructor
public class DefaultSecretAuthHandler implements ISecretAuthHandler {

    /** Agent template */
    private final AgentTemplate agentTemplate;

    /**
     * Auth
     *
     * @param accessId access id
     * @return the string
     * @since 1.9.0
     */
    @Override
    public String secretKey(String accessId) {
        // 根据clientId获取到clientSecret, 并进行MD5加密, 再和auth匹配是否相等
        AgentRequest request = AgentRequest.builder()
            .serviceName(AuthConstant.ORIZATION_SERVICE_NAME)
            .apiName(AuthConstant.OAUTH_CLIENT)
            .params(accessId)
            .method(HttpMethod.GET)
            .build();
        Result<Object> result = this.agentTemplate.executeForResult(request, Object.class);
        if (result.isOk()) {
            @SuppressWarnings("unchecked")
            Map<String, String> resultData = (Map<String, String>) result.getData();
            if (CollectionUtils.isNotEmpty(resultData)) {
                return resultData.getOrDefault(AuthConstant.CLIENT_SECRET, StringPool.EMPTY);
            }
        }
        log.error("[openness-core] 获取 'client_secret' 失败，响应: [{}]", result.getMessage());
        return StringPool.EMPTY;
    }
}
