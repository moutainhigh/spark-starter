package info.spark.starter.id.populater;

import info.spark.starter.id.entity.Id;
import info.spark.starter.id.entity.IdMeta;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 通过 synchronized 生成时间和序列号</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.24 16:21
 * @since 1.5.0
 */
public class SyncIdPopulator extends BasePopulator {

    /**
     * Sync id populator
     *
     * @since 1.5.0
     */
    public SyncIdPopulator() {
        super();
    }

    /**
     * Populate id
     *
     * @param id     id
     * @param idMeta id meta
     * @since 1.5.0
     */
    @Override
    public synchronized void populateId(@NotNull Id id, IdMeta idMeta) {
        super.populateId(id, idMeta);
    }

}
