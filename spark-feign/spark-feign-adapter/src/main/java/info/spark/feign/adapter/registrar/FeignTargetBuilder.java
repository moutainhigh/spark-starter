package info.spark.feign.adapter.registrar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.client.ClientFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.JsonUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;

import feign.Client;
import feign.Feign;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Util;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.ribbon.LBClient;
import feign.ribbon.RibbonClient;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.04 20:36
 * @since 1.0.0
 */
@Slf4j
public final class FeignTargetBuilder {
    /** Application context */
    private static ApplicationContext context;

    /**
     * Feign builder
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    private FeignTargetBuilder() {
    }

    /**
     * Gets instance *
     *
     * @param applicationContext application context
     * @return the instance
     * @since 1.0.0
     */
    static Feign.Builder getInstance(ApplicationContext applicationContext) {
        context = applicationContext;
        return FeignTargetHolder.INSTANCE;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.2.4
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.04 21:33
     * @since 1.0.0
     */
    private static class FeignTargetHolder {
        /** INSTANCE */
        private static final Feign.Builder INSTANCE = new FeignTargetBuilder().builder();

    }

    /**
     * Build feign client feign . builder.
     *
     * @return the feign . builder
     * @since 1.0.0
     */
    Feign.Builder builder() {
        Feign.Builder builder = Feign.builder()
            .client(this.robbinClient())
            .logger(new Slf4jLogger())
            .logLevel(Slf4jLogger.Level.FULL)
            .encoder(new SpringFormEncoder(new CustomizeJacksonEncoder()))
            .decoder(new JacksonDecoder(JsonUtils.getCopyMapper()));

        Map<String, RequestInterceptor> beansOfType = context.getBeansOfType(RequestInterceptor.class);
        if (!CollectionUtils.isEmpty(beansOfType)) {
            beansOfType.forEach((k, v) -> builder.requestInterceptor(v));
        }

        return builder;
    }

    /**
     * 使用 ribbon 客户端进行请求负载
     * todo-dong4j : (2020年01月19日 02:51) [注册为 bean]
     *
     * @return the client
     * @since 1.0.0
     */
    @NotNull
    private Client robbinClient() {
        this.loadProperties();

        okhttp3.OkHttpClient client = this.okHttpClient();
        // 自定义负载均衡, 默认是轮训
        return RibbonClient.builder()
            .delegate(new OkHttpClient(client))
            .lbClientFactory(clientName -> {
                IClientConfig config = ClientFactory.getNamedConfig(clientName);
                ILoadBalancer lb = ClientFactory.getNamedLoadBalancer(clientName);
                ZoneAwareLoadBalancer<?> zb = (ZoneAwareLoadBalancer<?>) lb;
                // 使用随机负载 todo-dong4j : (2019年11月06日 18:06) [添加更多的负载策略, 使用配置选择]
                zb.setRule(new AvailabilityFilteringRule());
                return LBClient.create(lb, config);
            }).build();
    }

    /**
     * 加载 ribbon 的配置
     *
     * @since 1.0.0
     */
    private void loadProperties() {
        Properties properties = new Properties();

        properties.setProperty(ConfigKey.RibbonConfigKey.MAX_AUTO_RETRIES,
                               context.getEnvironment().getProperty(ConfigKey.FeignConfigKey.MAX_AUTO_RETRIES,
                                                                    "0"));
        properties.setProperty(ConfigKey.RibbonConfigKey.MAX_AUTO_RETRIES_NEXT_SERVER,
                               context.getEnvironment().getProperty(ConfigKey.FeignConfigKey.MAX_AUTO_RETRIES_NEXT_SERVER,
                                                                    "1"));
        properties.setProperty(ConfigKey.RibbonConfigKey.OK_TO_RETRY_ON_ALL_OPERATIONS,
                               context.getEnvironment().getProperty(ConfigKey.FeignConfigKey.OK_TO_RETRY_ON_ALL_OPERATIONS,
                                                                    ConfigDefaultValue.FALSE_STRING));
        properties.setProperty(ConfigKey.RibbonConfigKey.SERVER_LIST_REFRESH_INTERVAL,
                               context.getEnvironment().getProperty(ConfigKey.FeignConfigKey.SERVER_LIST_REFRESH_INTERVAL,
                                                                    "2000"));
        properties.setProperty(ConfigKey.RibbonConfigKey.CONNECT_TIMEOUT,
                               context.getEnvironment().getProperty(ConfigKey.FeignConfigKey.CONNECT_TIMEOUT,
                                                                    "5000"));
        properties.setProperty(ConfigKey.RibbonConfigKey.READ_TIMEOUT,
                               context.getEnvironment().getProperty(ConfigKey.FeignConfigKey.READ_TIMEOUT,
                                                                    "10000"));

        String listOfServers = context.getEnvironment().getProperty(ConfigKey.FeignConfigKey.RIBBON_SERVICE_LIST);
        if (!StringUtils.hasText(listOfServers)) {
            listOfServers = "127.0.0.1:18080";
            log.warn("未配置 [{}], 使用默认配置 [{}]", ConfigKey.FeignConfigKey.RIBBON_SERVICE_LIST, listOfServers);
        }
        properties.setProperty(ConfigKey.RibbonConfigKey.LIST_OF_SERVERS, listOfServers);

        ConfigurationManager.loadProperties(properties);
    }

    /**
     * todo-dong4j : (2019年12月26日 16:40) [对 okhttp 做自定义配置]
     *
     * @return the okhttp 3 . ok http client
     * @since 1.0.0
     */
    @NotNull
    @Contract(" -> new")
    private okhttp3.OkHttpClient okHttpClient() {
        return new okhttp3.OkHttpClient();
    }

    /**
         * <p>Description: 重写 JacksonEncoder 编码方式, 当 bodyType 为 byte[] 时直接写入 template body, 解决 feign client 传入 byte[] 编码错误的问题 </p>
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.02 22:26
     * @since 1.0.0
     */
    private static class CustomizeJacksonEncoder extends JacksonEncoder {
        /** Delegate */
        private final Encoder delegate;
        /** Mapper */
        private final ObjectMapper mapper = JsonUtils.getInstance();

        /**
         * Customize jackson encoder
         *
         * @since 1.0.0
         */
        CustomizeJacksonEncoder() {
            super(JsonUtils.getInstance());
            this.delegate = new Default();
        }

        /**
         * Encode *
         *
         * @param object   object
         * @param bodyType body type
         * @param template template
         * @throws EncodeException encode exception
         * @since 1.0.0
         */
        @Override
        public void encode(Object object, Type bodyType, @NotNull RequestTemplate template) throws EncodeException {
            try {
                if (bodyType == byte[].class) {
                    template.body(Request.Body.encoded((byte[]) object, UTF_8));
                } else {
                    JavaType javaType = this.mapper.getTypeFactory().constructType(bodyType);
                    template.body(Request.Body.create(this.mapper.writerFor(javaType).writeValueAsString(object), Util.UTF_8));
                }
            } catch (JsonProcessingException e) {
                this.delegate.encode(object, bodyType, template);
            }
        }
    }
}

