package info.spark.agent.adapter;

import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotBlank;

/**
 * <p>Description: SDK 参数检查时, 使用此接口作为切入点 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.05.19 16:34
 * @since 1.9.0
 */
public interface IClient {
    /**
     * api 前缀
     *
     * @return the string
     * @since 1.9.0
     */
    default String root() {
        throw new UnsupportedOperationException("不再需要使用此接口");
    }

    /**
     * Function
     *
     * @param functionName function name
     * @return the string
     * @since 1.9.0
     */
    default String function(@NotBlank String functionName) {
        LoggerFactory.getLogger(IClient.class).error("不再需要 function 接口, 直接写入 ApiServiceMethod.code 的 value 即可");
        return functionName;
    }
}
