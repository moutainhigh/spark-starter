package info.spark.starter.mybatis.service;

import info.spark.starter.common.base.AbstractBaseEntity;
import info.spark.starter.common.base.BasePO;
import info.spark.starter.common.base.ICrudDelegate;

/**
 * <p>Description: 实体转换 </p>
 *
 * @param <PO>  parameter
 * @param <DTO> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 19:07
 * @since 1.8.0
 */
public interface IExchangeService<PO extends BasePO<?, PO>, DTO extends AbstractBaseEntity<?>>
    extends ICrudDelegate<DTO>, BaseService<PO> {

}
