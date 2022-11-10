package info.spark.starter.mybatis.service.cqrs;

import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.base.BaseQuery;

import java.io.Serializable;

/**
 * <p>Description: </p>
 *
 * @param <DTO> parameter
 * @param <Q>   parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.28 01:53
 * @since 1.7.3
 */
public interface BaseQueryService<DTO extends BaseDTO<? extends Serializable>,
    Q extends BaseQuery<? extends Serializable>> {

    /**
     * Gets base mapper *
     *
     * @return the base mapper
     * @since 1.7.3
     */
    BaseQueryMapper<DTO> getBaseMapper();

    /**
     * Select by id
     * <p>
     * todo-dong4j : (2021.02.28 02:12) [转换为 dto]
     *
     * @param id id
     * @return the dto
     * @since 1.7.3
     */
    default DTO selectById(Serializable id) {
        return this.getBaseMapper().selectById(id);
    }
}
