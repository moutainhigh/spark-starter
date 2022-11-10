package info.spark.starter.cache.el;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.17 20:27
 * @since 1.6.0
 */
@Data
public class ExpressionRootObject {
    /** Object */
    private final Object object;

    /** Args */
    private final Object[] args;

    /**
     * Expression root object
     *
     * @param object object
     * @param args   args
     * @since 1.6.0
     */
    public ExpressionRootObject(Object object, Object[] args) {
        this.object = object;
        this.args = args;
    }
}
