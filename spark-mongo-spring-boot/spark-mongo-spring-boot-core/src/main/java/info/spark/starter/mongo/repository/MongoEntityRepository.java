package info.spark.starter.mongo.repository;

import info.spark.starter.mongo.mapper.Model;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.*;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter 组件类型
 * @param <M> parameter 实体类型
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.17 13:49
 * @since 1.0.0
 */
public interface MongoEntityRepository<T extends Serializable, M extends Model<M>> extends MongoRepository<M, T> {

}
