package info.spark.starter.captcha.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.24 19:38
 * @since 1.0.0
 */
@Data
@Builder
public class CaptchaEnhancerEntity implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -1170040259388986545L;
    /** 请求失败计数器过期时间 */
    private Long requestFailedCountExpiresTime;
    /** 单位时间内 请求失败 次数阈值 (失败次数) */
    private Integer requestFailedCount;
    /** 请求计数器过期时间 */
    private Long requestCountExpiresTime;
    /** 单位时间请求的次数阈值 */
    private Integer requestCount;
    /** Captcha expires time */
    private Long captchaExpiresTime;
}
