package info.spark.agent.adapter.config;

import org.springframework.web.client.RestTemplate;

/**
 * <p>Description: RestTemplate 定制接口 </p>
 *
 * @author dong4j
 * @version 1.0.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.07 00:14
 * @since 1.0.0
 */
public interface RestTemplateCustomizer {

    /**
     * 用于自定义 {@link RestTemplate} 实例的回调
     *
     * @param restTemplate rest template
     * @since 1.0.0
     */
    void customize(RestTemplate restTemplate);

}
