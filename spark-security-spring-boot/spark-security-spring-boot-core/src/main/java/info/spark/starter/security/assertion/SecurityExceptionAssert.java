package info.spark.starter.security.assertion;

import info.spark.starter.security.exception.SecurityException;
import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.assertion.IAssert;
import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:25
 * @see info.spark.starter.util.core.api.IResultCode
 * @see info.spark.starter.util.core.assertion.IAssert
 * @since 1.0.0
 */
public interface SecurityExceptionAssert extends IResultCode, IAssert {
    /** serialVersionUID */
    long serialVersionUID = 3077918845714343375L;

    /**
     * New exception base exception
     *
     * @param args args
     * @return the base exception
     * @since 1.0.0
     */
    @Override
    default BaseException newException(Object... args) {
        return new SecurityException(this, args, this.getMessage());
    }

    /**
     * New exception base exception
     *
     * @param t    t
     * @param args args
     * @return the base exception
     * @since 1.0.0
     */
    @Override
    default BaseException newException(Throwable t, Object... args) {
        return new SecurityException(this, args, this.getMessage(), t);
    }
}
