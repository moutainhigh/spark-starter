package info.spark.agent.entity;

import info.spark.starter.basic.context.AgentRequestContextHolder;

import org.jetbrains.annotations.Contract;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: 请求参数封装 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 05:10
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiServiceRequest implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 989136593693145344L;
    /** Id */
    private String id;
    /** Appid */
    private String appid;
    /** Api */
    private String api;
    /** Version */
    private String version;
    /** Nonce */
    private String nonce;
    /** Client ip */
    private String clientIp;
    /** Message */
    private byte[] message;
    /** Params */
    private String params;
    /** Request */

    private transient HttpServletRequest request;
    /** Response */
    private transient HttpServletResponse response;

    /**
     * Api service request
     *
     * @param id       id
     * @param appid    appid
     * @param api      api
     * @param version  version
     * @param nonce    nonce
     * @param clientIp client ip
     * @param message  message
     * @since 1.0.0
     */
    @Contract(pure = true)
    public ApiServiceRequest(String id,
                             String appid,
                             String api,
                             String version,
                             String nonce,
                             String clientIp,
                             byte[] message) {
        this.id = id;
        this.appid = appid;
        this.api = api;
        this.version = version;
        this.nonce = nonce;
        this.clientIp = clientIp;
        this.message = message;
    }

    /**
     * Get request
     *
     * @return the http servlet request
     * @since 1.8.0
     */
    public HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = AgentRequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();
    }
}
