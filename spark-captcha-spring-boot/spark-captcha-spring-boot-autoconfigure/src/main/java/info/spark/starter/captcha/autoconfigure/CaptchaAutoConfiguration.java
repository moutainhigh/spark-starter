package info.spark.starter.captcha.autoconfigure;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import info.spark.starter.basic.util.StringPool;
import info.spark.starter.captcha.AbstractCaptcha;
import info.spark.starter.captcha.CaptchaCache;
import info.spark.starter.captcha.CaptchaCustomizer;
import info.spark.starter.captcha.DefaultCaptcha;
import info.spark.starter.captcha.ICaptcha;
import info.spark.starter.captcha.LocalMemoryCaptchaCache;
import info.spark.starter.captcha.constant.CaptchaConstant;
import info.spark.starter.captcha.enhancer.AbortedCheckEnhancer;
import info.spark.starter.captcha.enhancer.CaptchaCheckEnhancer;
import info.spark.starter.captcha.enhancer.FrequencyCheckEnhancer;
import info.spark.starter.captcha.entity.CaptchaConfig;
import info.spark.starter.captcha.entity.CaptchaEnhancerEntity;
import info.spark.starter.captcha.filter.BlockingRules;
import info.spark.starter.captcha.filter.CaptchaCodeFilter;
import info.spark.starter.captcha.interceptor.CaptchaSecurityInterceptor;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.ObjectUtils;

import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.19 15:56
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaAutoConfiguration implements SparkAutoConfiguration {

    /** Properties */
    private final CaptchaProperties properties;

    /**
     * Instantiates a new Kaptcha auto configuration.
     *
     * @param properties the properties
     * @since 1.0.0
     */
    @Contract(pure = true)
    public CaptchaAutoConfiguration(CaptchaProperties properties) {
        this.properties = properties;
    }

    /**
     * 检查配置: 配置了 type 就必须配置 check 类型, 如果配置了 check 类型, type 不用配置, 默认就是 动态 (DYNAMIC)
     *
     * @since 1.0.0
     */
    @Override
    public void execute() {
        if (this.properties.getFilter() == null) {
            log.warn("使用了验证码组件, 但是未配置 spark.captcha.filter, 将忽略验证码检查");
        }
        if (this.properties.getType() != null) {
            if (this.properties.getType().equals(CaptchaConfig.CaptchaType.DYNAMIC)
                && ObjectUtils.isNull(this.properties.getChecks())) {
                throw BaseCodes.CONFIG_ERROR.newException("开启动态验证码, 未配置 spark.captcha.checks");
            } else if (this.properties.getType().equals(CaptchaConfig.CaptchaType.COMMON)
                       && ObjectUtils.isNotNull(this.properties.getChecks())) {
                throw BaseCodes.CONFIG_ERROR.newException("开启全局验证码, 不需要配置 spark.captcha.checks");
            }
        }

    }

    /**
     * Default kaptcha.
     *
     * @return the default kaptcha
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(DefaultKaptcha.class)
    public DefaultKaptcha defaultKaptcha() {
        Properties prop = new Properties();

        prop.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, String.valueOf(this.properties.getWidth()));
        prop.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, String.valueOf(this.properties.getHeight()));

        CaptchaProperties.Content content = this.properties.getContent();
        prop.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, content.getSource());
        prop.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, String.valueOf(content.getLength()));
        prop.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE, String.valueOf(content.getSpace()));

        CaptchaProperties.BackgroundColor backgroundColor = this.properties.getBackgroundColor();
        prop.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_FROM, backgroundColor.getFrom());
        prop.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_TO, backgroundColor.getTo());

        CaptchaProperties.Border border = this.properties.getBorder();
        prop.setProperty(Constants.KAPTCHA_BORDER, border.getEnabled() ? "yes" : "no");
        prop.setProperty(Constants.KAPTCHA_BORDER_COLOR, border.getColor());
        prop.setProperty(Constants.KAPTCHA_BORDER_THICKNESS, String.valueOf(border.getThickness()));

        CaptchaProperties.Font font = this.properties.getFont();
        prop.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, font.getName());
        prop.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, String.valueOf(font.getSize()));
        prop.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, font.getColor());

        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(new Config(prop));
        return defaultKaptcha;
    }

    /**
     * 默认使用 google 验证码 Kaptcha, 如需替换只需要继承 {@link AbstractCaptcha} , 且替换此 bean
     *
     * @param defaultKaptcha the default kaptcha
     * @param captchacache   captchacache
     * @return the kaptcha
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(ICaptcha.class)
    public AbstractCaptcha captcha(DefaultKaptcha defaultKaptcha, CaptchaCache captchacache) {
        DefaultCaptcha defaultCaptcha = new DefaultCaptcha(defaultKaptcha, this.properties.getExpireTime());
        defaultCaptcha.setType(this.properties.getType());
        defaultCaptcha.setCaptchaCache(captchacache);
        return defaultCaptcha;
    }

    /**
     * 验证码拦截规则
     *
     * @return the blocking rules
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(BlockingRules.class)
    public BlockingRules blockingRules() {
        return new BlockingRules() {};
    }

    /**
     * Load balanced rest template initializer deprecated smart initializing singleton
     *
     * @param customizers customizers
     * @param captcha     captcha
     * @return the smart initializing singleton
     * @since 1.0.0
     */
    @Bean
    public SmartInitializingSingleton captchaSmartInitializingSingleton(List<CaptchaCustomizer> customizers,
                                                                        AbstractCaptcha captcha) {
        return () -> {
            for (CaptchaCustomizer customizer : customizers) {
                customizer.customize(captcha);
            }
        };
    }

    /**
     * Aborted check captcha customizer captcha customizer
     *
     * @param abortedCheckEnhancer aborted check enhancer
     * @return the captcha customizer
     * @since 1.0.0
     */
    @Bean
    @Conditional(value = AbortedCaptchaCondition.class)
    public CaptchaCustomizer abortedCheckCaptchaCustomizer(CaptchaCheckEnhancer abortedCheckEnhancer) {
        return captcha -> captcha.getCheckEnhancers().add(abortedCheckEnhancer);
    }

    /**
     * Captchacache captcha cache
     *
     * @return the captcha cache
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(CaptchaCache.class)
    public CaptchaCache captchacache() {
        return new LocalMemoryCaptchaCache(CaptchaEnhancerEntity.builder()
                                               .captchaExpiresTime(this.properties.getExpireTime())
                                               .requestCount(this.properties.getRequestCount())
                                               .requestCountExpiresTime(this.properties.getRequestCountExpiresTime())
                                               .requestFailedCount(this.properties.getRequestFailedCount())
                                               .requestFailedCountExpiresTime(this.properties.getRequestFailedCountExpiresTime())
                                               .build());
    }

    /**
     * Aborted check enhancer captcha check enhancer
     *
     * @param captchacache captchacache
     * @return the captcha check enhancer
     * @since 1.0.0
     */
    @Bean
    @Conditional(value = AbortedCaptchaCondition.class)
    public CaptchaCheckEnhancer abortedCheckEnhancer(CaptchaCache captchacache) {
        AbortedCheckEnhancer abortedCheckEnhancer = new AbortedCheckEnhancer(this.properties.getRequestFailedCount());
        abortedCheckEnhancer.setCaptchacache(captchacache);
        return abortedCheckEnhancer;
    }

    /**
     * Frequency captcha customizer captcha customizer
     *
     * @param frequencyCheckEnhancer frequency check enhancer
     * @return the captcha customizer
     * @since 1.0.0
     */
    @Bean
    @Conditional(value = FrequencyCaptchaCondition.class)
    public CaptchaCustomizer frequencyCaptchaCustomizer(CaptchaCheckEnhancer frequencyCheckEnhancer) {
        return captcha -> captcha.getCheckEnhancers().add(frequencyCheckEnhancer);
    }

    /**
     * Frequency check enhancer captcha check enhancer
     *
     * @param captchacache captchacache
     * @return the captcha check enhancer
     * @since 1.0.0
     */
    @Bean
    @Conditional(value = FrequencyCaptchaCondition.class)
    public CaptchaCheckEnhancer frequencyCheckEnhancer(CaptchaCache captchacache) {
        FrequencyCheckEnhancer frequencyCheckEnhancer = new FrequencyCheckEnhancer(this.properties.getRequestCount());
        frequencyCheckEnhancer.setCaptchacache(captchacache);
        return frequencyCheckEnhancer;
    }

    /**
     * 默认开启验证码认证拦截器
     *
     * @param blockingRules blocking rules
     * @param captcha       captcha
     * @return the once per request filter
     * @since 1.0.0
     */
    @Bean
    public FilterRegistrationBean<CaptchaCodeFilter> validateCodeFilter(BlockingRules blockingRules, AbstractCaptcha captcha) {
        log.debug("加载验证码处理器: [{}]", CaptchaCodeFilter.class);
        FilterRegistrationBean<CaptchaCodeFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new CaptchaCodeFilter(blockingRules, this.properties.getFilter(), captcha));
        filterRegistrationBean.addUrlPatterns(StringPool.ANY_URL_PATTERNS);
        filterRegistrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return filterRegistrationBean;
    }

    /**
     * Captcha interceptor captcha interceptor
     *
     * @param enhancers enhancers
     * @return the captcha interceptor
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(value = "spark.captcha.type", havingValue = "dynamic")
    public CaptchaSecurityInterceptor captchaInterceptor(List<CaptchaCheckEnhancer> enhancers) {
        if (CollectionUtils.isEmpty(enhancers)) {
            enhancers = Collections.emptyList();
        }
        return new CaptchaSecurityInterceptor(enhancers);
    }

    /**
     * Common interceptors common interceptors
     *
     * @param captchaSecurityInterceptor captcha interceptor
     * @return the common interceptors
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(value = "spark.captcha.type", havingValue = "dynamic")
    public CommonInterceptors commonInterceptors(CaptchaSecurityInterceptor captchaSecurityInterceptor) {
        // 拦截路径
        List<String> includePaths = new ArrayList<>();
        this.properties.getFilter().forEach(f -> includePaths.add(f.getUri()));
        // 不拦截路径
        List<String> excludePaths = new ArrayList<>();
        excludePaths.add(CaptchaConstant.CAPTCHA_URL);

        return new CommonInterceptors()
            .setInpathPatterns(includePaths)
            .setExpathPatterns(excludePaths)
            .addHandlerInterceptor(captchaSecurityInterceptor);
    }

    /**
         * <p>Description: 注入 {@link AbortedCheckEnhancer} 的条件 </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.21 19:59
     * @since 1.0.0
     */
    private static class AbortedCaptchaCondition extends CaptchaCondition {

        /**
         * Aborted captcha condition
         *
         * @since 1.0.0
         */
        AbortedCaptchaCondition() {
            super(CaptchaConfig.CheckType.ABORTED);
        }
    }

    /**
         * <p>Description: 注入 {@link FrequencyCheckEnhancer} 的条件 </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.21 19:59
     * @since 1.0.0
     */
    private static class FrequencyCaptchaCondition extends CaptchaCondition {

        /**
         * Frequency captcha condition
         *
         * @since 1.0.0
         */
        FrequencyCaptchaCondition() {
            super(CaptchaConfig.CheckType.FREQUENCY);
        }
    }
}
