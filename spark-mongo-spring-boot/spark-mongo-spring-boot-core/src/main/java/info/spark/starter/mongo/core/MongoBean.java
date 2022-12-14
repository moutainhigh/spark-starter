package info.spark.starter.mongo.core;

import org.springframework.data.mongodb.core.MongoTemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: ${description}</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.03 11:49
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MongoBean {
    /** Collection name */
    private String collectionName;
    /** Desc */
    private String desc;
    /** Mongo template */
    private MongoTemplate mongoTemplate;
}
