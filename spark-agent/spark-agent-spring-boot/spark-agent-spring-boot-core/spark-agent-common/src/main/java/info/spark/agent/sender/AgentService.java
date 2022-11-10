package info.spark.agent.sender;

import info.spark.agent.constant.AgentConstant;
import info.spark.agent.core.ApiServiceContext;
import info.spark.agent.entity.ApiExtend;
import info.spark.agent.entity.ApiServiceHeader;
import info.spark.agent.entity.ApiServiceRequest;
import info.spark.agent.exception.AgentServiceException;
import info.spark.agent.sender.impl.EmbeddedAgentServiceImpl;
import info.spark.starter.basic.context.ExpandIds;
import info.spark.starter.basic.context.ExpandIdsContext;
import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.basic.util.StringUtils;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 数据发送接口, 由业务方选择(本地{@link EmbeddedAgentServiceImpl}或者远程(暂未实现))或实现 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 05:07
 * @since 1.0.0
 */
public interface AgentService {

    /**
     * Send byte [ ]
     *
     * @param apiServiceRequest api service request
     * @return the byte [ ]
     * @since 1.0.0
     */
    byte[] send(ApiServiceRequest apiServiceRequest);

    /**
     * 根据是否存在指定的 header 构建 ApiExtend, 便于 agent 层直接获取相应字段.
     *
     * @param apiServiceRequest api service request
     * @param apiServiceHeader  api service header
     * @return the api extend
     * @since 1.8.0
     */
    default ApiExtend buildApiExtend(@NotNull ApiServiceRequest apiServiceRequest,
                                     @NotNull ApiServiceHeader apiServiceHeader) {
        // 8.11 iot开发 发现 tenant_id 入库参数有些数据异常，遂在请求时添加 remove 操作。
        ApiExtend apiExtend = ApiExtend.builder()
            .request(apiServiceRequest)
            .header(apiServiceHeader)
            .expandId(new ExpandIds())
            .build();

        Map<String, String> headers = apiServiceHeader.getHeaders();
        if (StringUtils.isNotBlank(headers.get(AgentConstant.X_AGENT_TENANTID))) {
            String tenantId = headers.get(AgentConstant.X_AGENT_TENANTID);
            try {
                apiExtend.getExpandId().setTenantId(Long.parseLong(tenantId));
            } catch (Exception e) {
                throw new AgentServiceException(StrFormatter.format("错误的类型转换: {} 无法转换为 Long", tenantId));
            }
        }

        if (StringUtils.isNotBlank(headers.get(AgentConstant.X_AGENT_APPID))) {
            apiExtend.getExpandId().setClientId(headers.get(AgentConstant.X_AGENT_APPID));
        }
        // 将解析到的ID们放入线程上下文中
        ExpandIdsContext.context().set(apiExtend.getExpandId());
        return apiExtend;
    }

    /**
     * Send byte [ ]
     *
     * @param apiServiceRequest api service request
     * @param apiServiceHeader  api service header
     * @return the byte [ ]
     * @since 1.0.0
     */
    byte[] send(@NotNull ApiServiceRequest apiServiceRequest, @NotNull ApiServiceHeader apiServiceHeader);

    /**
     * Ready *
     *
     * @param apiServiceContext api service context
     * @since 1.0.0
     */
    void ready(ApiServiceContext apiServiceContext);

    /**
     * Shutdown
     *
     * @since 1.0.0
     */
    void shutdown();
}
