package info.spark.starter.mongo.enums;

import info.spark.starter.mongo.exception.QueryException;

import org.springframework.data.mongodb.core.query.Criteria;

/**
 * <p>Description: ${description}</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.03 11:43
 * @since 1.0.0
 */
public enum Between {
    /** Eq between */
    EQ,
    /** Neq between */
    NEQ,
    /** Feq between */
    FEQ,
    /** Eeq between */
    EEQ;

    /**
     * Between *
     *
     * @param criteria criteria
     * @param begin    begin
     * @param end      end
     * @since 1.0.0
     */
    public void between(Criteria criteria, Object begin, Object end) {
        switch (this) {
            case EQ:
                criteria.lte(end).gte(begin);
                break;
            case NEQ:
                criteria.lt(end).gt(begin);
                break;
            case FEQ:
                criteria.lt(end).gte(begin);
                break;
            case EEQ:
                criteria.lte(end).gt(begin);
                break;
            default:
                throw new QueryException("no Between enum");
        }
    }
}
