package info.spark.agent.entity;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: header 相关字段 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 05:09
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiServiceHeader implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 7877510540580736201L;
    /** Timestamp */
    private Long timestamp;
    /** 预留字段, 用于接口签名 */
    private String sign;
    /** Headers */
    private Map<String, String> headers;
    /** Path */
    private String path;
    /** Method */
    private String method;
    /** App secret */
    private String appSecret;
    /** 重复提交检查 */
    private boolean enableReplyCheck;

    /**
     * Api service header
     *
     * @param timestamp timestamp
     * @param sign      sign
     * @since 1.0.0
     */
    @Contract(pure = true)
    public ApiServiceHeader(Long timestamp, String sign) {
        this.timestamp = timestamp;
        this.sign = sign;
    }

    /**
     * Api service header
     *
     * @param timestamp timestamp
     * @param sign      sign
     * @param path      path
     * @param method    method
     * @param headers   headers
     * @since 1.0.0
     */
    @Contract(pure = true)
    public ApiServiceHeader(Long timestamp,
                            String sign,
                            String path,
                            String method,
                            Map<String, String> headers) {
        this.timestamp = timestamp;
        this.sign = sign;
        this.path = path;
        this.method = method;
        this.headers = headers;
    }
}
