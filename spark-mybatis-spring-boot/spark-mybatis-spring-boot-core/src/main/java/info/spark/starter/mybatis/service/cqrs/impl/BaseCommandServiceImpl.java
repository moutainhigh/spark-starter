package info.spark.starter.mybatis.service.cqrs.impl;

import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.mybatis.service.cqrs.BaseCommandMapper;
import info.spark.starter.mybatis.service.cqrs.BaseCommandService;

import java.io.Serializable;

/**
 * <p>Description: </p>
 *
 * @param <DTO> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.28 02:00
 * @since 1.7.3
 */
public class BaseCommandServiceImpl<DTO extends BaseDTO<? extends Serializable>> implements BaseCommandService<DTO> {

    /**
     * Gets base mapper *
     *
     * @return the base mapper
     * @since 1.7.3
     */
    @Override
    public BaseCommandMapper<DTO> getBaseMapper() {
        return null;
    }

    /**
     * Save ignore
     *
     * @param entity entity
     * @return the boolean
     * @since 1.7.3
     */
    @Override
    public boolean saveIgnore(DTO entity) {
        return false;
    }
}
