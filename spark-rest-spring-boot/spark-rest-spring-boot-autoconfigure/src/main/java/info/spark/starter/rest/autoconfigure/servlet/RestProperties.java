package info.spark.starter.rest.autoconfigure.servlet;

import info.spark.starter.basic.constant.ConfigDefaultValue;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import lombok.Data;

/**
 * <p>Description: ${description}</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:47
 * @since 1.0.0
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = RestProperties.PREFIX)
public class RestProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.rest";

    /** Read timeout */
    private Integer readTimeout = 5000;
    /** Write timeout */
    private Integer writeTimeout = 5000;
    /** Connect timeout */
    private Integer connectTimeout = 3000;
    /** 启动完成后是否打开浏览器 */
    private boolean enableBrowser = Boolean.FALSE;
    /** 是否开启 undertow 容器的请求日志 */
    private boolean enableContainerLog = Boolean.FALSE;
    /** 是否开启 http2 支持 */
    private boolean enableHttp2 = Boolean.FALSE;
    /** request 和 response 缓存 */
    private boolean enableGlobalCacheFilter = Boolean.TRUE;
    /** filter 异常处理器 */
    private boolean enableExceptionFilter = Boolean.TRUE;
    /** token 参数注入处理器 */
    private boolean enableGlobalParameterFilter = Boolean.FALSE;
    /** 是否开启枚举类型全序列化 */
    private boolean enableEntityEnumAllFieldSerialize = Boolean.TRUE;
    /** 文件上传配置 */
    private Multipart multipart = new Multipart();
    /** json 配置 */
    private Json json = new Json();

    /**
         * <p>Description: json 序列化/反序列化, 时间格式配置 </p>
     *
     * @author dong4j
     * @version 1.2.4
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.04 15:44
     * @since 1.0.0
     */
    @Data
    public static class Json {
        /** 设置 jackson 时间序列化格式 */
        private String dateFormat = ConfigDefaultValue.DEFAULT_DATE_FORMAT;
        /** 设置时区 */
        private String timeZone = ConfigDefaultValue.DEFAULT_TIME_ZONE;
        /** 字段为空时不参与序列化 */
        private String defaultPropertyInclusion = ConfigDefaultValue.DEFAULT_PROPERTY_INCLUSION_VALUE;
    }

    /**
         * <p>Description: 文件上传配置 </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.07.13 17:32
     * @since 1.8.0
     */
    @Data
    public static class Multipart {
        /** Location */
        private String location = ConfigDefaultValue.CONTAINER_LOCATION;
    }
}
