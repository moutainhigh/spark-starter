package info.spark.starter.es.entity.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.es.enums.ErrorStatus;
import info.spark.starter.es.enums.ErrorType;

import org.frameworkset.elasticsearch.ElasticSearchException;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: 封装bboss异常实体 </p>
 *
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.26 09:53
 * @since 1.8.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BossElasticException implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 5880034758780659288L;

    /** Error */
    private Error error;
    /** Status */
    private ErrorStatus status;

    /**
     * Parse exception
     *
     * @param e e
     * @return the b boss elastic exception
     * @since 1.8.0
     */
    public static BossElasticException parseException(ElasticSearchException e) {
        return JsonUtils.parse(e.getMessage(), BossElasticException.class);
    }

    /**
         * <p>Description: </p>
     *
     * @author wanghao
     * @version 1.8.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.01.26 09:57
     * @since 1.8.0
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Error implements Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = -4368915697215194902L;

        /** Reason */
        private String reason;
        /** Index uuid */
        @JsonProperty("index_uuid")
        private String indexUuid;
        /** Index */
        private String index;
        /** Type */
        private ErrorType type;
        /** Root cause */
        @JsonProperty("root_cause")
        private Error[] rootCause;
    }
}

