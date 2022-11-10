package info.spark.starter.es.exception;

import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.assertion.IAssert;
import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.15 13:22
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public interface EsExceptionAssert extends IResultCode, IAssert {

    /**
     * New exceptions base exception.
     *
     * @param args the args
     * @return the base exception
     * @since 1.0.0
     */
    @Override
    default BaseException newException(Object... args) {
        return new EsException(this, args, this.getMessage());
    }

    /**
     * New exceptions base exception.
     *
     * @param t    the t
     * @param args the args
     * @return the base exception
     * @since 1.0.0
     */
    @Override
    default BaseException newException(Throwable t, Object... args) {
        return new EsException(this, args, this.getMessage(), t);
    }
}
