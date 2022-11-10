package info.spark.starter.es.entity.result;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 16:09
 * @since 1.7.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EsIndexResult extends EsBool {

    /** serialVersionUID */
    private static final long serialVersionUID = -1977552111613009847L;
    /** Shards acknowledged */
    @JsonProperty("shards_acknowledged")
    private Boolean shardsAcknowledged;
    /** Index */
    private String index;
}

