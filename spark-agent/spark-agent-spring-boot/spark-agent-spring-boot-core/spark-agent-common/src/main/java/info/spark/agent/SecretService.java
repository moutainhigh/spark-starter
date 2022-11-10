package info.spark.agent;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.19 11:10
 * @since 1.6.0
 */
@FunctionalInterface
public interface SecretService {

    /**
     * 业务端 agent 层实现此接口根据 clientId 获取对应的 secret
     *
     * @param clientId client id
     * @return the string
     * @since 1.6.0
     */
    String load(String clientId);
}
