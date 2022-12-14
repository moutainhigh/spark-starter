package info.spark.starter.mongo.entity;

import info.spark.starter.mongo.annotation.MongoCollection;
import info.spark.starter.mongo.annotation.MongoColumn;

import org.springframework.data.annotation.Id;

import lombok.Data;

/**
 * <p>Description: mongodb id 自增序列实体 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.17 14:24
 * @since 1.0.0
 */
@Data
@MongoCollection(value = "sequence")
public class Sequence {

    /** SEQ_ID */
    public static final String SEQ_ID = "seq_id";
    /** COLLECTION_NAME */
    public static final String COLLECTION_NAME = "collection_name";

    /** 主键 */
    @Id
    private String id;
    /** 当前自增id值 */
    @MongoColumn(name = SEQ_ID)
    private Long seqId;
    /** 需要自增 id 的集合名称 */
    @MongoColumn(name = COLLECTION_NAME)
    private String collectionName;
}
