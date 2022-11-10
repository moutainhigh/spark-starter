package info.spark.starter.mybatis.service.cqrs;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

/**
 * <p>Description: </p>
 *
 * @param <DTO> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.28 01:53
 * @since 1.7.3
 */
public interface BaseCommandService<DTO> {
    /**
     * Gets base mapper *
     *
     * @return the base mapper
     * @since 1.7.3
     */
    BaseCommandMapper<DTO> getBaseMapper();

    /**
     * Save
     * todo-dong4j : (2021.02.28 02:14) [转换为 po]
     *
     * @param dto dto
     * @return the boolean
     * @since 1.7.3
     */
    default boolean save(DTO dto) {
        return SqlHelper.retBool(this.getBaseMapper().insert(dto));
    }

    /**
     * Save ignore
     *
     * @param entity entity
     * @return the boolean
     * @since 1.7.3
     */
    boolean saveIgnore(DTO entity);
}
