package info.spark.starter.id.populater;

import info.spark.starter.id.entity.Id;
import info.spark.starter.id.entity.IdMeta;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 根据时间和序列号填充 id </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.24 16:19
 * @since 1.5.0
 */
public interface IdPopulator {

    /**
     * Populate id
     *
     * @param id     id
     * @param idMeta id meta
     * @since 1.5.0
     */
    void populateId(@NotNull Id id, IdMeta idMeta);

}
