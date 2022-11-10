package info.spark.agent.adapter.client;

import com.fasterxml.jackson.core.type.TypeReference;
import info.spark.agent.adapter.exception.AgentClientException;
import info.spark.starter.basic.Result;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestOperations;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.28 00:00
 * @since 1.0.0
 */
public interface AgentOperations extends RestOperations {

    /**
     * 返回 {@code Result<T>} 中的 T
     *
     * @param <T>          parameter
     * @param request      request
     * @param responseType response type
     * @return the for object
     * @throws AgentClientException     agent client exception
     * @throws IllegalArgumentException illegal argument exception
     * @since 1.0.0
     */
    <T> T executeForObject(AgentRequest request, Class<T> responseType) throws AgentClientException, IllegalArgumentException;

    /**
     * 返回 {@code Result<T>} 中的 T
     *
     * @param <T>          parameter
     * @param request      request
     * @param responseType response type
     * @return the t
     * @throws AgentClientException     agent client exception
     * @throws IllegalArgumentException illegal argument exception
     * @since 1.0.0
     */
    <T> T executeForObject(AgentRequest request, TypeReference<?> responseType) throws AgentClientException, IllegalArgumentException;

    /**
     * 返回 {@code Result<T>} 中的 T
     *
     * @param <T>          parameter
     * @param request      request
     * @param responseType response type
     * @return the t
     * @throws AgentClientException agent client exception
     * @since 1.0.0
     */
    <T> T executeForObject(@NotNull AgentRequest request, ParameterizedTypeReference<T> responseType) throws AgentClientException;

    /**
     * 返回 {@code Result<T>}
     *
     * @param <T>          parameter
     * @param request      request
     * @param responseType response type
     * @return the for entity
     * @throws AgentClientException     agent client exception
     * @throws IllegalArgumentException illegal argument exception
     * @since 1.0.0
     */
    <T> Result<T> executeForResult(AgentRequest request, Class<T> responseType) throws AgentClientException, IllegalArgumentException;

    /**
     * 返回 {@code Result<T>}
     *
     * @param <T>          parameter
     * @param request      request
     * @param responseType response type
     * @return the result
     * @throws AgentClientException     agent client exception
     * @throws IllegalArgumentException illegal argument exception
     * @since 1.0.0
     */
    <T> Result<T> executeForResult(AgentRequest request, @NotNull TypeReference<?> responseType) throws AgentClientException,
                                                                                                        IllegalArgumentException;

}
