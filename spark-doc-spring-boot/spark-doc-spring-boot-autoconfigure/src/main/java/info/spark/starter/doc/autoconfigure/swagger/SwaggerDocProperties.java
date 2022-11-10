package info.spark.starter.doc.autoconfigure.swagger;

import info.spark.starter.common.constant.App;
import info.spark.starter.core.util.NetUtils;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:54
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = SwaggerDocProperties.PREFIX)
public class SwaggerDocProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.doc.swagger";
    /** 是否开启 swagger */
    private boolean enabled;
    /** 标题 */
    private String title = "";
    /** 描述 */
    private String description = "";
    /** 版本 */
    private String version = "";
    /** 许可证 */
    private String license = "";
    /** 许可证 URL */
    private String licenseUrl = "";
    /** 服务条款 URL */
    private String termsOfServiceUrl = "";
    /** 忽略的参数类型 */
    private List<Class<?>> ignoredParameterTypes = new ArrayList<>();
    /** swagger 会解析的包路径 */
    private String basePackage = App.BASE_PACKAGES;
    /** swagger 会解析的 url 规则 */
    private List<String> basePath = new ArrayList<>();
    /** 在 basePath 基础上需要排除的 url 规则 */
    private List<String> excludePath = new ArrayList<>();
    /** 分组文档 */
    private Map<String, DocketInfo> docket = new LinkedHashMap<>();
    /** host 信息 */
    private String host = NetUtils.getLocalHost();
    /** 全局参数配置 */
    private List<GlobalOperationParameter> globalOperationParameters;
    /** Contact */
    private Contact contact = new Contact();
    /** 页面功能配置 */
    private UiConfig uiConfig = new UiConfig();
    /** 是否使用默认预定义的响应消息 ,默认 true */
    private boolean applyDefaultResponseMessages = Boolean.TRUE;
    /** 全局响应消息 */
    private GlobalResponseMessage globalResponseMessage;
    /** 全局统一鉴权配置 */
    private Authorization authorization = new Authorization();

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 14:54
     * @since 1.0.0
     */
    @Data
    @NoArgsConstructor
    public static class GlobalOperationParameter {
        /** 参数名 */
        private String name;
        /** 描述信息 */
        private String description;
        /** 指定参数类型 */
        private String modelRef;
        /** 参数放在哪个地方:header,query,path,body.form */
        private String parameterType;
        /** 参数是否必须传 */
        private String required;

    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 14:54
     * @since 1.0.0
     */
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    @AllArgsConstructor
    public static class DocketInfo {
        /** 标题 */
        private String title = "";
        /** 描述 */
        private String description = "";
        /** 版本 */
        private String version = "";
        /** 许可证 */
        private String license = "";
        /** 许可证 URL */
        private String licenseUrl = "";
        /** 服务条款 URL */
        private String termsOfServiceUrl = "";
        /** Contact */
        private Contact contact = new Contact();
        /** swagger 会解析的包路径 */
        private String basePackage = "";
        /** swagger 会解析的 url 规则 */
        private List<String> basePath = new ArrayList<>();
        /** 在 basePath 基础上需要排除的 url 规则 */
        private List<String> excludePath = new ArrayList<>();
        /** Global operation parameters */
        private List<GlobalOperationParameter> globalOperationParameters;
        /** 忽略的参数类型 */
        private List<Class<?>> ignoredParameterTypes = new ArrayList<>();

    }

    /**
     * The type Contact.
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 14:54
     * @since 1.0.0
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contact {
        /** 联系人 */
        private String name = App.SPARK_NAME_SPACE;
        /** 联系人 url */
        private String url = "";
        /** 联系人 email */
        private String email = App.SPARK_NAME_SPACE + "@gmail.com";

    }

    /**
     * The type Global response message.
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 14:54
     * @since 1.0.0
     */
    @Data
    @NoArgsConstructor
    public static class GlobalResponseMessage {
        /** POST 响应消息体 */
        List<GlobalResponseMessageBody> post = new ArrayList<>();
        /** GET 响应消息体 */
        List<GlobalResponseMessageBody> get = new ArrayList<>();
        /** PUT 响应消息体 */
        List<GlobalResponseMessageBody> put = new ArrayList<>();
        /** PATCH 响应消息体 */
        List<GlobalResponseMessageBody> patch = new ArrayList<>();
        /** DELETE 响应消息体 */
        List<GlobalResponseMessageBody> delete = new ArrayList<>();
        /** HEAD 响应消息体 */
        List<GlobalResponseMessageBody> head = new ArrayList<>();
        /** OPTIONS 响应消息体 */
        List<GlobalResponseMessageBody> options = new ArrayList<>();
        /** TRACE 响应消息体 */
        List<GlobalResponseMessageBody> trace = new ArrayList<>();

    }

    /**
     * The type Global response message body.
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 14:54
     * @since 1.0.0
     */
    @Data
    @NoArgsConstructor
    public static class GlobalResponseMessageBody {
        /** 响应码 */
        private int code;
        /** 响应消息 */
        private String message;
        /** 响应体 */
        private String modelRef;

    }

    /**
     * The type Ui config.
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 14:54
     * @since 1.0.0
     */
    @Data
    @NoArgsConstructor
    public static class UiConfig {
        /** Api sorter */
        private String apiSorter = "alpha";
        /** 是否启用 json 编辑器 */
        private Boolean jsonEditor = false;
        /** 是否显示请求头信息 */
        private Boolean showRequestHeaders = true;
        /** 支持页面提交的请求类型 */
        private String submitMethods = "get,post,put,delete,patch";
        /** 请求超时时间 */
        private Long requestTimeout = 10000L;
        /** Deep linking */
        private Boolean deepLinking;
        /** Display operation id */
        private Boolean displayOperationId;
        /** Default models expand depth */
        private Integer defaultModelsExpandDepth;
        /** Default model expand depth */
        private Integer defaultModelExpandDepth;
        /** Default model rendering */
        private ModelRendering defaultModelRendering;
        /** 是否显示请求耗时,默认 false */
        private Boolean displayRequestDuration = true;
        /** 可选 none | list */
        private DocExpansion docExpansion;
        /** Boolean=false OR String */
        private Object filter;
        /** Max displayed tags */
        private Integer maxDisplayedTags;
        /** Operations sorter */
        private OperationsSorter operationsSorter;
        /** Show extensions */
        private Boolean showExtensions;
        /** Tags sorter */
        private TagsSorter tagsSorter;
        /** Network */
        private String validatorUrl;
    }

    /**
     * securitySchemes 支持方式之一 ApiKey
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 14:54
     * @since 1.0.0
     */
    @Data
    @NoArgsConstructor
    public static class Authorization {
        /** 鉴权策略 ID,对应 SecurityReferences ID */
        private String name = "Authorization";
        /** 鉴权策略,可选 ApiKey | BasicAuth | None,默认 ApiKey */
        private String type = "BasicAuth";
        /** 鉴权传递的 Header 参数 */
        private String keyName = "X-Client-Token";
        /** 需要开启鉴权 URL 的正则 */
        private String authRegex = "^.*$";
    }

}

