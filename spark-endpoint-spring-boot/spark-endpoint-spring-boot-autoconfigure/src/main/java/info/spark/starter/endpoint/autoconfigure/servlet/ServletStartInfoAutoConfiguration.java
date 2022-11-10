package info.spark.starter.endpoint.autoconfigure.servlet;

import info.spark.starter.basic.Result;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.enums.LibraryEnum;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.endpoint.servlet.ServletInitializationService;
import info.spark.starter.util.core.api.R;
import info.spark.starter.endpoint.initialization.InitializationService;

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 11:20
 * @since 1.3.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletStartInfoAutoConfiguration implements SparkAutoConfiguration {


    /**
     * Start info endpoint
     *
     * @return the start info endpoint
     * @since 2.1.0
     */
    @Bean
    public StartInfoEndpoint startInfoEndpoint() {
        return new StartInfoEndpoint();
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.12.22 09:31
     * @since 2.1.0
     */
    @WebEndpoint(id = "start")
    public static class StartInfoEndpoint {
        /** Response */
        @Resource
        private HttpServletResponse response;

        /**
         * 显示 git 相关信息, 如果是本地编译运行, 不会生成 git.properties 文件, 则直接重定向到 /actuator/info
         *
         * @return the result
         * @throws IOException the io exception
         * @since 1.3.0
         */
        @ReadOperation
        public Result<Properties> versionInformation() throws IOException {
            ClassLoader classLoader = this.getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(App.GIT_CONFIG_FILE_NAME);
            if (inputStream == null) {
                this.response.sendRedirect(ConfigKit.getContextPath() + LibraryEnum.START_URL);
                return null;
            } else {
                Properties properties = new Properties();
                try {
                    properties.load(inputStream);
                } catch (IOException ignored) {
                }
                return R.succeed(properties);
            }
        }
    }

    /**
     * Servlet initialization service
     *
     * @return the initialization service
     * @since 2022.1.1
     */
    @Bean
    public InitializationService servletInitializationService() {
        return new ServletInitializationService();
    }
}
