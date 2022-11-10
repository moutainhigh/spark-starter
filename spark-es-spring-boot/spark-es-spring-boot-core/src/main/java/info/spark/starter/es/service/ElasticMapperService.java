package info.spark.starter.es.service;

import info.spark.starter.es.entity.document.BaseElasticEntity;
import info.spark.starter.es.exception.EsErrorCodes;
import info.spark.starter.es.support.ElasticsearchUtils;

import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;

import java.lang.reflect.Method;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 15:03
 * @since 1.7.1
 */
@Slf4j
public class ElasticMapperService extends AbstractElasticService {

    /** serialVersionUID */
    private static final long serialVersionUID = -1145357063581385814L;

    /**
     * Search list
     *
     * @param index  index
     * @param method method
     * @param param  param
     * @param type   type
     * @return the list
     * @since 1.7.1
     */
    public List<? extends BaseElasticEntity> searchList(String index,
                                                        Method method,
                                                        Object param,
                                                        Class<? extends BaseElasticEntity> type) {
        ClientInterface client = this.getConfigClient(method);
        ESDatas<? extends BaseElasticEntity> esData = client.searchList(String.format("%s/_search", index),
                                                                         method.getName(),
                                                                         param,
                                                                         type);
        return esData.getDatas();
    }

    /**
     * Template
     *
     * @param templateName template name
     * @param method       method
     * @param param        param
     * @since 1.7.1
     */
    public void template(String templateName, Method method, Object param) {
        ClientInterface client = this.getConfigClient(method);
        try {
            client.createTempate(templateName, method.getName(), param);
        } catch (Exception e) {
            throw EsErrorCodes.TEMPLATE_INVOKE_ERROR.newException(e.getMessage());
        }
    }

    /**
     * Gets config client *
     *
     * @param method method
     * @return the config client
     * @since 1.7.1
     */
    private ClientInterface getConfigClient(Method method) {
        return this.getClient(ElasticsearchUtils.allMapperPath(method.getDeclaringClass()));
    }

}
