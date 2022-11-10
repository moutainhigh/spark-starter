package info.spark.feign.adapter.config;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.DummyPing;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PollingServerListUpdater;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;

import org.springframework.context.annotation.Bean;

/**
 * <p>Description: 自定义ribbon策略可以覆盖 RibbonClientConfiguration 中的默认配置
 * 1. 自定义的 Ribbon 配置不能放在 SpringApplication 的同级或下级目录中会覆盖所有的 Ribbon 客户端配置,所有的客户端都使用了相同的配置;
 * 2. 自定义的 Ribbon 配置不能放在 ComponentScan 的目录中否则次配置会覆盖所有的 Ribbon 客户端配置,所有的客户端都使用了相同的配置;
 * </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.29 19:03
 * @since 1.0.0
 */
public class RibbonClientConfig {

    /** DEFAULT_CONNECT_TIMEOUT */
    public static final int DEFAULT_CONNECT_TIMEOUT = 1000;
    /** DEFAULT_READ_TIMEOUT */
    public static final int DEFAULT_READ_TIMEOUT = 1000;

    /**
     * Ribbon rule rule
     *
     * @param config config
     * @return the rule
     * @since 1.0.0
     */
    @Bean
    public IRule ribbonRule(IClientConfig config) {
        //轮询策略
        ZoneAvoidanceRule rule = new ZoneAvoidanceRule();
        rule.initWithNiwsConfig(config);
        return rule;
    }

    /**
     * Ribbon ping ping
     * 检测服务是否存活
     *
     * @param config config
     * @return the ping
     * @since 1.0.0
     */
    @Bean
    public IPing ribbonPing(IClientConfig config) {
        return new DummyPing();
    }

    /* 服务列保, 覆盖此方法,必须在yml/properties文件中配置服务列表
    @Bean
    public ServerList<Server> ribbonServerList(IClientConfig config) {
        ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
        serverList.initWithNiwsConfig(config);
        return serverList;
    }
    */

    /**
     * 服务列表更新器
     *
     * @param config config
     * @return the server list updater
     * @since 1.0.0
     */
    @Bean
    public ServerListUpdater ribbonServerListUpdater(IClientConfig config) {
        return new PollingServerListUpdater(config);
    }

    /**
     * 负载均衡器
     *
     * @param config            config
     * @param serverList        server list
     * @param serverListFilter  server list filter
     * @param rule              rule
     * @param ping              ping
     * @param serverListUpdater server list updater
     * @return the load balancer
     * @since 1.0.0
     */
    @Bean
    @SuppressWarnings("checkstyle:ParameterNumber")
    public ILoadBalancer ribbonLoadBalancer(IClientConfig config,
                                            ServerList<Server> serverList,
                                            ServerListFilter<Server> serverListFilter,
                                            IRule rule, IPing ping,
                                            ServerListUpdater serverListUpdater) {
        return new ZoneAwareLoadBalancer<>(config,
                                           rule,
                                           ping,
                                           serverList,
                                           serverListFilter,
                                           serverListUpdater);
    }

}
