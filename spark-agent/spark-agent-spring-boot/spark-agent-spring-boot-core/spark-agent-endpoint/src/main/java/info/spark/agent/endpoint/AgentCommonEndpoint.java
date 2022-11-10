package info.spark.agent.endpoint;

import info.spark.agent.constant.AgentConstant;
import info.spark.starter.basic.util.JsonUtils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.24 14:08
 * @since 1.4.0
 */
@SuppressWarnings("checkstyle:ParameterNumber")
public interface AgentCommonEndpoint extends AgentHandler {

    /**
     * 通过 id 查询数据统一入口
     *
     * @param api             api
     * @param version         version
     * @param appid           appid
     * @param timestamp       timestamp
     * @param sign            sign
     * @param nonce           nonce
     * @param signatureHeader signature header
     * @param charset         charset
     * @param id              id
     * @return the byte [ ]
     * @since 1.0.0
     */
    @GetMapping(value = AgentConstant.ROOT_ENDPOINT + "/{id}",
                consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    default byte[] doGetById(@RequestHeader(value = AgentConstant.X_AGENT_API) String api,
                             @RequestHeader(value = AgentConstant.X_AGENT_VERSION, required = false) String version,
                             @RequestHeader(value = AgentConstant.X_AGENT_APPID, required = false) String appid,
                             @RequestHeader(value = AgentConstant.X_AGENT_TIMESTAMP, required = false) Long timestamp,
                             @RequestHeader(value = AgentConstant.X_AGENT_SIGNATURE, required = false) String sign,
                             @RequestHeader(value = AgentConstant.X_AGENT_NONCE, required = false) String nonce,
                             @RequestHeader(value = AgentConstant.X_AGENT_SIGNATURE_HEADERS, required = false) String signatureHeader,
                             @RequestHeader(value = AgentConstant.X_AGENT_CHARSET, required = false) String charset,
                             @PathVariable String id) {

        return this.doGet(api,
                          version,
                          appid,
                          timestamp,
                          sign,
                          nonce,
                          signatureHeader,
                          charset,
                          Base64.encodeBase64URLSafeString(JsonUtils.toJsonAsBytes(id)));
    }

    /**
     * 通过 get 查询数据统一入口
     *
     * @param api             api
     * @param version         version
     * @param appid           appid
     * @param timestamp       timestamp
     * @param sign            sign
     * @param nonce           nonce
     * @param signatureHeader signature header
     * @param charset         charset
     * @param data            data
     * @return the access token
     * @since 1.0.0
     */
    @GetMapping(value = AgentConstant.ROOT_ENDPOINT,
                consumes = {MediaType.APPLICATION_JSON_VALUE,
                            MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    default byte[] doGet(@RequestHeader(value = AgentConstant.X_AGENT_API) String api,
                         @RequestHeader(value = AgentConstant.X_AGENT_VERSION, required = false) String version,
                         @RequestHeader(value = AgentConstant.X_AGENT_APPID, required = false) String appid,
                         @RequestHeader(value = AgentConstant.X_AGENT_TIMESTAMP, required = false) Long timestamp,
                         @RequestHeader(value = AgentConstant.X_AGENT_SIGNATURE, required = false) String sign,
                         @RequestHeader(value = AgentConstant.X_AGENT_NONCE, required = false) String nonce,
                         @RequestHeader(value = AgentConstant.X_AGENT_SIGNATURE_HEADERS, required = false) String signatureHeader,
                         @RequestHeader(value = AgentConstant.X_AGENT_CHARSET, required = false) String charset,
                         @RequestParam(value = AgentConstant.GET_PARAM_NAME, required = false) String data) {

        return this.handle(api,
                           version,
                           appid,
                           timestamp,
                           sign,
                           nonce,
                           signatureHeader,
                           charset,
                           HttpMethod.GET,
                           Base64.decodeBase64(data));
    }

    /**
     * post, put 和 patch 请求统一入口, 请求数据全部在 body 中.
     *
     * @param request         request
     * @param api             API名称
     * @param version         API版本
     * @param appid           产品标识
     * @param timestamp       时间戳 毫秒
     * @param sign            签名值
     * @param nonce           请求唯一标识
     * @param signatureHeader signature header
     * @param charset         请求的编码方式
     * @param data            请求包
     * @return the response entity
     * @since 1.7.0
     */
    @RequestMapping(value = AgentConstant.ROOT_ENDPOINT,
                    method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH},
                    consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    default byte[] doService(HttpServletRequest request,
                             @RequestHeader(value = AgentConstant.X_AGENT_API) String api,
                             @RequestHeader(value = AgentConstant.X_AGENT_VERSION, required = false) String version,
                             @RequestHeader(value = AgentConstant.X_AGENT_APPID, required = false) String appid,
                             @RequestHeader(value = AgentConstant.X_AGENT_TIMESTAMP, required = false) Long timestamp,
                             @RequestHeader(value = AgentConstant.X_AGENT_SIGNATURE, required = false) String sign,
                             @RequestHeader(value = AgentConstant.X_AGENT_NONCE, required = false) String nonce,
                             @RequestHeader(value = AgentConstant.X_AGENT_SIGNATURE_HEADERS, required = false) String signatureHeader,
                             @RequestHeader(value = AgentConstant.X_AGENT_CHARSET, required = false) String charset,
                             @RequestBody byte[] data) {

        HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());

        return this.handle(api,
                           version,
                           appid,
                           timestamp,
                           sign,
                           nonce,
                           signatureHeader,
                           charset,
                           httpMethod == null ? HttpMethod.POST : httpMethod,
                           data);
    }

    /**
     * 通过 id 删除数据统一入口
     *
     * @param version         version
     * @param api             api
     * @param appid           appid
     * @param timestamp       timestamp
     * @param sign            sign
     * @param nonce           nonce
     * @param signatureHeader signature header
     * @param charset         charset
     * @param id              id
     * @return the result
     * @since 1.0.0
     */
    @DeleteMapping(value = AgentConstant.ROOT_ENDPOINT + "/{id}",
                   consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    default byte[] doDeleteById(@RequestHeader(value = AgentConstant.X_AGENT_API) String api,
                                @RequestHeader(value = AgentConstant.X_AGENT_VERSION, required = false) String version,
                                @RequestHeader(value = AgentConstant.X_AGENT_APPID, required = false) String appid,
                                @RequestHeader(value = AgentConstant.X_AGENT_TIMESTAMP, required = false) Long timestamp,
                                @RequestHeader(value = AgentConstant.X_AGENT_SIGNATURE, required = false) String sign,
                                @RequestHeader(value = AgentConstant.X_AGENT_NONCE, required = false) String nonce,
                                @RequestHeader(value = AgentConstant.X_AGENT_SIGNATURE_HEADERS, required = false) String signatureHeader,
                                @RequestHeader(value = AgentConstant.X_AGENT_CHARSET, required = false) String charset,
                                @PathVariable String id) {

        return this.doDelete(api,
                             version,
                             appid,
                             timestamp,
                             sign,
                             nonce,
                             signatureHeader,
                             charset,
                             JsonUtils.toJsonAsBytes(id));
    }

    /**
     * 删除多个 id, 入参 data 实际应该是 {@code List<Long>} 的 byte[]
     *
     * @param api             api
     * @param version         version
     * @param appid           appid
     * @param timestamp       timestamp
     * @param sign            sign
     * @param nonce           nonce
     * @param signatureHeader signature header
     * @param charset         charset
     * @param data            data
     * @return the byte [ ]
     * @since 1.0.0
     */
    @DeleteMapping(value = AgentConstant.ROOT_ENDPOINT,
                   consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    default byte[] doDelete(@RequestHeader(value = AgentConstant.X_AGENT_API) String api,
                            @RequestHeader(value = AgentConstant.X_AGENT_VERSION, required = false) String version,
                            @RequestHeader(value = AgentConstant.X_AGENT_APPID, required = false) String appid,
                            @RequestHeader(value = AgentConstant.X_AGENT_TIMESTAMP, required = false) Long timestamp,
                            @RequestHeader(value = AgentConstant.X_AGENT_SIGNATURE, required = false) String sign,
                            @RequestHeader(value = AgentConstant.X_AGENT_NONCE, required = false) String nonce,
                            @RequestHeader(value = AgentConstant.X_AGENT_SIGNATURE_HEADERS, required = false) String signatureHeader,
                            @RequestHeader(value = AgentConstant.X_AGENT_CHARSET, required = false) String charset,
                            @RequestBody byte[] data) {

        return this.handle(api,
                           version,
                           appid,
                           timestamp,
                           sign,
                           nonce,
                           signatureHeader,
                           charset,
                           HttpMethod.DELETE,
                           data);
    }

}
