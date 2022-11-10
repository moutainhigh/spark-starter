package info.spark.starter.endpoint.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.endpoint.initialization.InitializationService;
import info.spark.starter.endpoint.initialization.PreloadComponent;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 12:08
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class EndpointAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Preload component
     *
     * @param initializationService initialization service
     * @return the preload component
     * @since 2022.1.1
     */
    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public PreloadComponent preloadComponent(InitializationService initializationService) {
        return new PreloadComponent(initializationService);
    }
}
