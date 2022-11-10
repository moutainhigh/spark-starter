package info.spark.starter.es.entity.document;

import info.spark.starter.common.base.IBaseEntity;

import java.io.Serializable;


/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 18:43
 * @since 1.7.1
 */
@SuppressWarnings("serial")
public abstract class BaseElasticEntity<T extends Serializable> implements IBaseEntity<T> {

}
