package info.spark.starter.rest.autoconfigure;

import org.springframework.web.client.RestTemplate;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.30 19:58
 * @since 2022.1.1
 */
public interface RestTemplateCustomizer {

    /**
     * Customize
     *
     * @param restTemplate rest template
     * @since 2022.1.1
     */
    void customize(RestTemplate restTemplate);

}
