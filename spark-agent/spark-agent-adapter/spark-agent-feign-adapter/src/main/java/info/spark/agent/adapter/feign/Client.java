package info.spark.agent.adapter.feign;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

import feign.HeaderMap;
import feign.Param;
import feign.RequestLine;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 05:14
 * @since 1.0.0
 */
public interface Client {

    /**
     * Execute object node
     *
     * @param headerMap header map
     * @param data      data
     * @return the object node
     * @since 1.0.0
     */
    @RequestLine("GET /")
    ObjectNode executeGet(@HeaderMap Map<String, Object> headerMap, @Param("data") String data);

    /**
     * Execute post object node
     *
     * @param headerMap header map
     * @param data      data
     * @return the object node
     * @since 1.0.0
     */
    @RequestLine("POST /")
    ObjectNode executePost(@HeaderMap Map<String, Object> headerMap, byte[] data);

    /**
     * Execute put object node
     *
     * @param headerMap header map
     * @param data      data
     * @return the object node
     * @since 1.0.0
     */
    @RequestLine("PUT /")
    ObjectNode executePut(@HeaderMap Map<String, Object> headerMap, byte[] data);

    /**
     * Execute delete object node
     *
     * @param headerMap header map
     * @param id        id
     * @return the object node
     * @since 1.0.0
     */
    @RequestLine("DELETE /{id}")
    ObjectNode executeDelete(@HeaderMap Map<String, Object> headerMap, @Param("id") String id);

    /**
     * Execute delete object node
     *
     * @param headerMap header map
     * @param data      data
     * @return the object node
     * @since 1.0.0
     */
    @RequestLine("DELETE /")
    ObjectNode executeDelete(@HeaderMap Map<String, Object> headerMap, byte[] data);

    /**
     * Time result
     *
     * @return the result
     * @since 1.0.0
     */
    @RequestLine("GET /time")
    ObjectNode time();

    /**
     * Ping string
     *
     * @return the string
     * @since 1.0.0
     */
    @RequestLine("GET /ping")
    ObjectNode ping();
}
