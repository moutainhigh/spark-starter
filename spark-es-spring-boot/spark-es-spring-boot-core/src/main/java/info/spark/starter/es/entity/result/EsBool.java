package info.spark.starter.es.entity.result;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.20 15:12
 * @since 1.7.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsBool implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -2029860335627606216L;
    /** Acknowledged */
    private Boolean acknowledged;

    /** FALSE */
    public static final EsBool FALSE = new EsBool(false);
    /** TRUE */
    public static final EsBool TRUE = new EsBool(true);
}
