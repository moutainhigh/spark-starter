package info.spark.starter.cache.autoconfigure;

import com.alicp.jetcache.AbstractCacheBuilder;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheBuilder;
import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.support.SpringConfigProvider;
import com.alicp.jetcache.autoconfigure.AutoConfigureBeans;
import com.alicp.jetcache.autoconfigure.CaffeineAutoConfiguration;
import com.alicp.jetcache.autoconfigure.JetCacheAutoConfiguration;
import com.alicp.jetcache.autoconfigure.LinkedHashMapAutoConfiguration;
import com.alicp.jetcache.autoconfigure.RedisLettuceAutoConfiguration;
import com.alicp.jetcache.external.ExternalCacheBuilder;
import com.alicp.jetcache.support.StatInfo;
import com.alicp.jetcache.support.StatInfoLogger;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.cache.KeyExpirationListenerAdapter;
import info.spark.starter.cache.service.CacheService;
import info.spark.starter.cache.service.impl.CaffeineCacheServiceImpl;
import info.spark.starter.cache.service.impl.LinkedHashMapCacheServiceImpl;
import info.spark.starter.cache.service.impl.RedisCacheServiceImpl;
import info.spark.starter.cache.support.FastjsonKeyConvertor;
import info.spark.starter.cache.support.JacksonValueDecoder;
import info.spark.starter.cache.support.JacksonValueEncoder;
import info.spark.starter.cache.support.JacksonValueSerialPolicy;
import info.spark.starter.cache.support.ProtobufValueDecoder;
import info.spark.starter.cache.support.ProtobufValueEncoder;
import info.spark.starter.cache.support.ProtobufValueSerialPolicy;
import info.spark.starter.common.start.SparkAutoConfiguration;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.function.Consumer;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.11 10:09
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableCreateCacheAnnotation
@Import(value = {
    LettuceConnectionConfiguration.class,
    RedisKeyExpirationAutoConfiguration.class
})
@EnableConfigurationProperties(CacheProperties.class)
@AutoConfigureAfter(JetCacheAutoConfiguration.class)
public class CacheAutoConfiguration implements SparkAutoConfiguration {
    /** INIT_MESSAGE */
    private static final String INIT_MESSAGE = "?????????: [{}]";

    /**
     * ??????????????????/????????????
     *
     * @param jacksonValueDecoder jackson value decoder
     * @param jacksonValueEncoder jackson value encoder
     * @return the spring config provider
     * @since 1.0.0
     */
    @Bean
    public SpringConfigProvider springConfigProvider(JacksonValueDecoder jacksonValueDecoder,
                                                     JacksonValueEncoder jacksonValueEncoder) {
        return new SpringConfigProvider() {
            @Override
            public Function<Object, byte[]> parseValueEncoder(String valueEncoder) {
                if (valueEncoder.equalsIgnoreCase(JacksonValueSerialPolicy.JACKSON)) {
                    return jacksonValueEncoder;
                } else {
                    return super.parseValueEncoder(valueEncoder);
                }
            }

            @Override
            public Function<byte[], Object> parseValueDecoder(String valueDecoder) {
                if (valueDecoder.equalsIgnoreCase(JacksonValueSerialPolicy.JACKSON)) {
                    return jacksonValueDecoder;
                } else {
                    return super.parseValueDecoder(valueDecoder);
                }
            }

            @Override
            public Consumer<StatInfo> statCallback() {
                // ???????????????logger
                return new StatInfoLogger(false);
            }
        };
    }

    /**
     * ?????? RedisTemplate, key ?????? Fashjson ?????????
     *
     * @param connectionFactory        connection factory
     * @param jacksonValueSerialPolicy jackson value serial policy
     * @return the redis template
     * @since 1.0.0
     */
    @Bean
    @Conditional(RedisLettuceAutoConfiguration.RedisLettuceCondition.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       JacksonValueSerialPolicy jacksonValueSerialPolicy) {

        log.debug(INIT_MESSAGE, RedisTemplate.class);
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        ObjectMapper objectMapper = JsonUtils.getCopyMapper();
        // ?????? Jackson ???????????????????????????????????????
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // ?????????????????? class
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        // key??????String??????????????????
        template.setKeySerializer(FastjsonKeyConvertor.INSTANCE);
        // hash???key?????????String??????????????????
        template.setHashKeySerializer(new StringRedisSerializer());
        // value?????????????????????jackson
        template.setValueSerializer(jacksonValueSerialPolicy);
        // hash???value?????????????????????jackson
        template.setHashValueSerializer(jacksonValueSerialPolicy);
        template.setDefaultSerializer(jacksonValueSerialPolicy);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redis CacheService ??????, ???????????????????????????, ???????????? RedisCacheService ?????? CacheService ?????????
     *
     * @param redisTemplate                redis template
     * @param keyExpirationListenerAdapter key expiration listener adapter
     * @param autoConfigureBeans           auto configure beans
     * @return the redis service
     * @since 1.0.0
     */
    @Bean
    @Primary
    @SuppressWarnings("all")
    @Conditional(RedisLettuceAutoConfiguration.RedisLettuceCondition.class)
    public CacheService redisCacheService(@NotNull RedisTemplate<String, Object> redisTemplate,
                                          KeyExpirationListenerAdapter keyExpirationListenerAdapter,
                                          @NotNull AutoConfigureBeans autoConfigureBeans) {

        log.debug(INIT_MESSAGE, RedisCacheServiceImpl.class);

        CacheBuilder cacheBuilder = autoConfigureBeans.getRemoteCacheBuilders().get("default");
        ExternalCacheBuilder acb = (ExternalCacheBuilder) cacheBuilder;
        // ???????????????????????? FastjsonKeyConvertor
        acb.setKeyConvertor(FastjsonKeyConvertor.INSTANCE);
        acb.setKeyPrefix("");
        return new RedisCacheServiceImpl(cacheBuilder.buildCache(), redisTemplate, keyExpirationListenerAdapter);

    }

    /**
     * ????????????????????????????????????????????? cache ??????????????????
     *
     * @param autoConfigureBeans auto configure beans
     * @return the cache service
     * @since 1.6.0
     */
    @Bean
    @SuppressWarnings("all")
    @Conditional(CaffeineAutoConfiguration.CaffeineCondition.class)
    public CacheService caffeineCacheService(@NotNull AutoConfigureBeans autoConfigureBeans) {
        log.debug(INIT_MESSAGE, CaffeineCacheServiceImpl.class);

        Cache<String, Object> cache = buildCache(autoConfigureBeans);
        return new CaffeineCacheServiceImpl(cache);
    }

    /**
     * ????????????????????????????????????????????? cache ??????????????????
     *
     * @param autoConfigureBeans auto configure beans
     * @return the cache service
     * @since 1.6.0
     */
    @Bean
    @SuppressWarnings("all")
    @Conditional(LinkedHashMapAutoConfiguration.LinkedHashMapCondition.class)
    public CacheService linkedHashMapCacheService(@NotNull AutoConfigureBeans autoConfigureBeans) {
        log.debug(INIT_MESSAGE, LinkedHashMapCacheServiceImpl.class);

        Cache<String, Object> cache = buildCache(autoConfigureBeans);
        return new LinkedHashMapCacheServiceImpl(cache);
    }

    /**
     * Build cache
     *
     * @param autoConfigureBeans auto configure beans
     * @return the cache
     * @since 1.6.0
     */
    @SuppressWarnings("all")
    private Cache<String, Object> buildCache(@NotNull AutoConfigureBeans autoConfigureBeans) {
        CacheBuilder cacheBuilder = autoConfigureBeans.getLocalCacheBuilders().get("default");
        AbstractCacheBuilder acb = (AbstractCacheBuilder) cacheBuilder;
        // ???????????????????????? FastjsonKeyConvertor
        acb.setKeyConvertor(FastjsonKeyConvertor.INSTANCE);
        return cacheBuilder.buildCache();
    }

    /**
         * <p>Description: jackson ??????????????????????????????</p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.19 00:35
     * @since 1.0.0
     */
    @Configuration(proxyBeanMethods = false)
    static class JacksonSerializeAutoConfiguration {
        /**
         * Jackson value decoder jackson value decoder
         *
         * @return the jackson value decoder
         * @since 1.0.0
         */
        @Bean
        public JacksonValueDecoder jacksonValueDecoder() {
            return new JacksonValueDecoder();
        }

        /**
         * Jackson value encoder jackson value encoder
         *
         * @return the jackson value encoder
         * @since 1.0.0
         */
        @Bean
        public JacksonValueEncoder jacksonValueEncoder() {
            return new JacksonValueEncoder();
        }

        /**
         * Jackson value serial policy jackson value serial policy
         *
         * @param jacksonValueDecoder jackson value decoder
         * @param jacksonValueEncoder jackson value encoder
         * @return the jackson value serial policy
         * @since 1.0.0
         */
        @Bean
        public JacksonValueSerialPolicy jacksonValueSerialPolicy(JacksonValueDecoder jacksonValueDecoder,
                                                                 JacksonValueEncoder jacksonValueEncoder) {
            return new JacksonValueSerialPolicy(jacksonValueDecoder, jacksonValueEncoder);
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.19 00:36
     * @since 1.0.0
     */
    @Configuration(proxyBeanMethods = false)
    static class ProtobufSerializeAutoConfiguration {
        /**
         * Protobuf value decoder protobuf value decoder
         *
         * @return the protobuf value decoder
         * @since 1.0.0
         */
        @Bean
        public ProtobufValueDecoder protobufValueDecoder() {
            return new ProtobufValueDecoder();
        }

        /**
         * Jackson value encoder jackson value encoder
         *
         * @return the jackson value encoder
         * @since 1.0.0
         */
        @Bean
        public ProtobufValueEncoder protobufValueEncoder() {
            return new ProtobufValueEncoder();
        }


        /**
         * Protobuf value serial policy protobuf value serial policy
         *
         * @param protobufValueDecoder protobuf value decoder
         * @param protobufValueEncoder protobuf value encoder
         * @return the protobuf value serial policy
         * @since 1.0.0
         */
        @Bean
        public ProtobufValueSerialPolicy protobufValueSerialPolicy(ProtobufValueDecoder protobufValueDecoder,
                                                                   ProtobufValueEncoder protobufValueEncoder) {
            return new ProtobufValueSerialPolicy(protobufValueDecoder, protobufValueEncoder);
        }
    }

}
