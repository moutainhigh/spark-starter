package info.spark.starter.sms.assertion;

import info.spark.starter.util.core.api.IResultCode;
import info.spark.starter.util.core.assertion.IAssert;
import info.spark.starter.util.core.exception.BaseException;
import info.spark.starter.sms.exception.SmsException;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.01 10:22
 * @since 1.0.0
 */
public interface SmsExceptionAssert extends IResultCode, IAssert {
    /** serialVersionUID */
    long serialVersionUID = 3077918845714343375L;

    /**
     * New exceptions base exception.
     *
     * @param args the args
     * @return the base exception
     * @since 1.0.0
     */
    @Override
    default BaseException newException(Object... args) {
        return new SmsException(this, args, this.getMessage());
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
        return new SmsException(this, args, this.getMessage(), t);
    }
}
