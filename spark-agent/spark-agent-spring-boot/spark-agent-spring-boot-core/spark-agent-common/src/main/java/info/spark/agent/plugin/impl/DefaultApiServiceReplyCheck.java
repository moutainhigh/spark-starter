package info.spark.agent.plugin.impl;

import info.spark.agent.NonceCacheSevice;
import info.spark.agent.constant.AgentConstant;
import info.spark.agent.entity.ApiServiceHeader;
import info.spark.agent.entity.ApiServiceRequest;
import info.spark.agent.exception.AgentCodes;
import info.spark.agent.exception.AgentServiceException;
import info.spark.agent.plugin.ApiServiceReplayCheck;
import info.spark.starter.basic.util.StringUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 默认防重攻击实现: 如果开启, header 中必须存在 {@link AgentConstant#X_AGENT_NONCE},
 * 第一次请求会存到缓存中, 后面如果请求存在相同的 nonce, 则会抛出异常, nonce 缓存 15 分钟(可配置), 即 15 分钟内不同使用同一个 nonce 发起请求.
 * </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.19 14:46
 * @since 1.6.0
 */
public class DefaultApiServiceReplyCheck implements ApiServiceReplayCheck {

    /** Cache secret sevice */
    private final NonceCacheSevice nonceCacheSevice;
    /** Enable reply check */
    private final boolean enableReplyCheck;

    /**
     * Default api service reply check
     *
     * @param nonceCacheSevice cache secret sevice
     * @param enableReplyCheck enable reply check
     * @since 1.6.0
     */
    @Contract(pure = true)
    public DefaultApiServiceReplyCheck(NonceCacheSevice nonceCacheSevice, boolean enableReplyCheck) {
        this.nonceCacheSevice = nonceCacheSevice;
        this.enableReplyCheck = enableReplyCheck;
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
    public void check(@NotNull ApiServiceHeader apiServiceHeader,
                      @NotNull ApiServiceRequest apiServiceRequest) throws AgentServiceException {
        if (this.enableReplyCheck) {
            String nonce = apiServiceRequest.getNonce();
            // nonce 在 cache 里面找得到就不允许访问
            if (StringUtils.isEmpty(nonce) || StringUtils.isNotBlank(this.nonceCacheSevice.get(nonce))) {
                throw AgentCodes.REQUEST_NONCE_INVALID.newException();
            } else {
                this.nonceCacheSevice.set(apiServiceRequest.getNonce(), System.currentTimeMillis() + "");
            }
        }
    }
}
