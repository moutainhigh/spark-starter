package info.spark.starter.mongo.enums;

import org.jetbrains.annotations.Contract;

/**
 * <p>Description: ${description}</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.03 11:44
 * @since 1.0.0
 */
public enum Order {
    /** Desc order */
    desc(-1),
    /** Asc order */
    asc(1);

    /** Order value */
    private final int orderValue;

    /**
     * Order
     *
     * @param orderValue order value
     * @since 1.0.0
     */
    @Contract(pure = true)
    Order(int orderValue) {
        this.orderValue = orderValue;
    }

    /**
     * Gets order value *
     *
     * @return the order value
     * @since 1.0.0
     */
    @Contract(pure = true)
    public int getOrderValue() {
        return orderValue;
    }
}
