package info.spark.agent.adapter.client;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.07 23:13
 * @since 1.6.0
 */
public interface RequestBuilder {

    /**
     * Build request
     *
     * @return the agent request
     * @since 1.6.0
     */
    AgentRequest buildRequest();
}
