package info.spark.starter.captcha.autoconfigure;

import info.spark.starter.captcha.entity.CaptchaConfig;
import info.spark.starter.captcha.entity.FilterBean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.19 09:45
 * @since 1.0.0
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = CaptchaProperties.PREFIX)
public class CaptchaProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.captcha";

    /** 验证码过期时间, 默认 60 秒 */
    private Long expireTime = 60L;
    /** 验证码类型, 设置后即开启验证码功能 (普通的和动态的) */
    private CaptchaConfig.CaptchaType type;
    /** 动态验证码检查类型 (如果配置为动态验证码, 此字段必须配置, 有错误统计和请求次数统计 2 种方式) */
    private List<CaptchaConfig.CheckType> checks;
    /** 是否对单独的应用启用验证码 clientId: true (使用在 oauth 模块, 统一对验证码进行配置) */
    private Map<String, Boolean> enableMap;
    /** 验证码拦截器拦截的 uri:method, 调用接口指定的 http method 将进行验证码处理 */
    private List<FilterBean> filter;
    /** 请求失败计数器过期时间 */
    private Long requestFailedCountExpiresTime = 5L;
    /** 单位时间内 请求失败 次数阈值 (失败次数) */
    private Integer requestFailedCount = 5;
    /** 请求计数器过期时间 */
    private Long requestCountExpiresTime = 2 * 60L;
    /** 单位时间请求的次数阈值 */
    private Integer requestCount = 3;

    /** 宽度 */
    private Integer width = 200;
    /** 高度 */
    private Integer height = 50;
    /** 内容 */
    private Content content = new Content();
    /** 背景色 */
    private BackgroundColor backgroundColor = new BackgroundColor();
    /** 字体 */
    private Font font = new Font();
    /** 边框 */
    private Border border = new Border();

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2019.11.19 09:45
     * @since 1.0.0
     */
    @Data
    static class BackgroundColor {
        /** 开始渐变色 */
        private String from = "lightGray";
        /** 结束渐变色 */
        private String to = "white";

    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2019.11.19 09:45
     * @since 1.0.0
     */
    @Data
    static class Content {
        /** 内容源 */
        private String source = "1234567890";
        /** 内容长度 */
        private Integer length = 4;
        /** 内容间隔 */
        private Integer space = 2;

    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2019.11.19 09:45
     * @since 1.0.0
     */
    @Data
    static class Border {
        /** 是否开启 */
        private Boolean enabled = true;
        /** 颜色 */
        private String color = "black";
        /** 厚度 */
        private Integer thickness = 1;

    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2019.11.19 09:45
     * @since 1.0.0
     */
    @Data
    static class Font {
        /** 名称 */
        private String name = "Arial";
        /** 颜色 */
        private String color = "black";
        /** 大小 */
        private Integer size = 40;
    }
}
