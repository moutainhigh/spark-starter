package info.spark.starter.openness.exception;

import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.assertion.IAssert;
import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description: </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.08.19 17:55
 * @since 1.9.0
 */
@SuppressWarnings("serial")
public interface OpennessExceptionAssert extends IResultCode, IAssert {

    /**
     * New exception
     *
     * @param args args
     * @return the base exception
     * @since 1.9.0
     */
    @Override
    default BaseException newException(Object... args) {
        return new OpennessException(this, args, this.getMessage());
    }

    /**
     * New exception
     *
     * @param t    t
     * @param args args
     * @return the base exception
     * @since 1.9.0
     */
    @Override
    default BaseException newException(Throwable t, Object... args) {
        return new OpennessException(this, args, this.getMessage(), t);
    }
}
