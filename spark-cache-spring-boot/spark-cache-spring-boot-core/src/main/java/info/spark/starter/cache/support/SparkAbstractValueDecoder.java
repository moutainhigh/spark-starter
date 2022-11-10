package info.spark.starter.cache.support;

import com.alicp.jetcache.support.AbstractValueDecoder;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.12 00:20
 * @since 1.0.0
 */
@Slf4j
abstract class SparkAbstractValueDecoder extends AbstractValueDecoder {

    /**
     * Spark abstract value decoder
     *
     * @param useIdentityNumber use identity number
     * @since 1.0.0
     */
    SparkAbstractValueDecoder(boolean useIdentityNumber) {
        super(useIdentityNumber);
    }

}
