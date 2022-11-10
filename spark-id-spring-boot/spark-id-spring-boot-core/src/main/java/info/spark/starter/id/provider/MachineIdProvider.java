package info.spark.starter.id.provider;

/**
 * <p>Description: 生成机器 id</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.24 16:23
 * @since 1.5.0
 */
public interface MachineIdProvider {
    /**
     * Gets machine id *
     *
     * @return the machine id
     * @since 1.5.0
     */
    long getMachineId();
}
