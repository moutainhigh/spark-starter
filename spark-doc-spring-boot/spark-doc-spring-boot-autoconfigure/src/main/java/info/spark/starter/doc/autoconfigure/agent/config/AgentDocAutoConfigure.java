package info.spark.starter.doc.autoconfigure.agent.config;

import com.fasterxml.classmate.TypeResolver;
import info.spark.agent.AbstractAgentService;
import info.spark.agent.annotation.ApiService;
import info.spark.agent.annotation.ApiServiceMethod;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.enums.LibraryEnum;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.doc.agent.hand.AgentDocHandlerFilter;
import info.spark.starter.doc.agent.schema.AgentHandlerPlugin;
import info.spark.starter.doc.agent.schema.AgentOperationParameterReader;
import info.spark.starter.doc.agent.schema.AgentParameterHeadersConditionReader;
import info.spark.starter.doc.agent.schema.AgentParameterRequiredBuilderPlugin;
import info.spark.starter.doc.agent.schema.AgentParameterTypeReaderPlugin;
import info.spark.starter.doc.autoconfigure.agent.annotation.EnableAutoAop;
import info.spark.starter.doc.autoconfigure.swagger.SwaggerDocProperties;
import info.spark.starter.doc.common.schema.SparkOperationModelsProviderPlugin;
import info.spark.starter.doc.common.schema.SparkOperationResponseMessagePlugin;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.DispatcherServlet;

import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;
import springfox.documentation.swagger2.web.Swagger2ControllerWebMvc;

/**
 * <p>Description: agent doc 装配类 </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.31 12:02
 * @since 1.7.0
 */
@EnableAutoAop
@Configuration(proxyBeanMethods = false)
@Profile(value = {App.ENV_LOCAL, App.ENV_DEV, App.ENV_TEST, App.ENV_PREV})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = SwaggerDocProperties.PREFIX + ".enabled", matchIfMissing = true)
@ConditionalOnClass(value = {DispatcherServlet.class, Swagger2ControllerWebMvc.class, ApiService.class,
                             AgentOperationParameterReader.class})
@EnableConfigurationProperties(SwaggerDocProperties.class)
public class AgentDocAutoConfigure implements SparkAutoConfiguration {

    /**
     * 构造
     *
     * @param swaggerProperties swagger properties
     * @since 1.7.0
     */
    @Contract(pure = true)
    public AgentDocAutoConfigure(@NotNull SwaggerDocProperties swaggerProperties) {
        // 默认排除 /agent 接口文档
        swaggerProperties.getExcludePath().add("/agent/**");
        swaggerProperties.getExcludePath().add("/request-urls");
    }

    /**
         * <p>Description: </p>
     *
     * @author wanghao
     * @version 1.7.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.01.07 15:21
     * @since 1.7.0
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(AbstractAgentService.class)
    static class AgentPluginSchemas {

        /**
         * 扫描 agent handler
         *
         * @param methodResolver method resolver
         * @return the agent handler plugin
         * @since 1.7.0
         */
        @Bean
        @SuppressWarnings("all")
        public AgentHandlerPlugin agentHandlerPlugin(HandlerMethodResolver methodResolver) {
            return new AgentHandlerPlugin(methodResolver);
        }

        /**
         * Agent operation models provider plugin 返回类型统一Result<?>包装
         *
         * @return the agent operation models provider plugin
         * @since 1.7.0
         */
        @Bean
        @ConditionalOnMissingBean
        public SparkOperationModelsProviderPlugin agentOperationModelsProviderPlugin() {
            return new SparkOperationModelsProviderPlugin(ApiServiceMethod.class);
        }

        /**
         * Agent operation parameter reader
         *
         * @return the agent operation parameter reader
         * @since 1.7.0
         */
        @Bean
        public AgentOperationParameterReader agentOperationParameterReader() {
            return new AgentOperationParameterReader();
        }

        /**
         * Agent operation 返回类型统一Result<?>包装
         *
         * @return the agent operation response message plugin
         * @since 1.7.0
         */
        @Bean
        @ConditionalOnMissingBean
        public SparkOperationResponseMessagePlugin agentOperationResponseMessagePlugin() {
            return new SparkOperationResponseMessagePlugin(ApiServiceMethod.class);
        }

        /**
         * Agent parameter 是否必填
         *
         * @return the agent parameter required builder plugin
         * @since 1.7.0
         */
        @Bean
        public AgentParameterRequiredBuilderPlugin agentParameterRequiredBuilderPlugin() {
            return new AgentParameterRequiredBuilderPlugin();
        }

        /**
         * agent 参数 type，body会被ui渲染为json格式
         *
         * @return the agent parameter type reader plugin
         * @since 1.7.0
         */
        @Bean
        public AgentParameterTypeReaderPlugin agentParameterTypeReaderPlugin() {
            return new AgentParameterTypeReaderPlugin();
        }

        /**
         * 用于swagger ui 测试 agent get接口
         *
         * @return the agent doc handler filter
         * @since 1.7.0
         */
        @Bean
        @ConditionalOnClass(name = "io.undertow.servlet.spec.HttpServletRequestImpl")
        public AgentDocHandlerFilter agentDocHandlerFilter() {
            return new AgentDocHandlerFilter();
        }

        /**
         * agent header 插件
         * todo-dong4j : (2021-10-7 20:25) [哪里装配了 TypeResolver]
         *
         * @param typeResolver type resolver
         * @return the agent parameter headers condition reader
         * @since 1.7.0
         */
        @Bean
        public AgentParameterHeadersConditionReader agentParameterHeadersConditionReader(TypeResolver typeResolver) {
            return new AgentParameterHeadersConditionReader(typeResolver);
        }
    }

    /**
     * 获取库类型
     *
     * @return the library type
     * @since 1.7.0
     */
    @Override
    public LibraryEnum getLibraryType() {
        return LibraryEnum.SWAGGER_REST_BOOTSTRAP;
    }

}
