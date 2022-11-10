package info.spark.agent.endpoint;

import info.spark.agent.constant.AgentConstant;
import info.spark.starter.basic.Result;
import info.spark.starter.util.core.api.R;
import info.spark.starter.endpoint.Endpoint;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: agent service 可用性检查接口  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.08 10:57
 * @since 1.5.0
 */
@Slf4j
@Endpoint
@ResponseBody
public class CheckEndpoint extends AbstractEndpoint {

    /** Discovery client */
    @Resource
    private DiscoveryClient discoveryClient;
    /** Composite discovery client */
    @Resource
    private CompositeDiscoveryClient compositeDiscoveryClient;

    /**
     * check api
     * curl --location --request GET 'http://127.0.0.1:18080/agent/check' \
     * --header 'Accept: application/json' \
     * --header 'Content-Type: application/json'
     *
     * @return the string
     * @since 1.0.0
     */
    @GetMapping(value = AgentConstant.ROOT_ENDPOINT + "/check",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Map<String, List<ServiceInstance>>> check() {
        Map<String, List<ServiceInstance>> instances = new HashMap<>(16);
        List<String> services = this.discoveryClient.getServices();
        services.forEach(s -> {
            List<ServiceInstance> list = this.discoveryClient.getInstances(s);
            instances.put(s, list);
        });

        List<DiscoveryClient> discoveryClients = this.compositeDiscoveryClient.getDiscoveryClients();
        log.info("[{} {}]", this.compositeDiscoveryClient, discoveryClients);

        return R.succeed(instances);
    }
}
