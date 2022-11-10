package info.spark.agent.adapter.client;

import com.fasterxml.jackson.core.type.TypeReference;
import info.spark.starter.basic.Result;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.13 20:17
 * @since 1.6.0
 */
public interface AgentResult {

    /**
     * agent service 有返回值使用
     *
     * @param <T>   parameter
     * @param clazz clazz
     * @return the t
     * @since 1.6.0
     */
    <T> AgentOptional<T> expect(Class<T> clazz);

    /**
     * And return
     *
     * @param <T>          parameter
     * @param responseType response type
     * @return the t
     * @since 1.6.0
     */
    <T> AgentOptional<T> expect(TypeReference<T> responseType);

    /**
     * agent service 无返回值使用
     *
     * @return the result
     * @since 1.6.0
     */
    Result<?> expect();

}
