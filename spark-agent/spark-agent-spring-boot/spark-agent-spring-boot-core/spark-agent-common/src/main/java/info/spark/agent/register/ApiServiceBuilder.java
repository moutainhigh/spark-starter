package info.spark.agent.register;


import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.Contract;
import org.springframework.context.ApplicationContext;

/**
 * <p>Description: 用于在不使用 @ApiService 的情况下创建 ApiService 的生成器.
 * 注意:
 * 1. 使用 builder 创建的 ApiService 不是单例的;
 * 2. version 全部都是 1.0.0
 * 3. api name 为全类名
 * </p>
 *
 * @author dong4j
 * @version 1.0.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 02:20
 * @since 1.0.0
 */
public class ApiServiceBuilder {

    /** Application context */
    private final ApplicationContext applicationContext;

    /**
     * Feign client builder
     *
     * @param applicationContext application context
     * @since 1.0.0
     */
    @Contract(pure = true)
    public ApiServiceBuilder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * For type builder
     *
     * @param <T>  parameter
     * @param type type
     * @return the builder
     * @since 1.0.0
     */
    public <T> Builder<T> forType(Class<T> type) {
        return this.forType(type, StringUtils.firstCharToLower(type.getSimpleName()));
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
        return this.forType(type, name, type.getName());
    }

    /**
     * For type builder
     *
     * @param <T>     parameter
     * @param type    type
     * @param name    name
     * @param apiName api name
     * @return the builder
     * @since 1.0.0
     */
    public <T> Builder<T> forType(Class<T> type, String name, String apiName) {
        return new Builder<>(this.applicationContext, type, name, apiName);
    }

    /**
         * <p>Description: </p>
     *
     * @param <T> parameter
     * @author dong4j
     * @version 1.0.4
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.19 02:25
     * @since 1.0.0
     */
    public static final class Builder<T> {

        /** Feign client factory bean */
        private final AgentServiceFactoryBean<T> feignClientFactoryBean;

        /**
         * Builder
         *
         * @param applicationContext application context
         * @param type               type
         * @param name               name
         * @param apiName            api name
         * @since 1.0.0
         */
        private Builder(ApplicationContext applicationContext,
                        Class<T> type,
                        String name,
                        String apiName) {
            this.feignClientFactoryBean = new AgentServiceFactoryBean<>();
            this.feignClientFactoryBean.setApplicationContext(applicationContext);
            this.feignClientFactoryBean.setType(type);
            this.feignClientFactoryBean.setName(name);
            this.feignClientFactoryBean.setApiName(apiName);
        }

        /**
         * Build t
         *
         * @return the created Feign client
         * @throws Exception exception
         * @since 1.0.0
         */
        public T build() throws Exception {
            return this.feignClientFactoryBean.getTarget();
        }
    }
}
