package info.spark.starter.es.entity;

import org.frameworkset.elasticsearch.client.ClientInterface;

/**
 * <p>Description:  </p>
 *
 * @param <T> parameter
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.22 18:43
 * @since 1.7.1
 */
public interface DynamicMapperClient<T> {

    /**
     * Execute
     *
     * @param clientInterface client interface
     * @param templateName    template name
     * @return the object
     * @since 1.7.1
     */
    T execute(ClientInterface clientInterface, String templateName);

}
