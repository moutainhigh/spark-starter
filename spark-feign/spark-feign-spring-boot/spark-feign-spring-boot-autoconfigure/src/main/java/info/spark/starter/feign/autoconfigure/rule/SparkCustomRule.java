package info.spark.starter.feign.autoconfigure.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import info.spark.starter.core.util.NetUtils;
import info.spark.starter.feign.autoconfigure.FeignClientProperties;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 自定义负载均衡策略</p>
 * 如果是本地开发, 则优先使用同一 IP 进行调用
 * 注意: 此规则不能被纳入 IOC 容器
 * todo-dong4j : (2019年08月15日 16:26) [根据当前启动环境设置规则]
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:20
 * @since 1.0.0
 */
@Slf4j
public class SparkCustomRule extends AbstractLoadBalancerRule {
    /** Feign client properties */
    private final FeignClientProperties feignClientProperties;

    /**
     * Spark custom rule
     *
     * @param feignClientProperties feign client properties
     * @since 1.0.0
     */
    public SparkCustomRule(FeignClientProperties feignClientProperties) {
        this.feignClientProperties = feignClientProperties;
    }

    /**
     * Init with niws config *
     *
     * @param clientConfig client config
     * @since 1.0.0
     */
    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
    }

    /**
     * 在choose方法中,自定义我们自己的规则,返回的Server就是具体选择出来的服务
     * 自己的规则: 按照轮询的规则,但是每个被轮询到的服务调用5次.
     * todo-dong4j : (2019年11月07日 10:21) [重构]
     *
     * @param o the o
     * @return server server
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("checkstyle:ReturnCount")
    public Server choose(Object o) {
        // 获取负载均衡器lb
        ILoadBalancer lb = this.getLoadBalancer();
        if (lb == null) {
            return null;
        }

        Server server = null;
        //noinspection LoopConditionNotUpdatedInsideLoop
        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            // 获取可用服务列表
            List<Server> upList = lb.getReachableServers();
            SparkCustomRule.log.debug("server: {}", upList);
            // 获取所有服务列表
            List<Server> allList = lb.getAllServers();
            int serverCount = allList.size();
            if (serverCount == 0) {
                return null;
            }

            String devAddr = this.feignClientProperties.getDevAddr();
            if (StringUtils.isBlank(devAddr)) {
                // 获取本地 ip (排除虚拟网卡和多网卡, 实在不行就只有指定 spark.feign.dev-addr 了)
                devAddr = NetUtils.getLocalHost();
            }

            String[] ipAndPort = devAddr != null ? devAddr.split(":") : new String[0];
            String ip = ipAndPort[0];
            String port = "";
            if (ipAndPort.length > 1) {
                port = ipAndPort[1];
            }

            List<Server> matchIpList = upList
                .stream()
                .filter(remoteServer -> ip.equals(remoteServer.getHost()))
                .collect(Collectors.toList());

            if (matchIpList.isEmpty()) {
                SparkCustomRule.log.error("选择的服务不存在: {}", devAddr);
                return null;
            }

            // 如果没有设置 port 则返回第一个服务
            if (StringUtils.isBlank(port)) {
                return matchIpList.get(0);
            }

            String finalDevAddr = devAddr;
            List<Server> matchPortList = upList
                .stream()
                .filter(remoteServer -> remoteServer.getHostPort().equals(finalDevAddr))
                .collect(Collectors.toList());

            if (matchPortList.isEmpty()) {
                SparkCustomRule.log.error("选择的服务不存在: {}", finalDevAddr);
                return null;
            }
        }
        return server;

    }
}
