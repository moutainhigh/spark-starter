package info.spark.starter.id.populater;

import info.spark.starter.id.entity.Id;
import info.spark.starter.id.entity.IdMeta;
import info.spark.starter.id.enums.IdType;
import info.spark.starter.id.util.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>Description: 通过 CAS 无锁生成时间和序列号</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.24 16:25
 * @since 1.5.0
 */
public class AtomicIdPopulator implements IdPopulator, ResetPopulator {

    /**
         * <p>Description: CAS 必须保证安全的修改时间和序列号, 因此使用此数据结构对 2 者进行包装</p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.24 16:25
     * @since 1.5.0
     */
    static class Variant {

        /** Sequence */
        private long sequence = 0;
        /** Last timestamp */
        private long lastTimestamp = -1;

    }

    /** Variant */
    private AtomicReference<Variant> variant = new AtomicReference<>(new Variant());

    /**
     * Atomic id populator
     *
     * @since 1.5.0
     */
    public AtomicIdPopulator() {
        super();
    }

    /**
     * CAS 自旋锁实现安全的生成时间和序列号:
     * 1. 获取并保存原来的变量, 这个变量包含原来的时间和序列号字段;
     * 2. 基于原来的变量计算新的时间和序列号, 计算逻辑和 {@link LockIdPopulator} 与 {@link SyncIdPopulator} 一致;
     * 3. 计算后使用 CAS 操作更新原来的变量, 在更新过程中需要传递保存原来的变量;
     * 4. 吐过保存原来的变量被其他线程改变, 则需要重新拿到最新的变量并在此计算和尝试更新;
     *
     * @param id     id
     * @param idMeta id meta
     * @since 1.5.0
     */
    @Override
    public void populateId(@NotNull Id id, IdMeta idMeta) {
        Variant varOld, varNew;
        long timestamp, sequence;

        while (true) {
            varOld = this.variant.get();

            timestamp = TimeUtils.genTime(IdType.parse(id.getIdType()));
            TimeUtils.validateTimestamp(varOld.lastTimestamp, timestamp);

            sequence = varOld.sequence;

            if (timestamp == varOld.lastTimestamp) {
                sequence++;
                sequence &= idMeta.getSeqBitsMask();
                if (sequence == 0) {
                    timestamp = TimeUtils.tillNextTimeUnit(varOld.lastTimestamp, IdType.parse(id.getIdType()));
                }
            } else {
                sequence = 0;
            }

            // Assign the current variant by the atomic tools
            varNew = new Variant();
            varNew.sequence = sequence;
            varNew.lastTimestamp = timestamp;

            if (this.variant.compareAndSet(varOld, varNew)) {
                id.setSeq(sequence);
                id.setTime(timestamp);

                break;
            }

        }
    }

    /**
     * Reset
     *
     * @since 1.5.0
     */
    @Override
    public void reset() {
        this.variant = new AtomicReference<>(new Variant());
    }

}
