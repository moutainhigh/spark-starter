package info.spark.starter.feign.autoconfigure;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.netflix.loadbalancer.IRule;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.feign.autoconfigure.rule.SparkCustomRule;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: feign 客户端增强 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:08
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FeignClientProperties.class)
public class FeignClientAutoConfiguration implements SparkAutoConfiguration {

    /**
     * todo-dong4j : (2019年10月15日 21:01) [暂时注释 后期优化]
     *
     * @param feignClientProperties the feign client properties
     * @return the rule
     * @since 1.0.0
     */
    @ConditionalOnClass(NacosDiscoveryProperties.class)
    public IRule sparkCustomRule(FeignClientProperties feignClientProperties) {
        if (ConfigKit.isLocalLaunch()) {
            log.warn("当前为本地开发环境, 使用自定义负载规则: {}", SparkCustomRule.class.getName());
            return new SparkCustomRule(feignClientProperties);
        }
        log.warn("当前非本地开发环境 {}, 使用 Nacos 负载规则: {}", ConfigKit.getEnv().getName(), NacosRule.class.getName());
        return new NacosRule();
    }
}
