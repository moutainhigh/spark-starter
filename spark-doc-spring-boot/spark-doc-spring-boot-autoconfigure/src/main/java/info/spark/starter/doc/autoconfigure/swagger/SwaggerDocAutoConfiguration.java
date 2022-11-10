package info.spark.starter.doc.autoconfigure.swagger;


import info.spark.starter.common.constant.App;
import info.spark.starter.common.enums.LibraryEnum;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.common.util.StartUtils;
import info.spark.starter.doc.swagger.parameter.ExpandedParameterNotBlankAnnotationPlugin;
import info.spark.starter.doc.swagger.plugin.ParameterNotBlankAnnotationPlugin;
import info.spark.starter.doc.swagger.schema.ModelPropertyNotBlankAnnotationPlugin;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.Servlet;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationWebMvcConfiguration;
import springfox.documentation.swagger2.web.Swagger2ControllerWebMvc;

import static com.google.common.collect.Lists.newArrayList;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:54
 * @since 1.4.0
 */
@Configuration(proxyBeanMethods = false)
@Profile(value = {App.ENV_LOCAL, App.ENV_DEV, App.ENV_TEST, App.ENV_PREV})
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, Swagger2ControllerWebMvc.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import(value = {Swagger2DocumentationWebMvcConfiguration.class})
@ConditionalOnProperty(name = SwaggerDocProperties.PREFIX + ".enabled", matchIfMissing = true)
@EnableConfigurationProperties(SwaggerDocProperties.class)
public class SwaggerDocAutoConfiguration implements SparkAutoConfiguration, BeanFactoryAware {

    /** Swagger properties */
    private final SwaggerDocProperties swaggerProperties;
    /** Basic auth */
    private static final String BASIC_AUTH = "BasicAuth";
    /** None */
    private static final String NONE = "None";
    /** Bean factory */
    private BeanFactory beanFactory;

    /**
     * Instantiates a new Swagger auto configuration.
     *
     * @param swaggerProperties the swagger properties
     * @since 1.4.0
     */
    @Contract(pure = true)
    public SwaggerDocAutoConfiguration(@NotNull SwaggerDocProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
        // 默认排除 /actuator 接口文档
        swaggerProperties.getExcludePath().add("/actuator/**");
        swaggerProperties.getExcludePath().add("/error");
    }

    /**
     * Ui configuration ui configuration.
     *
     * @param swaggerProperties the swagger properties
     * @return the ui configuration
     * @since 1.4.0
     */
    @Bean
    public UiConfiguration uiConfiguration(@NotNull SwaggerDocProperties swaggerProperties) {
        return UiConfigurationBuilder.builder()
            .deepLinking(swaggerProperties.getUiConfig().getDeepLinking())
            .defaultModelExpandDepth(swaggerProperties.getUiConfig().getDefaultModelExpandDepth())
            .defaultModelRendering(swaggerProperties.getUiConfig().getDefaultModelRendering())
            .defaultModelsExpandDepth(swaggerProperties.getUiConfig().getDefaultModelsExpandDepth())
            .displayOperationId(swaggerProperties.getUiConfig().getDisplayOperationId())
            .displayRequestDuration(swaggerProperties.getUiConfig().getDisplayRequestDuration())
            .docExpansion(swaggerProperties.getUiConfig().getDocExpansion())
            .maxDisplayedTags(swaggerProperties.getUiConfig().getMaxDisplayedTags())
            .operationsSorter(swaggerProperties.getUiConfig().getOperationsSorter())
            .showExtensions(swaggerProperties.getUiConfig().getShowExtensions())
            .tagsSorter(swaggerProperties.getUiConfig().getTagsSorter())
            .validatorUrl(swaggerProperties.getUiConfig().getValidatorUrl())
            .build();
    }

    /**
     * Create rest api list.
     *
     * @param swaggerProperties the swagger properties
     * @return the list
     * @since 1.4.0
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(UiConfiguration.class)
    @ConditionalOnProperty(name = SwaggerDocProperties.PREFIX + ".enabled", matchIfMissing = true)
    public List<Docket> createRestApi(@NotNull SwaggerDocProperties swaggerProperties) {
        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) this.beanFactory;
        List<Docket> docketList = new LinkedList<>();

        // 没有分组
        if (swaggerProperties.getDocket().size() == 0) {
            SwaggerDocProperties.DocketInfo docketInfo = new SwaggerDocProperties.DocketInfo()
                .setTitle(swaggerProperties.getTitle())
                .setDescription(swaggerProperties.getDescription())
                .setVersion(StringUtils.isEmpty(swaggerProperties.getVersion())
                            ? ConfigKit.getAppVersion()
                            : swaggerProperties.getVersion())
                .setLicense(swaggerProperties.getLicense())
                .setLicenseUrl(swaggerProperties.getLicenseUrl())
                .setBasePackage(swaggerProperties.getBasePackage())
                .setContact(new SwaggerDocProperties.Contact(swaggerProperties.getContact().getName(),
                                                             swaggerProperties.getContact().getUrl(),
                                                             swaggerProperties.getContact().getEmail()))
                .setTermsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl());

            swaggerProperties.getDocket().put(ConfigKit.getAppName(), docketInfo);
        }

        // 分组创建
        return this.buildDockets(swaggerProperties, configurableBeanFactory, docketList);
    }

    /**
     * Build dockets list
     *
     * @param swaggerProperties       swagger properties
     * @param configurableBeanFactory configurable bean factory
     * @param docketList              docket list
     * @return the list
     * @since 1.4.0
     */
    @Contract("_, _, _ -> param3")
    private List<Docket> buildDockets(@NotNull SwaggerDocProperties swaggerProperties,
                                      ConfigurableBeanFactory configurableBeanFactory,
                                      List<Docket> docketList) {
        for (String groupName : swaggerProperties.getDocket().keySet()) {
            SwaggerDocProperties.DocketInfo docketInfo = swaggerProperties.getDocket().get(groupName);

            ApiInfo apiInfo = new ApiInfoBuilder()
                .title(docketInfo.getTitle().isEmpty() ? swaggerProperties.getTitle() : docketInfo.getTitle())
                .description(docketInfo.getDescription().isEmpty() ? swaggerProperties.getDescription() : docketInfo.getDescription())
                .version(docketInfo.getVersion().isEmpty() ? StringUtils.isEmpty(swaggerProperties.getVersion())
                                                             ? ConfigKit.getAppVersion()
                                                             : swaggerProperties.getVersion()
                                                           : docketInfo.getVersion())
                .license(docketInfo.getLicense().isEmpty() ? swaggerProperties.getLicense() : docketInfo.getLicense())
                .licenseUrl(docketInfo.getLicenseUrl().isEmpty() ? swaggerProperties.getLicenseUrl() : docketInfo.getLicenseUrl())
                .contact(
                    new Contact(
                        docketInfo.getContact().getName().isEmpty()
                        ? swaggerProperties.getContact().getName()
                        : docketInfo.getContact().getName(),
                        docketInfo.getContact().getUrl().isEmpty()
                        ? swaggerProperties.getContact().getUrl()
                        : docketInfo.getContact().getUrl(),
                        docketInfo.getContact().getEmail().isEmpty()
                        ? swaggerProperties.getContact().getEmail()
                        : docketInfo.getContact().getEmail()
                    )
                        )
                .termsOfServiceUrl(docketInfo.getTermsOfServiceUrl().isEmpty()
                                   ? swaggerProperties.getTermsOfServiceUrl()
                                   : docketInfo.getTermsOfServiceUrl())
                .build();

            Docket docketForBuilder = new Docket(DocumentationType.SWAGGER_2)
                .host(swaggerProperties.getHost())
                .apiInfo(apiInfo)
                .securityContexts(Collections.singletonList(this.securityContext()))

                .globalOperationParameters(this.assemblyGlobalOperationParameters(
                    swaggerProperties.getGlobalOperationParameters(),
                    docketInfo.getGlobalOperationParameters()));

            this.processAuthAndResponse(swaggerProperties, docketForBuilder);

            Docket docket = this.processPath(
                    docketForBuilder
                        .groupName(groupName)
                        .select()
                        .apis(RequestHandlerSelectors.basePackage(docketInfo.getBasePackage())))
                .build();

            // ignoredParameterTypes
            Class<?>[] array = new Class[docketInfo.getIgnoredParameterTypes().size()];
            Class<?>[] ignoredParameterTypes = docketInfo.getIgnoredParameterTypes().toArray(array);
            docket.ignoredParameterTypes(ignoredParameterTypes);

            // 如果是 dubbo 应用, 会导致存在相同的 bean name, 因此这里添加一个前缀
            configurableBeanFactory.registerSingleton("SwaggerDoc@" + groupName, docket);
            docketList.add(docket);
        }
        return docketList;
    }


    /**
     * Process path *
     *
     * @param apiSelectorBuilder api selector builder
     * @return the api selector builder
     * @since 1.4.0
     */
    private ApiSelectorBuilder processPath(ApiSelectorBuilder apiSelectorBuilder) {
        // base-path 处理 当没有配置任何 path 的时候,解析 /**
        if (this.swaggerProperties.getBasePath().isEmpty()) {
            this.swaggerProperties.getBasePath().add("/**");
        }

        for (String base : this.swaggerProperties.getBasePath()) {
            apiSelectorBuilder.paths(PathSelectors.ant(base));
        }

        for (String exclude : this.swaggerProperties.getExcludePath()) {
            apiSelectorBuilder.paths(PathSelectors.ant(exclude).negate());
        }
        return apiSelectorBuilder;
    }

    /**
     * 配置默认的全局鉴权策略的开关,以及通过正则表达式进行匹配; 默认 ^.*$ 匹配所有 URL
     * 其中 securityReferences 为配置启用的鉴权策略
     *
     * @return security context
     * @since 1.4.0
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(this.defaultAuth())
            .forPaths(PathSelectors.regex(this.swaggerProperties.getAuthorization().getAuthRegex()))
            .build();
    }

    /**
     * Build global operation parameters from swagger properties list
     *
     * @param globalOperationParameters global operation parameters
     * @return the list
     * @since 1.4.0
     */
    @NotNull
    private List<Parameter> buildGlobalOperationParametersFromSwaggerProperties(
        List<SwaggerDocProperties.GlobalOperationParameter> globalOperationParameters) {
        List<Parameter> parameters = new ArrayList<>();

        if (Objects.isNull(globalOperationParameters)) {
            return parameters;
        }
        for (SwaggerDocProperties.GlobalOperationParameter globalOperationParameter : globalOperationParameters) {
            parameters.add(new ParameterBuilder()
                               .name(globalOperationParameter.getName())
                               .description(globalOperationParameter.getDescription())
                               .modelRef(new ModelRef(globalOperationParameter.getModelRef()))
                               .parameterType(globalOperationParameter.getParameterType())
                               .required(Boolean.parseBoolean(globalOperationParameter.getRequired()))
                               .build());
        }
        return parameters;
    }

    /**
     * Process auth and response *
     *
     * @param swaggerProperties swagger properties
     * @param docketForBuilder  docket for builder
     * @since 1.4.0
     */
    private void processAuthAndResponse(@NotNull SwaggerDocProperties swaggerProperties, Docket docketForBuilder) {
        if (BASIC_AUTH.equalsIgnoreCase(swaggerProperties.getAuthorization().getType())) {
            docketForBuilder.securitySchemes(Collections.singletonList(this.basicAuth()));
        } else if (!NONE.equalsIgnoreCase(swaggerProperties.getAuthorization().getType())) {
            docketForBuilder.securitySchemes(Collections.singletonList(this.apiKey()));
        }

        // 全局响应消息
        if (!swaggerProperties.isApplyDefaultResponseMessages()) {
            this.buildGlobalResponseMessage(swaggerProperties, docketForBuilder);
        }
    }

    /**
     * 局部参数按照 name 覆盖局部参数
     *
     * @param globalOperationParameters global operation parameters
     * @param docketOperationParameters docket operation parameters
     * @return list list
     * @since 1.4.0
     */
    @NotNull
    private List<Parameter> assemblyGlobalOperationParameters(
        List<SwaggerDocProperties.GlobalOperationParameter> globalOperationParameters,
        List<SwaggerDocProperties.GlobalOperationParameter> docketOperationParameters) {

        if (Objects.isNull(docketOperationParameters) || docketOperationParameters.isEmpty()) {
            return this.buildGlobalOperationParametersFromSwaggerProperties(globalOperationParameters);
        }

        Set<String> docketNames = docketOperationParameters.stream()
            .map(SwaggerDocProperties.GlobalOperationParameter::getName)
            .collect(Collectors.toSet());

        List<SwaggerDocProperties.GlobalOperationParameter> resultOperationParameters = newArrayList();

        if (Objects.nonNull(globalOperationParameters)) {
            for (SwaggerDocProperties.GlobalOperationParameter parameter : globalOperationParameters) {
                if (!docketNames.contains(parameter.getName())) {
                    resultOperationParameters.add(parameter);
                }
            }
        }

        resultOperationParameters.addAll(docketOperationParameters);
        return this.buildGlobalOperationParametersFromSwaggerProperties(resultOperationParameters);
    }

    /**
     * 配置默认的全局鉴权策略; 其中返回的 SecurityReference 中,reference 即为 ApiKey 对象里面的 name,保持一致才能开启全局鉴权
     *
     * @return list list
     * @since 1.4.0
     */
    @NotNull
    private @Unmodifiable List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(SecurityReference.builder()
                                             .reference(this.swaggerProperties.getAuthorization().getName())
                                             .scopes(authorizationScopes).build());
    }

    /**
     * 配置基于 BasicAuth 的鉴权对象
     *
     * @return basic auth
     * @since 1.4.0
     */
    @NotNull
    @Contract(" -> new")
    private BasicAuth basicAuth() {
        return new BasicAuth(this.swaggerProperties.getAuthorization().getName());
    }

    /**
     * 配置基于 ApiKey 的鉴权对象
     *
     * @return api key
     * @since 1.4.0
     */
    @NotNull
    @Contract(" -> new")
    private ApiKey apiKey() {
        return new ApiKey(this.swaggerProperties.getAuthorization().getName(),
                          this.swaggerProperties.getAuthorization().getKeyName(),
                          ApiKeyVehicle.HEADER.getValue());
    }

    /**
     * 设置全局响应消息
     *
     * @param swaggerProperties swaggerProperties 支持 POST,GET,PUT,PATCH,DELETE,HEAD,OPTIONS,TRACE
     * @param docketForBuilder  swagger docket builder
     * @since 1.4.0
     */
    private void buildGlobalResponseMessage(@NotNull SwaggerDocProperties swaggerProperties, @NotNull Docket docketForBuilder) {

        SwaggerDocProperties.GlobalResponseMessage globalResponseMessages = swaggerProperties.getGlobalResponseMessage();

        /* POST,GET,PUT,PATCH,DELETE,HEAD,OPTIONS,TRACE 响应消息体 **/
        List<ResponseMessage> postResponseMessages = this.getResponseMessageList(globalResponseMessages.getPost());
        List<ResponseMessage> getResponseMessages = this.getResponseMessageList(globalResponseMessages.getGet());
        List<ResponseMessage> putResponseMessages = this.getResponseMessageList(globalResponseMessages.getPut());
        List<ResponseMessage> patchResponseMessages = this.getResponseMessageList(globalResponseMessages.getPatch());
        List<ResponseMessage> deleteResponseMessages = this.getResponseMessageList(globalResponseMessages.getDelete());
        List<ResponseMessage> headResponseMessages = this.getResponseMessageList(globalResponseMessages.getHead());
        List<ResponseMessage> optionsResponseMessages = this.getResponseMessageList(globalResponseMessages.getOptions());
        List<ResponseMessage> trackResponseMessages = this.getResponseMessageList(globalResponseMessages.getTrace());

        docketForBuilder.useDefaultResponseMessages(swaggerProperties.isApplyDefaultResponseMessages())
            .globalResponseMessage(RequestMethod.POST, postResponseMessages)
            .globalResponseMessage(RequestMethod.GET, getResponseMessages)
            .globalResponseMessage(RequestMethod.PUT, putResponseMessages)
            .globalResponseMessage(RequestMethod.PATCH, patchResponseMessages)
            .globalResponseMessage(RequestMethod.DELETE, deleteResponseMessages)
            .globalResponseMessage(RequestMethod.HEAD, headResponseMessages)
            .globalResponseMessage(RequestMethod.OPTIONS, optionsResponseMessages)
            .globalResponseMessage(RequestMethod.TRACE, trackResponseMessages);
    }

    /**
     * 获取返回消息体列表
     *
     * @param globalResponseMessageBodyList 全局 Code 消息返回集合
     * @return response message list
     * @since 1.4.0
     */
    @NotNull
    private List<ResponseMessage> getResponseMessageList(
        @NotNull List<SwaggerDocProperties.GlobalResponseMessageBody> globalResponseMessageBodyList) {
        List<ResponseMessage> responseMessages = new ArrayList<>();
        for (SwaggerDocProperties.GlobalResponseMessageBody globalResponseMessageBody : globalResponseMessageBodyList) {
            ResponseMessageBuilder responseMessageBuilder = new ResponseMessageBuilder();
            responseMessageBuilder.code(globalResponseMessageBody.getCode()).message(globalResponseMessageBody.getMessage());

            if (!StringUtils.isEmpty(globalResponseMessageBody.getModelRef())) {
                responseMessageBuilder.responseModel(new ModelRef(globalResponseMessageBody.getModelRef()));
            }
            responseMessages.add(responseMessageBuilder.build());
        }

        return responseMessages;
    }

    /**
     * Sets bean factory *
     *
     * @param beanFactory bean factory
     * @throws BeansException beans exception
     * @since 1.4.0
     */
    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 自动装配类检查
     *
     * @since 1.0.0
     */
    @Override
    public void execute() {
        StartUtils.addCustomInfo(() -> StartUtils.padding(LibraryEnum.SWAGGER_JSON.getName())
                                       + StartUtils.buildUrl()
                                       + LibraryEnum.SWAGGER_JSON.getUri()
                                       + "?group="
                                       + ConfigKit.getAppName());
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.4.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.05.08 16:54
     * @since 1.4.0
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnResource(resources = "classpath:META-INF/resources/swagger-ui.html")
    static class SwaggerUiAutoConfiguration implements SparkAutoConfiguration {

        /**
         * Gets library type *
         *
         * @return the library type
         * @since 1.4.0
         */
        @Override
        public LibraryEnum getLibraryType() {
            return LibraryEnum.SWAGGER_REST_DEFAULT;
        }

        /**
         * Parameter not blank annotation plugin parameter not blank annotation plugin
         *
         * @return the parameter not blank annotation plugin
         * @since 1.0.0
         */
        @Bean
        public ParameterNotBlankAnnotationPlugin parameterNotBlankAnnotationPlugin() {
            return new ParameterNotBlankAnnotationPlugin();
        }

        /**
         * Model property not blank annotation plugin model property not blank annotation plugin
         *
         * @return the model property not blank annotation plugin
         * @since 1.0.0
         */
        @Bean
        public ModelPropertyNotBlankAnnotationPlugin modelPropertyNotBlankAnnotationPlugin() {
            return new ModelPropertyNotBlankAnnotationPlugin();
        }

        /**
         * Not blank annotation plugin expanded parameter not blank annotation plugin
         *
         * @return the expanded parameter not blank annotation plugin
         * @since 1.0.0
         */
        @Bean
        public ExpandedParameterNotBlankAnnotationPlugin notBlankAnnotationPlugin() {
            return new ExpandedParameterNotBlankAnnotationPlugin();
        }
    }

}
