package info.spark.starter.captcha.autoconfigure;

import com.google.common.collect.Lists;

import info.spark.starter.captcha.enhancer.CaptchaCheckEnhancer;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: 通用拦截器配置类 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.21 20:45
 * @since 1.0.0
 */
public class CommonInterceptors implements WebMvcConfigurer {

    /** Inpath patterns */
    private List<String> inpathPatterns = new ArrayList<>();
    /** Expath patterns */
    private List<String> expathPatterns = new ArrayList<>();
    /** Interceptorlist */
    private final List<HandlerInterceptor> interceptorlist = Lists.newArrayList();
    /** Check enhancers */
    @SuppressWarnings("FieldCanBeLocal")
    private List<CaptchaCheckEnhancer> checkEnhancers = Lists.newArrayList();

    /**
     * Add interceptors *
     *
     * @param registry registry
     * @since 1.0.0
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        // 注册自定义拦截器,并配置可访问路径
        for (HandlerInterceptor intercept : this.interceptorlist) {
            registry.addInterceptor(intercept)
                .addPathPatterns(this.inpathPatterns.toArray(new String[0]))
                .excludePathPatterns(this.expathPatterns.toArray(new String[0]));
        }
    }

    /**
     * Sets inpath patterns *
     *
     * @param inpathPatterns inpath patterns
     * @return the inpath patterns
     * @since 1.0.0
     */
    public CommonInterceptors setInpathPatterns(List<String> inpathPatterns) {
        this.inpathPatterns = inpathPatterns;
        return this;
    }

    /**
     * Sets expath patterns *
     *
     * @param expathPatterns expath patterns
     * @return the expath patterns
     * @since 1.0.0
     */
    public CommonInterceptors setExpathPatterns(List<String> expathPatterns) {
        this.expathPatterns = expathPatterns;
        return this;
    }

    /**
     * Add handler interceptor web mvc interceptors
     *
     * @param interceptor interceptor
     * @return the web mvc interceptors
     * @since 1.0.0
     */
    public CommonInterceptors addHandlerInterceptor(HandlerInterceptor interceptor) {
        this.interceptorlist.add(interceptor);
        return this;
    }

    /**
     * Add check enhancers common interceptors
     *
     * @param checkEnhancers check enhancers
     * @return the common interceptors
     * @since 1.0.0
     */
    public CommonInterceptors addCheckEnhancers(List<CaptchaCheckEnhancer> checkEnhancers) {
        this.checkEnhancers = checkEnhancers;
        return this;
    }
}
