package info.spark.starter.common.mapstruct;

import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.base.BasePO;
import info.spark.starter.common.base.BaseVO;

import java.io.Serializable;

/**
 * <p>Description:  </p>
 *
 * @param <V> parameter
 * @param <D> parameter
 * @param <P> parameter
 * @author dong4j
 * @version 1.6.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.30 16:56
 * @since 1.6.0
 */
@Deprecated
public interface BaseConverter<V extends BaseVO<? extends Serializable>,
    D extends BaseDTO<? extends Serializable>,
    P extends BasePO<? extends Serializable, P>> extends BaseWrapper<V, D, P> {
}
