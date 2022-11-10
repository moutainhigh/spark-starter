package info.spark.starter.openness.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.08.20 17:58
 * @since 1.9.0
 */
@Data
public class SignEntity implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = -7275517199588987028L;

    /** Http method type */
    private String httpMethodType;
    /** Content type str */
    private String contentTypeStr;
    /** Timestamp */
    private String timestamp;
    /** nonce */
    private String nonce;
    /** Uri */
    private String uri;
    /** Param md 5 */
    private String paramMd5;
    /** Secret key */
    private String secretKey;
}
