package info.spark.starter.cache.autoconfigure;

import com.alicp.jetcache.autoconfigure.LettuceFactory;
import com.alicp.jetcache.autoconfigure.RedisLettuceAutoConfiguration;
import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.start.SparkAutoConfiguration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.resource.ClientResources;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.12 12:32
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CacheProperties.class)
@Conditional(RedisLettuceAutoConfiguration.RedisLettuceCondition.class)
public class LettuceConnectionConfiguration extends RedisConnectionConfiguration implements SparkAutoConfiguration {

    /** ????????????????????? area ????????? template */
    private final CacheProperties.Area defaultArea;

    /** Builder customizers */
    private final ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers;

    /**
     * Lettuce connection configuration
     *
     * @param cacheProperties    cache properties
     * @param builderCustomizers builder customizers
     * @since 1.0.0
     */
    LettuceConnectionConfiguration(@NotNull CacheProperties cacheProperties,
                                   ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers) {
        super(cacheProperties.getRemote().get("default"));
        this.defaultArea = cacheProperties.getRemote().get("default");
        this.builderCustomizers = builderCustomizers;
    }


    /**
     * Default client lettuce factory
     *
     * @return the lettuce factory
     * @since 1.0.0
     */
    @Bean(name = "defaultClient")
    @DependsOn(RedisLettuceAutoConfiguration.AUTO_INIT_BEAN_NAME)
    public LettuceFactory defaultClient() {
        return new LettuceFactory("remote.default", AbstractRedisClient.class);
    }

    /**
     * ??? bean ???????????????, ?????????????????? idea ????????? bean ?????????????????? IoC (Spring ??????????????? LettuceFactory ??????)
     *
     * @param lettuceFactory lettuce factory
     * @return the redis client
     * @throws Exception exception
     * @since 1.0.0
     */
    @Bean
    public AbstractRedisClient redisClient(@NotNull LettuceFactory lettuceFactory) throws Exception {
        return (AbstractRedisClient) lettuceFactory.getObject();
    }

    /**
     * Lettuce client resources client resources
     *
     * @param redisClient redis client
     * @return the client resources
     * @see LettuceConnectionConfiguration#defaultClient() LettuceConnectionConfiguration#defaultClient()
     * @since 1.0.0
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(ClientResources.class)
    public ClientResources lettuceClientResources(@NotNull AbstractRedisClient redisClient) {
        if (redisClient instanceof RedisClient) {
            return ((RedisClient) redisClient).getResources();
        } else {
            return ((RedisClusterClient) redisClient).getResources();
        }
    }

    /**
     * Redis connection factory lettuce connection factory
     * todo-dong4j : (2020???02???12??? 12:43) [?????????????????? pool]
     *
     * @param clientResources client resources
     * @return the lettuce connection factory
     * @throws UnknownHostException unknown host exception
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public LettuceConnectionFactory redisConnectionFactory(ClientResources clientResources) {
        LettuceClientConfiguration clientConfig = this.getLettuceClientConfiguration(clientResources, this.defaultArea.getPoolConfig());
        return this.createLettuceConnectionFactory(clientConfig, this.defaultArea.getUri());
    }

    /**
     * Create lettuce connection factory lettuce connection factory
     *
     * @param clientConfiguration client configuration
     * @param uris                uris
     * @return the lettuce connection factory
     * @since 1.0.0
     */
    @Contract("_, _ -> new")
    @NotNull
    private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration,
                                                                    @NotNull List<String> uris) {

        List<RedisURI> uriList = uris.stream().map((u) -> RedisURI.create(URI.create(u))).collect(Collectors.toList());

        // ??????
        if (uriList.size() == 1 && uriList.get(0).getSentinels().isEmpty()) {
            return new LettuceConnectionFactory(this.getStandaloneConfig(uriList.get(0)), clientConfiguration);
        } else if (uriList.size() == 1 && !uriList.get(0).getSentinels().isEmpty()) {
            // ?????? (redis-sentinel://[password@]127.0.0.1:26379,127.0.0.1:26380,127.0.0.1:26381[/db]?sentinelMasterId=mymaster)
            return new LettuceConnectionFactory(this.getSentinelConfig(uriList.get(0)), clientConfiguration);
        } else if (uriList.size() > 1) {
            // ??????
            return new LettuceConnectionFactory(this.getClusterConfiguration(uriList), clientConfiguration);
        } else {
            throw new IllegalStateException(StrFormatter.format("url ????????????: {}", JsonUtils.toJson(uriList)));
        }

    }

    /**
     * Gets lettuce client configuration *
     *
     * @param clientResources client resources
     * @param pool            pool
     * @return the lettuce client configuration
     * @since 1.0.0
     */
    @NotNull
    private LettuceClientConfiguration getLettuceClientConfiguration(ClientResources clientResources, CacheProperties.Pool pool) {
        LettuceClientConfigurationBuilder builder = this.createBuilder(pool);
        builder.clientResources(clientResources);
        this.customize(builder);
        return builder.build();
    }

    /**
     * Create builder lettuce client configuration builder
     *
     * @param pool pool
     * @return the lettuce client configuration builder
     * @since 1.0.0
     */
    @Contract("null -> !null")
    private LettuceClientConfigurationBuilder createBuilder(CacheProperties.Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return new LettuceConnectionConfiguration.PoolBuilderFactory().createBuilder(pool);
    }

    /**
     * Customize *
     *
     * @param builder builder
     * @since 1.0.0
     */
    private void customize(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        this.builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    }

    /**
     * Inner class to allow optional commons-pool2 dependency.
     *
     * @author dong4j
     * @version 1.2.4
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.12 12:32
     * @since 1.0.0
     */
    private static class PoolBuilderFactory {

        /**
         * Create builder lettuce client configuration builder
         *
         * @param properties properties
         * @return the lettuce client configuration builder
         * @since 1.0.0
         */
        LettuceClientConfigurationBuilder createBuilder(CacheProperties.Pool properties) {
            return LettucePoolingClientConfiguration.builder().poolConfig(this.getPoolConfig(properties));
        }

        /**
         * Gets pool config *
         *
         * @param properties properties
         * @return the pool config
         * @since 1.0.0
         */
        private GenericObjectPoolConfig<?> getPoolConfig(@NotNull CacheProperties.Pool properties) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(properties.getMaxTotal());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getTimeBetweenEvictionRuns() != null) {
                config.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRuns().toMillis());
            }
            if (properties.getMaxWait() != null) {
                config.setMaxWaitMillis(properties.getMaxWait().toMillis());
            }
            return config;
        }

    }
}
