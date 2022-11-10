package info.spark.starter.es.service;

import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;

import java.io.Serializable;

import javax.annotation.Resource;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 19:35
 * @since 1.7.1
 */
@SuppressWarnings("serial")
public abstract class AbstractElasticService implements Serializable {

    /** B boss es starter */
    @Resource
    private BBossESStarter bossEsStarter;

    /**
     * Gets client *
     *
     * @return the client
     * @since 1.7.1
     */
    public ClientInterface getClient() {
        return this.bossEsStarter.getRestClient();
    }

    /**
     * Gets client *
     *
     * @param mapperPath mapper path
     * @return the client
     * @since 1.7.1
     */
    public ClientInterface getClient(String mapperPath) {
        return this.bossEsStarter.getConfigRestClient(mapperPath);
    }
}
