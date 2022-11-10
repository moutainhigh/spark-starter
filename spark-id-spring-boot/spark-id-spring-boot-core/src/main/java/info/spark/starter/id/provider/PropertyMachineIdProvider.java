package info.spark.starter.id.provider;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Description: 基于配置生成 machineId, 需要在部署的每一台机器上配置不同的机器号</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.24 16:24
 * @since 1.5.0
 */
public class PropertyMachineIdProvider implements MachineIdProvider {
    /** Machine id */
    @Getter
    @Setter
    private long machineId;
}
