package info.spark.starter.captcha.assertion;

import info.spark.starter.captcha.exception.CaptchaException;
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
 * @see IResultCode
 * @see IAssert
 * @since 1.0.0
 */
public interface CaptchaExceptionAssert extends IResultCode, IAssert {
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
        return new CaptchaException(this, args, this.getMessage());
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
        return new CaptchaException(this, args, this.getMessage(), t);
    }
}
