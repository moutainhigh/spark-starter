package info.spark.agent.client;

import info.spark.agent.adapter.annotation.Client;

/**
 * <p>Description: 如果子接口添加 value, 将覆盖父接口的 serviceName </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.17 12:21
 * @since 1.7.1
 */
@Client
// @Client("sub-user-center")
public interface SubTestAgentClient extends TestAgentClient {
}
