package info.spark.agent.adapter.entity;

import info.spark.agent.adapter.client.AgentRequest;
import info.spark.starter.basic.Result;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.23 15:11
 * @since 1.7.0
 */
@Data
@Builder
public class AgentRecord implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -9193980918790690661L;
    /** 请求参数 */
    private AgentRequest agentRequest;
    /** 响应结果 */
    private Result<?> result;
    /** 请求时间 */
    private Date requestTime;
    /** 响应时间 */
    private Date responseTime;
}
