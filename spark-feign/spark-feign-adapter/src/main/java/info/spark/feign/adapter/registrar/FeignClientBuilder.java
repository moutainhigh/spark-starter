package info.spark.feign.adapter.registrar;

import info.spark.feign.adapter.util.FeignUtils;

import org.jetbrains.annotations.Contract;
import org.springframework.context.ApplicationContext;

/**
 * <p>Description: 用于在不使用 @FeignClient 的情况下创建 Feign client 的生成器.
 * 这个构建器构建Feign客户机的方式与使用Feign client注释创建Feign客户机的方式完全相同
 * 注意: 使用 builder 创建的 client 不是单例的
 * </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 02:20
 * @since 1.0.0
 */
public class FeignClientBuilder {

    /** Application context */
    private final ApplicationContext applicationContext;

    /**
     * Feign client builder
     *
     * @param applicationContext application context
     * @since 1.0.0
     */
    @Contract(pure = true)
    public FeignClientBuilder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * For type builder
     *
     * @param <T>  parameter
     * @param type type
     * @param name name
     * @return the builder
     * @since 1.0.0
     */
    public <T> Builder<T> forType(Class<T> type, String name) {
        return new Builder<>(this.applicationContext, type, name);
    }

    /**
         * <p>Description: </p>
     *
     * @param <T> parameter
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.19 02:25
     * @since 1.0.0
     */
    public static final class Builder<T> {

        /** Feign client factory bean */
        private final FeignClientFactoryBean<T> feignClientFactoryBean;

        /**
         * Builder
         *
         * @param applicationContext application context
         * @param type               type
         * @param name               name
         * @since 1.0.0
         */
        private Builder(ApplicationContext applicationContext,
                        Class<T> type,
                        String name) {
            this.feignClientFactoryBean = new FeignClientFactoryBean<>();
            this.feignClientFactoryBean.setApplicationContext(applicationContext);
            this.feignClientFactoryBean.setType(type);
            this.feignClientFactoryBean.setName(FeignUtils.getName(name));
            this.feignClientFactoryBean.setContextId(FeignUtils.getName(name));
            Builder<T> builder = this.url("").path("").decode404(false).fallback(Void.class).fallbackFactory(Void.class);
        }

        /**
         * Url builder
         *
         * @param url url
         * @return the builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        public Builder<T> url(String url) {
            this.feignClientFactoryBean.setUrl(FeignUtils.getUrl(url));
            return this;
        }

        /**
         * Context id builder
         *
         * @param contextId context id
         * @return the builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        public Builder<T> contextId(String contextId) {
            this.feignClientFactoryBean.setContextId(contextId);
            return this;
        }

        /**
         * Path builder
         *
         * @param path path
         * @return the builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        public Builder<T> path(String path) {
            this.feignClientFactoryBean.setPath(FeignUtils.getPath(path));
            return this;
        }

        /**
         * Decode 404 builder
         *
         * @param decode404 decode 404
         * @return the builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        public Builder<T> decode404(boolean decode404) {
            this.feignClientFactoryBean.setDecode404(decode404);
            return this;
        }

        /**
         * Fallback builder
         *
         * @param fallback fallback
         * @return the builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        public Builder<T> fallback(Class<?> fallback) {
            FeignUtils.validateFallback(fallback);
            this.feignClientFactoryBean.setFallback(fallback);
            return this;
        }

        /**
         * Fallback factory builder
         *
         * @param fallbackFactory fallback factory
         * @return the builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        public Builder<T> fallbackFactory(Class<?> fallbackFactory) {
            FeignUtils.validateFallbackFactory(fallbackFactory);
            this.feignClientFactoryBean.setFallbackFactory(fallbackFactory);
            return this;
        }

        /**
         * Build t
         *
         * @return the created Feign client
         * @since 1.0.0
         */
        public T build() {
            return this.feignClientFactoryBean.getTarget();
        }
    }
}
