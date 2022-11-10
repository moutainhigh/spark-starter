package info.spark.agent.adapter.feign;

import info.spark.agent.adapter.constant.AgentApapterConstants;
import info.spark.feign.adapter.annotation.FeignClient;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 05:43
 * @since 1.0.0
 */
@FeignClient(name = AgentApapterConstants.THIRD_CENTER_NAME, agent = true)
public interface ThirdClient extends Client {
}
