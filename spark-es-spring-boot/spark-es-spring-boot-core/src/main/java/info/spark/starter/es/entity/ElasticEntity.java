package info.spark.starter.es.entity;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.12 16:21
 * @since 1.5.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class ElasticEntity<T> {

    /** 主键标识, 用户 ES 持久化 */
    private String id;
    /** json 对象, 实际存储数据 */
    private Map<String, Object> data;
}
