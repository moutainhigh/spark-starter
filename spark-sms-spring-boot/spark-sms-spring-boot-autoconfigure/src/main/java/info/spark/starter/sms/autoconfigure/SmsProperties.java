package info.spark.starter.sms.autoconfigure;

import info.spark.starter.sms.SmsConfig;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 10:25
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = SmsProperties.PREFIX)
public class SmsProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.sms";
    /** Aliyun */
    private Aliyun aliyun = new Aliyun();
    /** Qcloud */
    private Qcloud qcloud = new Qcloud();
    /** Yimei */
    private Yimei yimei = new Yimei();

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.01 23:30
     * @since 1.0.0
     */
    private static class Aliyun extends SmsConfig {

        /** serialVersionUID */
        private static final long serialVersionUID = -6510927458754144685L;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.01 23:30
     * @since 1.0.0
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class Qcloud extends SmsConfig {

        /** serialVersionUID */
        private static final long serialVersionUID = 1186540592173834079L;
        /** Sign */
        @NotBlank(message = "sign 不能为空")
        private String sign;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.01 23:43
     * @since 1.0.0
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class Yimei extends SmsConfig {

        /** serialVersionUID */
        private static final long serialVersionUID = 1186540592173834079L;

        /** 亿美短信cdkey, 默认值 */
        @NotBlank(message = "appId 不能为空")
        private String appId = "EUCP-EMY-SMS1-0GVAX";
        /** 亿美短信密码 password, 默认值 */
        @NotBlank(message = "appKey 不能为空")
        private String appKey = "652831";
        /** 亿美短信URL, 默认值 */
        @URL(message = "不是合法的 URL")
        private String url = "http://sdk999in.eucp.b2m.cn:8080";
    }
}
