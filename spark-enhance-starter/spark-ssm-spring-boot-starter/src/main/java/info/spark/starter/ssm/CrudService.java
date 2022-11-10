package info.spark.starter.ssm;

import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.mybatis.service.cqrs.BaseCommandService;

import java.io.Serializable;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.09.15 11:28
 * @since 2.0.0
 */
public interface CrudService<DTO extends BaseDTO<? extends Serializable>> extends BaseCommandService<DTO> {
}
