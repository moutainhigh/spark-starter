package info.spark.starter.rest.autoconfigure.servlet;

import info.spark.starter.common.start.SparkAutoConfiguration;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;

/**
 * <p>Description: 统一处理首尾空白字符串问题</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.05.25 13:55
 * @since 1.9.0
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class WebMvcStringTrimAutoConfiguration implements SparkAutoConfiguration {

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.05.25 13:55
     * @since 1.9.0
     */
    @ControllerAdvice
    public static class ControllerStringParamTrimConfig {

        /**
         * 处理 url 或 form 表单中的参数.
         * {@link StringTrimmerEditor}: 构造方法中 boolean 参数含义为如果是空白字符串,是否转换为null, 即如果为true,那么 " " 会被转换为 null,否者为 ""
         *
         * @param binder binder
         * @since 1.9.0
         */
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            StringTrimmerEditor propertyEditor = new StringTrimmerEditor(false);
            binder.registerCustomEditor(String.class, propertyEditor);
        }
    }

}
