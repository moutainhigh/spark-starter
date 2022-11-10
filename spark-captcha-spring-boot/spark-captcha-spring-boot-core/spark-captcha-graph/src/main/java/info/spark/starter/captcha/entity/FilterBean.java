package info.spark.starter.captcha.entity;

import org.springframework.http.HttpMethod;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.21 16:31
 * @since 1.0.0
 */
@Data
public class FilterBean {
    /** Uri */
    private String uri;
    /** Method */
    private HttpMethod method;
    /** 拦截类型: 图片验证码, 短信验证码 */
    private String type;
}
