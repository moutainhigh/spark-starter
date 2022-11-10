package info.spark.agent.adapter.client;

import info.spark.agent.constant.AgentConstant;

import info.spark.agent.adapter.annotation.Client;

import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.20 10:26
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AgentRequest implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -4918846315160377275L;
    /** Headers */
    @Builder.Default
    private final Map<String, String> headers = new HashMap<>(8);
    /** Header */
    private String header;
    /** 服务名(需要与 spark-gateway 注册的路由一致 [predicates.args.pattern]) */
    private String serviceName;
    /** 请求的接口名 */
    private String apiName;
    /** 接口版本, 默认 1.0.0 */
    @Builder.Default
    private String version = AgentConstant.DEFAULT_VERSION;
    /** 请求参数(全部为 json 格式) */
    private Object params;
    /** path 参数 */
    private String pathVariable;
    /** Method */
    private HttpMethod method;
    /** 数据长度 */
    private Integer dataLength;
    /** 是否使用 sdk */
    @Builder.Default
    private Boolean useSdk = false;
    /** 是否需要签名 */
    @Builder.Default
    private boolean signature = false;

    /**
     * 通过 {@link Client} 透传的自定义 url
     *
     * @since 1.7.0
     */
    private String customEndpoint;

    /**
     * Get header
     *
     * @param header header
     * @return the string
     * @since 1.5.0
     */
    public String getHeader(String header) {
        return this.headers.get(header);
    }

    /**
     * Gets headers *
     *
     * @return the headers
     * @since 1.6.0
     */
    public Map<String, String> getHeaders() {
        this.headers.putIfAbsent(AgentConstant.X_AGENT_VERSION, this.version);
        this.headers.putIfAbsent(AgentConstant.X_AGENT_API, this.apiName);
        this.headers.putIfAbsent("Accept", "application/json");
        this.headers.putIfAbsent("Content-Type", "application/json");
        return this.headers;
    }

    /**
     * Header
     *
     * @param header header
     * @param value  value
     * @return the agent request
     * @since 1.6.0
     */
    public AgentRequest header(String header, String value) {
        this.headers.put(header, value);
        return this;
    }

}
