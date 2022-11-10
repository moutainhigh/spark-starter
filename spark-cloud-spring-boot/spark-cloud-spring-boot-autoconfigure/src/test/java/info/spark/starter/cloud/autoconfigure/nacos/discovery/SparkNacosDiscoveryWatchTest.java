package info.spark.starter.cloud.autoconfigure.nacos.discovery;

import info.spark.starter.cloud.autoconfigure.nacos.SparkNacosProperties;
import info.spark.starter.cloud.autoconfigure.nacos.SparkNamingMaintainManager;

import org.junit.jupiter.api.Test;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.03 01:04
 * @since 1.5.0
 */
class SparkNacosDiscoveryWatchTest {

    @Test
    void test_destroy() {
        SparkNacosProperties nacosDiscoveryProperties = new SparkNacosProperties();
        SparkNacosProperties.Discovery discovery = new SparkNacosProperties.Discovery();
        discovery.setServerAddr("nacos.server");
        discovery.setNamespace("dong4j");
        nacosDiscoveryProperties.setDiscovery(discovery);
        SparkNacosDiscoveryWatch watch = new SparkNacosDiscoveryWatch(new SparkNamingMaintainManager(nacosDiscoveryProperties));
        watch.stop("sample-nacos-consumer");
    }

}
