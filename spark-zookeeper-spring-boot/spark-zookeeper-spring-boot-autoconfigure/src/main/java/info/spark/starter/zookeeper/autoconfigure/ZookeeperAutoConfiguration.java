package info.spark.starter.zookeeper.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.zookeeper.ZookeeperService;
import info.spark.starter.zookeeper.DefaultZookeeperService;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.api.CompressionProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZookeeperFactory;
import org.apache.zookeeper.ZooKeeper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.04 22:32
 * @since 1.8.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = {ZooKeeper.class, CuratorFramework.class})
@ConditionalOnProperty(prefix = ZookeeperProperties.PREFIX, value = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperAutoConfiguration implements SparkAutoConfiguration, BeanFactoryAware {

    /** Zookeeper properties */
    private final ZookeeperProperties zookeeperProperties;
    /** Bean factory */
    private BeanFactory beanFactory;

    /**
     * Zookeeper auto configuration
     *
     * @param zookeeperProperties zookeeper properties
     * @since 1.8.0
     */
    public ZookeeperAutoConfiguration(ZookeeperProperties zookeeperProperties) {
        this.zookeeperProperties = zookeeperProperties;
    }

    /**
     * Curator framework
     *
     * @param retryPolicy retry policy
     * @return the curator framework
     * @since 1.8.0
     */
    @Bean(initMethod = "start", destroyMethod = "close")
    @ConditionalOnMissingBean(CuratorFramework.class)
    public CuratorFramework curatorFramework(RetryPolicy retryPolicy) {
        // 使用 builder 创建 client
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();

        // IMPORTANT: use either connection-string or ensembleProvider but not both.
        if (StringUtils.hasText(this.zookeeperProperties.getConnectString())) {
            // connection string will be first
            builder.connectString(this.zookeeperProperties.getConnectString());
        } else if (StringUtils.hasLength(this.zookeeperProperties.getEnsembleProviderRef())) {
            builder.ensembleProvider(this.beanFactory.getBean(this.zookeeperProperties.getEnsembleProviderRef(), EnsembleProvider
                .class));
        } else {
            throw new IllegalArgumentException("[Assertion failed] 'connection-string' must be configured.");
        }

        if (StringUtils.hasLength(this.zookeeperProperties.getAclProvider())) {
            builder.aclProvider(this.beanFactory.getBean(this.zookeeperProperties.getAclProvider(), ACLProvider.class));
        }

        // Add connection authorization
        if (StringUtils.hasText(this.zookeeperProperties.getAuthInfosRef())) {
            @SuppressWarnings("unchecked")
            List<AuthInfo> authInfos = this.beanFactory.getBean(this.zookeeperProperties.getAuthInfosRef(), List.class);
            builder.authorization(authInfos);
        } else if (StringUtils.hasText(this.zookeeperProperties.getScheme())
                   && StringUtils.hasText(this.zookeeperProperties.getAuthBase64Str())) {
            builder.authorization(this.zookeeperProperties.getScheme(),
                                  Base64Utils.decodeFromString(this.zookeeperProperties.getAuthBase64Str()));
        }

        // canBeReadOnly if true, allow ZooKeeper client to enter read only mode in case of a network partition
        if (this.zookeeperProperties.getCanBeReadOnly() != null) {
            builder.canBeReadOnly(this.zookeeperProperties.getCanBeReadOnly());
        }

        // 默认 false
        if (this.zookeeperProperties.getUseContainerParentsIfAvailable() != null
            && !this.zookeeperProperties.getUseContainerParentsIfAvailable()) {
            builder.dontUseContainerParents();
        }
        // 是否压缩
        if (StringUtils.hasLength(this.zookeeperProperties.getCompressionProviderRef())) {
            builder.compressionProvider(this.beanFactory.getBean(this.zookeeperProperties.getCompressionProviderRef(),
                                                                 CompressionProvider.class));
        }
        // 设置默认数据
        if (this.zookeeperProperties.getDefaultDataBase64Str() != null) {
            builder.defaultData(Base64Utils.decodeFromString(this.zookeeperProperties.getDefaultDataBase64Str()));
        }

        // 设置当前操作的节点
        if (StringUtils.hasText(this.zookeeperProperties.getNamespace())) {
            builder.namespace(this.zookeeperProperties.getNamespace());
        }

        // 重试策略
        if (null != retryPolicy) {
            builder.retryPolicy(retryPolicy);
        }

        if (null != this.zookeeperProperties.getSessionTimeOutMs()) {
            builder.sessionTimeoutMs(this.zookeeperProperties.getSessionTimeOutMs());
        }

        if (null != this.zookeeperProperties.getConnectionTimeoutMs()) {
            builder.connectionTimeoutMs(this.zookeeperProperties.getConnectionTimeoutMs());
        }

        if (null != this.zookeeperProperties.getMaxCloseWaitMs()) {
            builder.maxCloseWaitMs(this.zookeeperProperties.getMaxCloseWaitMs());
        }

        if (StringUtils.hasLength(this.zookeeperProperties.getThreadFactoryRef())) {
            builder.threadFactory(this.beanFactory.getBean(this.zookeeperProperties.getThreadFactoryRef(), ThreadFactory.class));
        }

        if (StringUtils.hasLength(this.zookeeperProperties.getZookeeperFactoryRef())) {
            builder.zookeeperFactory(this.beanFactory.getBean(this.zookeeperProperties.getZookeeperFactoryRef(), ZookeeperFactory
                .class));
        }

        log.info("Start curatorFramework -> {}, sessionTimeOutMs={}, connectionTimeoutMs={}",
                 this.zookeeperProperties.getConnectString(),
                 this.zookeeperProperties.getSessionTimeOutMs(),
                 this.zookeeperProperties.getConnectionTimeoutMs());

        return builder.build();
    }

    /**
     * Retry policy
     *
     * @return the retry policy
     * @since 1.8.0
     */
    @Bean
    @ConditionalOnMissingBean(RetryPolicy.class)
    public RetryPolicy retryPolicy() {
        return new ExponentialBackoffRetry(this.zookeeperProperties.getBaseSleepTimeMs(), this.zookeeperProperties.getMaxRetries());
    }

    /**
     * Zookeeper service
     *
     * @param curatorFramework curator framework
     * @return the zookeeper service
     * @since 1.8.0
     */
    @Bean
    @ConditionalOnMissingBean(ZookeeperService.class)
    public ZookeeperService zookeeperService(CuratorFramework curatorFramework) {
        return new DefaultZookeeperService(curatorFramework);
    }

    /**
     * Sets bean factory *
     *
     * @param beanFactory bean factory
     * @throws BeansException beans exception
     * @since 1.8.0
     */
    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
