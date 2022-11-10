package info.spark.starter.cache.exception;

import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.exception.BaseException;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.28 16:28
 * @since 1.7.0
 */
@SuppressWarnings("java:S110")
public class CacheLockException extends BaseException {

    private static final long serialVersionUID = 2157282953672508633L;

    /**
     * Cache lock exception
     *
     * @param msg msg
     * @since 1.6.0
     */
    public CacheLockException(String msg) {
        super(msg);
        this.resultCode = BaseCodes.SERVER_INNER_ERROR;
    }
}
